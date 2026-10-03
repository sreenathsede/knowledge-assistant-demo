package com.sreenath.knowledge.model;

import java.time.LocalDate;
import java.util.Set;

public record KnowledgeDocument(
        String id,
        String title,
        String type,
        String version,
        LocalDate effectiveFrom,
        Set<String> allowedGroups,
        String content) {

    public boolean accessibleTo(Set<String> groups) {
        return groups != null && groups.stream().anyMatch(allowedGroups::contains);
    }

    public boolean activeOn(LocalDate date) {
        return !effectiveFrom.isAfter(date);
    }
}
