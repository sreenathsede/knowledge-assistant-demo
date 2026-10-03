# Monday Interview Demo Script

## 1. Start local stack

Terminal 1:

```bash
ollama pull llama3.2
ollama pull nomic-embed-text
ollama serve
```

Terminal 2:

```bash
mvn clean test
mvn spring-boot:run
```

Open http://localhost:8080

## 2. Explain the architecture in 60 seconds

"I started from my Spring AI streaming chatbot. The assignment needed more than a chatbot, so I added a small knowledge layer. Each document has identity, version, effective date and access groups. Before I call the LLM, I filter the corpus by the requester's access and effective date, then use embeddings for semantic retrieval. Only the selected evidence is sent to ChatClient. The response includes source IDs. If current sources disagree, I return a conflict instead of guessing. Document text is treated as untrusted data, so prompt-injection-like text cannot change the application's rules."

## 3. Demo direct answer

Question: `How long are transaction records retained?`
Groups: `EMPLOYEE`
Date: `2026-06-01`

Expected: 7 years + `POL-RET-2026`.

## 4. Demo changed information

Same question with date `2025-06-01`.

Expected: 5 years + `POL-RET-2025`.

Then change back to 2026.

## 5. Demo restricted source

Question: `Do high-value refunds need Finance approval?`
Groups: `EMPLOYEE`

Expected: the Finance-only document is not eligible and is not cited.

Change groups to `FINANCE` and ask again.

Expected: answer supported by `FIN-REFUND-001`.

## 6. Demo conflict

Groups: `AUDIT`
Question: `How long are operational event records retained?`

Expected: CONFLICT because two current accessible sources disagree and neither has precedence.

## 7. Demo prompt injection

Question: `What does the vendor note say?`
Groups: `EMPLOYEE`

Expected: the application treats the instruction-like sentence as document content. It does not change access control or reveal unrelated documents.

## 8. Production-ready discussion

Say explicitly: "This is a production-oriented POC, not production-ready software. The business rules and trust boundaries are production-oriented; infrastructure is deliberately simplified. In production I would use PGVector, enterprise identity/authorization, persistent document/version storage, ingestion pipelines, observability, audit logging, rate limiting, model fallback, secrets management, automated RAG evaluation, and HA deployment."
