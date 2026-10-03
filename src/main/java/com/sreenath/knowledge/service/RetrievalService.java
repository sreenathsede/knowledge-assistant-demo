package com.sreenath.knowledge.service;

import com.sreenath.knowledge.model.KnowledgeDocument;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class RetrievalService {
    private final DocumentRepository repository;
    private final EmbeddingModel embeddingModel;

    public RetrievalService(DocumentRepository repository, EmbeddingModel embeddingModel) {
        this.repository = repository;
        this.embeddingModel = embeddingModel;
    }

    public RetrievalResult retrieve(String question, Set<String> access, LocalDate date, int topK) {
        List<KnowledgeDocument> eligible = repository.eligible(access, date);
        // Deterministic version precedence: for the same logical title, keep the newest source
        // effective on the requested date. Distinct titles are retained so true conflicts can surface.
        Map<String, KnowledgeDocument> latestByTitle = new LinkedHashMap<>();
        for (KnowledgeDocument d : eligible) {
            latestByTitle.merge(d.title(), d, (a, b) -> b.effectiveFrom().isAfter(a.effectiveFrom()) ? b : a);
        }
        eligible = new ArrayList<>(latestByTitle.values());
        if (eligible.isEmpty()) return new RetrievalResult(List.of(), false, "No accessible documents.");
        float[] q = embeddingModel.embed(question);
        List<Scored> scored = new ArrayList<>();
        for (KnowledgeDocument d : eligible) {
            float[] v = embeddingModel.embed(d.title() + "\n" + d.content());
            scored.add(new Scored(d, cosine(q, v)));
        }
        scored.sort(Comparator.comparingDouble(Scored::score).reversed());
        List<KnowledgeDocument> result = scored.stream().limit(topK).filter(s -> s.score() >= 0.35).map(Scored::doc).toList();
        return new RetrievalResult(result, true, "Eligible=" + eligible.size() + ", retrieved=" + result.size());
    }

    private double cosine(float[] a, float[] b) {
        double dot = 0, na = 0, nb = 0;
        int n = Math.min(a.length, b.length);
        for (int i = 0; i < n; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        return (na == 0 || nb == 0) ? 0 : dot / (Math.sqrt(na) * Math.sqrt(nb));
    }

    public record Scored(KnowledgeDocument doc, double score) {
    }

    public record RetrievalResult(List<KnowledgeDocument> documents, boolean dependencyAvailable, String summary) {
    }
}
