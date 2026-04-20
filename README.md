# spring-core-template

A Spring Boot service template with feature-first clean architecture. Designed to work alongside
[spring-identity-template](https://github.com/wolf-361/spring-identity-template) — it trusts JWTs
issued by the identity service rather than issuing its own.

## Architecture

Vertical slices per feature, each with explicit use case / command / result layers:

```
core/                        ← shared cross-cutting concerns
  config/                    ← AppProperties (cors, jwt, identity client URL)
  clients/identity/          ← IdentityClient (look up user details by ID)
  exception/                 ← GlobalExceptionHandler + ErrorResponse
  jwt/                       ← JwtValidator + JwtValidationFilter
  security/                  ← SecurityConfig (stateless, JWT-authenticated)
  web/filter/                ← CorrelationIdFilter, RequestLoggingFilter

features/
  {feature}/
    domain/
      model/                 ← JPA entities + domain logic
      exception/             ← feature-specific exceptions
    application/
      usecase/               ← one class per operation
      command/               ← explicit input objects
      result/                ← explicit output objects
      repository/            ← interface (no JPA leaking out)
    infrastructure/
      persistence/           ← JpaRepository + adapter impl
      scheduling/            ← scheduled tasks for this feature
      seed/                  ← dev-only data seeder
      web/
        controller/
        dto/                 ← request + response DTOs
        mapper/              ← maps between DTOs and commands/results
```

The `item` feature is a working skeleton that demonstrates the full pattern.
**Delete or replace it** once you have used it as a reference.

## Getting started

### 1. Clone and initialize

```bash
git clone https://github.com/your-org/spring-core-template.git my-service
cd my-service
bash scripts/init.sh
```

`init.sh` renames the package, updates Gradle files, installs the ktlint pre-commit hook
(with auto-format), and removes itself.

### 2. Configure

```bash
cp .env.example .env
# Fill in DB_*, JWT_SECRET (must match the identity service), IDENTITY_SERVICE_URL
```

### 3. Start Postgres

```bash
docker compose up -d
```

### 4. Run

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

Swagger UI is available at `http://localhost:8080/swagger-ui.html` in dev mode.
Actuator (health, prometheus) runs on port `8081`.

## JWT authentication

This service validates tokens issued by the identity service using a shared HMAC secret
(`JWT_SECRET`). It does **not** issue tokens. The authenticated user's UUID is available
in controllers via `@AuthenticationPrincipal UUID userId`.

To look up additional user details (name, email), inject `IdentityClient` and call
`getUserById(userId, bearerToken)`.

## Adding a feature

1. Create `features/{name}/` following the `item` skeleton structure
2. Add a Flyway migration in `src/main/resources/db/migration/`
3. Register feature exceptions in `GlobalExceptionHandler`
4. Write tests: domain model, mapper, use cases

## Running tests

```bash
./gradlew test
```

## Code style

ktlint enforces style at commit time (auto-formats staged files, then checks).
To run manually:

```bash
./gradlew ktlintFormat   # fix
./gradlew ktlintCheck    # check only
```
