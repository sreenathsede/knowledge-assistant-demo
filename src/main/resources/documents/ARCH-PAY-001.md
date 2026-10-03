---
id: ARCH-PAY-001
title: Payment Architecture Notes
type: architecture
version: v1
effectiveFrom: 2026-02-15
allowedGroups: EMPLOYEE,ARCHITECT
---
The payment flow is API Gateway -> Payment Service -> Kafka -> Ledger Service. Kafka provides asynchronous decoupling between payment processing and ledger posting.
