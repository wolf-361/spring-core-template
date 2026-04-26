# ADR-0003: Generic External Error Messages

## Status

Accepted — 2026-04

## Context

Error messages returned to clients are a security surface. Specific messages leak information that attackers can weaponize:

- `"User not found"` vs `"Incorrect password"` tells an attacker which accounts exist — enabling user enumeration.
- `"Account is locked"` vs `"Account is inactive"` reveals internal account state.

At the same time, *developers* need detailed error information to debug production issues — vague "something went wrong" logs are useless during incidents.

## Decision

I implemented a **two-tier error system**:

- **Internally**, exceptions are specific. The `ApplicationException` sealed hierarchy distinguishes between distinct failure reasons. Logs contain the full detail, stack trace, and correlation ID.
- **Externally**, error responses use:
    - A **generic message** that does not leak internal state.
    - A **stable error code** that clients can branch on for UX purposes, without revealing internal reasons.

Mapping is centralized in `GlobalExceptionHandler`.

### Error response shape

```json
{
  "code": "NOT_FOUND",
  "message": "Resource not found",
  "timestamp": "2026-04-18T14:30:00Z",
  "correlationId": "abc-123"
}
```

The `code` is stable and safe for client-side branching. The `message` is user-displayable but intentionally vague.

## Consequences

### Positive

- **No internal state leakage** through error messages.
- **Developers keep signal.** Logs are specific, include correlation IDs, and let support and on-call engineers find the real cause quickly.
- **Clients have stable branching.** The `code` field is part of the public contract. Clients can handle distinct error types without needing specific internal reasons.

### Negative / Tradeoffs

- **Developers must resist the urge** to "just return a better message" when a support ticket comes in. The discipline is: fix the log, not the response.

### Log discipline (non-negotiable)

These values are **never** logged, under any circumstances:

- Plaintext passwords or secrets
- Access tokens or session tokens
- Full PII bodies

Correlation IDs and user IDs are always safe to log and **should** appear in every log line related to a request.

## Alternatives Considered

### Specific user-facing error messages

Partially rejected — fine for validation errors (field-level feedback is expected). Not acceptable for security-sensitive paths where revealing state helps attackers.

### Only generic messages, no error codes

Rejected. Clients legitimately need to distinguish error types for UX. The `code` field provides this without leaking state.
