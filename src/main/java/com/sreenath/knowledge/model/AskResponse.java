package com.sreenath.knowledge.model;

import java.util.List;

public record AskResponse(String answer, String status, List<SourceReference> sources, String retrievalSummary) {}
