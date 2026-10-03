package com.sreenath.knowledge.service;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class AccessAndDateRulesTest {
    private final DocumentRepository repo = new DocumentRepository();

    @Test void 2025GetsOldPolicy() {
        var ids = repo.eligible(Set.of("EMPLOYEE"), LocalDate.of(2025, 6, 1)).stream().map(d -> d.id()).toList();
        assertTrue(ids.contains("POL-RET-2025"));
    }

    @Test void 2026GetsNewPolicy() {
        var docs = repo.eligible(Set.of("EMPLOYEE"), LocalDate.of(2026, 6, 1));
        var policy = docs.stream().filter(d -> d.title().equals("Transaction Retention Policy")).findFirst().orElseThrow();
        assertEquals("POL-RET-2026", policy.id());
    }

    @Test void unauthorizedFinanceSourceIsExcluded() {
        var ids = repo.eligible(Set.of("EMPLOYEE"), LocalDate.of(2026, 6, 1)).stream().map(d -> d.id()).toList();
        assertFalse(ids.contains("FIN-REFUND-001"));
    }

    @Test void authorizedFinanceSourceIsAvailable() {
        var ids = repo.eligible(Set.of("FINANCE"), LocalDate.of(2026, 6, 1)).stream().map(d -> d.id()).toList();
        assertTrue(ids.contains("FIN-REFUND-001"));
    }
}
