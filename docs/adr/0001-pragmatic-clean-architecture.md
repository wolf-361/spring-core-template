# ADR-0001: Pragmatic Clean Architecture with Use Cases

## Status

Accepted — 2026-04

## Context

This template needs an architecture that is:

- **Rigorous enough** to scale across multiple services and teams without turning into a mud-ball.
- **Light enough** to avoid the over-engineering trap where every feature takes three layers of ceremony.
- **Recognizable** to any Spring Kotlin developer who joins the project.

I had previously built services using NestJS with a standard layered structure (`controller → service → repository`). That worked, but service classes grew into 400-line objects holding 12 loosely related methods. Testing required mocking half the codebase.

I wanted the benefits of Clean Architecture (inverted dependencies, testable core) without the ceremony of textbook Hexagonal (Ports & Adapters terminology, mandatory separation of JPA entities from domain entities, domain interfaces on everything).

## Decision

I adopted a **three-layer Clean Architecture** with **feature-first organization** at the top level:

- `features/<name>/domain/` — business entities and invariants
- `features/<name>/application/` — use cases + interfaces for anything the use case doesn't own
- `features/<name>/infrastructure/` — all adapters (controllers, persistence, external clients)

Cross-cutting concerns (security, JWT, web filters, config) live in `core/` at the top level.

With these specific choices:

1. **Use cases, not services.** One class per action. Public method is always `execute(command): result`.
2. **Lightweight interface pattern.** Interfaces live in `application/`, implementations in `infrastructure/`. I don't use "Port" / "Adapter" naming — the folder location conveys the role.
3. **Feature-first at the top level.** This template is designed for multi-domain services where organizing by feature (`item`, `order`, `notification`) is cleaner than a flat `domain/application/infrastructure` split across unrelated concerns.

## Consequences

### Positive

- **Testing is trivial.** Each use case has 2–4 dependencies. Mock them, test one method.
- **Responsibility is obvious.** If you're adding an endpoint, you add a use case. No guessing where logic belongs.
- **Use cases compose.** A complex flow can orchestrate multiple use cases in a controller or a higher-level use case, without fat-service cross-coupling.
- **Inverted dependencies.** Domain and application don't import Spring (beyond the narrow JPA tradeoff in ADR-0002). Swapping Postgres for DynamoDB, or Spring for Ktor, only touches `infrastructure/`.

### Negative / Tradeoffs

- **More files.** More use cases vs a traditional 2-class service layout. I consider this a feature — file count maps to endpoint count, which is a legitimate measure of app size.
- **Boilerplate for small actions.** A one-line use case still needs a `Command`, `Result`, and `execute()` method. I accept this — consistency beats the occasional shortcut.
- **Cross-use-case reuse** needs a shared private service injected into both — not a use case calling another use case.

## Alternatives Considered

### Traditional layered architecture (Controller / Service / Repository)

Rejected. Previous experience showed service classes grow into unmaintainable god objects. Test setup becomes painful. Forces developers to mentally parse which methods of a service belong together.

### Full Hexagonal / Ports & Adapters with strict port/adapter naming

Rejected. Adds ceremony without proportional benefit. Renaming interfaces `UserRepositoryPort` and implementations `UserRepositoryAdapter` is vocabulary overhead, not architectural clarity. The folder location (`application/` vs `infrastructure/`) already conveys the role.

### Flat (non-feature-first) structure

Rejected for a multi-domain service. When a service has multiple unrelated domains (items, orders, notifications), a flat structure puts unrelated concerns in the same `domain/` and `application/` folders. Feature-first makes it obvious where everything belonging to `item` lives.
