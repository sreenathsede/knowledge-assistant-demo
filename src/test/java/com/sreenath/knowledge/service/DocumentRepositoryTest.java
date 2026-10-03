package com.sreenath.knowledge.service;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class DocumentRepositoryTest {
    private final DocumentRepository repo = new DocumentRepository();
    @Test void newerPolicyAppliesFor2026(){var ids=repo.eligible(Set.of("EMPLOYEE"), LocalDate.of(2026,6,1)).stream().map(d->d.id()).toList();assertTrue(ids.contains("POL-RET-2026"));}
    @Test void restrictedFinanceDocumentIsNotEligibleForEmployee(){var ids=repo.eligible(Set.of("EMPLOYEE"), LocalDate.of(2026,6,1)).stream().map(d->d.id()).toList();assertFalse(ids.contains("FIN-REFUND-001"));}
    @Test void financeDocumentIsEligibleForFinance(){var ids=repo.eligible(Set.of("FINANCE"), LocalDate.of(2026,6,1)).stream().map(d->d.id()).toList();assertTrue(ids.contains("FIN-REFUND-001"));}
}
