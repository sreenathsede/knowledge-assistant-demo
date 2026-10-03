package com.sreenath.knowledge.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.Set;

public record AskRequest(
        @NotBlank(message = "question must not be blank") String question,
        @NotEmpty(message = "userAccess must contain at least one group") Set<String> userAccess,
        LocalDate effectiveDate) {
}
