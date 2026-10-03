---
id: OPS-PAY-001
title: Payment Operations Guide
type: operations
version: v3
effectiveFrom: 2026-02-01
allowedGroups: EMPLOYEE,OPS
---
For a payment stuck in PROCESSING, first check the payment status API. If status is unknown, check the message broker consumer lag. Escalate to Payments L2 after both checks.
