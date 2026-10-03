package com.sreenath.knowledge.service;

import com.sreenath.knowledge.model.KnowledgeDocument;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

/**
 * Simple classpath document repository for the interview POC.
 * <p>
 * Documents live under src/main/resources/documents/*.md so the reviewer can
 * inspect/edit the synthetic corpus without changing Java code.
 */
@Repository
public class DocumentRepository {
    private final List<KnowledgeDocument> documents;

    public DocumentRepository() {
        this.documents = loadDocuments();
    }

    private List<KnowledgeDocument> loadDocuments() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:documents/*.md");
            List<KnowledgeDocument> result = new ArrayList<>();
            for (Resource resource : resources) {
                result.add(parse(resource));
            }
            if (result.isEmpty()) {
                throw new IllegalStateException("No knowledge documents found under classpath:/documents/");
            }
            return List.copyOf(result);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load knowledge documents", e);
        }
    }

    private KnowledgeDocument parse(Resource resource) throws IOException {
        String raw = resource.getContentAsString(StandardCharsets.UTF_8).trim();

        if (!raw.startsWith("---")) {
            throw new IllegalArgumentException(
                    "Invalid document format: " + resource.getFilename()
                            + ". Document must start with '---'."
            );
        }

        int secondDelimiter = raw.indexOf("\n---", 3);

        if (secondDelimiter == -1) {
            throw new IllegalArgumentException(
                    "Invalid document format: " + resource.getFilename()
                            + ". Missing closing '---'."
            );
        }

        String metadataBlock = raw.substring(3, secondDelimiter).trim();
        String content = raw.substring(secondDelimiter + 4).trim();

        Map<String, String> metadata = new LinkedHashMap<>();

        for (String line : metadataBlock.split("\\R")) {
            if (line.isBlank() || !line.contains(":")) {
                continue;
            }

            int colon = line.indexOf(':');

            String key = line.substring(0, colon).trim();
            String value = line.substring(colon + 1).trim();

            metadata.put(key, value);
        }

        return new KnowledgeDocument(
                required(metadata, "id", resource),
                required(metadata, "title", resource),
                required(metadata, "type", resource),
                required(metadata, "version", resource),
                LocalDate.parse(required(metadata, "effectiveFrom", resource)),
                parseGroups(required(metadata, "allowedGroups", resource)),
                content
        );
    }

    private String required(Map<String, String> metadata, String key, Resource resource) {
        String value = metadata.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing '" + key + "' in " + resource.getFilename());
        }
        return value;
    }

    private Set<String> parseGroups(String value) {
        Set<String> groups = new LinkedHashSet<>();
        for (String group : value.split(",")) {
            if (!group.isBlank()) groups.add(group.trim().toUpperCase(Locale.ROOT));
        }
        return Set.copyOf(groups);
    }

    public List<KnowledgeDocument> all() {
        return documents;
    }

    public List<KnowledgeDocument> eligible(Set<String> access, LocalDate date) {
        Set<String> normalized = access == null ? Set.of() : access.stream()
                .filter(Objects::nonNull)
                .map(g -> g.trim().toUpperCase(Locale.ROOT))
                .collect(java.util.stream.Collectors.toSet());
        return documents.stream()
                .filter(d -> d.accessibleTo(normalized))
                .filter(d -> d.activeOn(date))
                .toList();
    }
}
