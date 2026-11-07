# Architecture Decision Records (ADRs) — FleetSafe Platform

Each record states one decision taken for the FleetSafe RESTful API, why it was taken and what it costs.

## Index

- [ADR-001: Modular Monolith with Layered Domain-Driven Design](#adr-001-modular-monolith-with-layered-domain-driven-design)
- [ADR-002: Command Query Responsibility Segregation (CQRS)](#adr-002-command-query-responsibility-segregation-cqrs)
- [ADR-003: Domain Model Decoupled from JPA Persistence](#adr-003-domain-model-decoupled-from-jpa-persistence)
- [ADR-004: Sealed Result Type with Typed Failures per Use Case](#adr-004-sealed-result-type-with-typed-failures-per-use-case)
- [ADR-005: References Across Bounded Contexts by UUID Identity](#adr-005-references-across-bounded-contexts-by-uuid-identity)
- [ADR-006: Stateless JWT Authentication Scoped to a Company](#adr-006-stateless-jwt-authentication-scoped-to-a-company)
- [ADR-007: Localized Error Responses as RFC 9457 Problem Details](#adr-007-localized-error-responses-as-rfc-9457-problem-details)
- [ADR-008: Domain Event Dispatching through DomainEventPublisher](#adr-008-domain-event-dispatching-through-domaineventpublisher)
- [ADR-009: Virtual Threads for Request Handling](#adr-009-virtual-threads-for-request-handling)
- [ADR-010: Development and Production Profiles](#adr-010-development-and-production-profiles)
- [ADR-011: Contract-First OpenAPI Documentation](#adr-011-contract-first-openapi-documentation)
- [ADR-012: Container Delivery to Azure Container Apps](#adr-012-container-delivery-to-azure-container-apps)

---

## ADR-001: Modular Monolith with Layered Domain-Driven Design

### Status
Accepted

### Context
FleetSafe covers six bounded contexts with different owners and rates of change: Identity and Access, Fleet Management, Vehicle Documentation, Pre-Operational Inspection, Evaluation and Authorization, and Incident Management. The team needs clear boundaries between them without the operational cost of one service per context.

### Decision
1. Deploy a single Spring Boot application.
2. Give each bounded context its own package under `com.bitmeisters.fleetsafe.platform` (`iam`, `evaluation`, `documentation`, …), plus `shared` for the shared kernel.
3. Split every context into four layers: `domain`, `application`, `infrastructure` and `interfaces`.
4. Name infrastructure packages by technology type and technology, for example `infrastructure.tokens.jwt` and `infrastructure.hashing.bcrypt`.

### Consequences
- **Positive**: one build and one deployment; each context can be developed and defended by its owner.
- **Negative**: boundaries are kept by convention, not by the compiler; a careless import can couple two contexts.

---

## ADR-002: Command Query Responsibility Segregation (CQRS)

### Status
Accepted

### Context
Use cases that change state enforce business rules; use cases that read state only filter and project. Mixing both in one service hides which operations have side effects.

### Decision
1. Model every state change as a command record handled by a `*CommandService`.
2. Model every read as a query record handled by a `*QueryService`.
3. Keep the service interfaces in `application.commandservices` and `application.queryservices`, and their implementations in `application.internal`.

### Consequences
- **Positive**: side effects are explicit in the type of each operation.
- **Negative**: more small classes per use case.

---

## ADR-003: Domain Model Decoupled from JPA Persistence

### Status
Accepted

### Context
Annotating aggregates with JPA forces public setters and no-argument constructors, which let any caller bypass the domain rules.

### Decision
1. Keep aggregates free of JPA annotations and setters; state changes go through domain methods.
2. Map each aggregate to a `*PersistenceEntity` in `infrastructure.persistence.jpa.entities`.
3. Convert between both with a `*PersistenceAssembler`, used only by the repository adapter (`*RepositoryImpl`) that implements the domain repository interface.
4. Rebuild an aggregate from the database with a static `restore(...)` factory that sets the stored state without running creation rules or registering events.

### Consequences
- **Positive**: aggregates protect their invariants; the domain does not depend on the database.
- **Negative**: every aggregate needs a persistence entity, an assembler and a `restore(...)` factory listing all its fields.

---

## ADR-004: Sealed Result Type with Typed Failures per Use Case

### Status
Accepted

### Context
Expected failures, such as an email already registered or a rule not found, are part of the business flow. Throwing exceptions for them hides them from method signatures. The reference platform uses a generic `ApplicationError` record; the course's `catch-up-platform` uses sealed failure types.

### Decision
1. Return `Result<T, F>` from command and query services, a sealed interface with `Success` and `Failure`.
2. Declare `F` per use case as a sealed interface of records, for example `UserCommandFailure` with `EmailAlreadyRegistered`, `NotFound` and `InvalidCredentials`.
3. Give every failure a `messageKey()` that names its translated message.
4. Turn a result into an HTTP response with a `ResponseEntityFrom*ResultAssembler` per context, which picks the status with an exhaustive `switch`.
5. Do not adopt `ApplicationError`.

### Consequences
- **Positive**: the compiler lists every failure a use case can return and checks that each one is mapped to a status.
- **Negative**: one result assembler per context instead of a single shared one.

---

## ADR-005: References Across Bounded Contexts by UUID Identity

### Status
Accepted

### Context
An evaluation needs the vehicle and the inspection, which belong to other contexts. Holding their objects would couple the contexts and their transactions.

### Decision
1. Identify every aggregate with a `UUID` generated on persistence (`GenerationType.UUID`).
2. Inside a context, relate tables with foreign keys.
3. Across contexts, store only the identifier, wrapped in a value object of the referencing context, for example `VehicleId` and `InspectionId` in `evaluation`.
4. Check that the referenced aggregate exists in the application layer, not with a database constraint.

### Consequences
- **Positive**: each context can change its model without breaking the others; identifiers are not guessable.
- **Negative**: no database guarantees that a referenced aggregate still exists.

---

## ADR-006: Stateless JWT Authentication Scoped to a Company

### Status
Accepted

### Context
The API serves several transport companies, and every user belongs to one. The web application calls it from another origin.

### Decision
1. Authenticate with `POST /api/v1/auth/login`, which returns a signed JWT.
2. Put the user's `role` and `companyId` in the token claims.
3. Require `Authorization: Bearer <token>` on every other endpoint, with stateless sessions.
4. Hash passwords with BCrypt.
5. Let only an administrator open accounts, through `POST /api/v1/users`; there is no public sign-up.
6. Seed the first administrator at startup from `ADMIN_EMAIL`, `ADMIN_PASSWORD` and `ADMIN_COMPANY_ID`.

### Consequences
- **Positive**: no session storage; each request carries the company it acts on.
- **Negative**: a token stays valid until it expires, even if the user is deactivated in the meantime.

---

## ADR-007: Localized Error Responses as RFC 9457 Problem Details

### Status
Accepted

### Context
The course requires the web services to support `en_US` and `es_419`, and the web application needs one error format to handle.

### Decision
1. Keep every user-facing message in `messages.properties` (English) and `messages_es_419.properties` (Latin American Spanish).
2. Resolve the language from the `Accept-Language` header.
3. Use message keys, never literal text, in failures and in the `IllegalArgumentException` thrown by value objects.
4. Answer every error with a `ProblemDetail` body: result assemblers for expected failures, and `GlobalExceptionHandler` for validation errors and invalid arguments.
5. Answer with the key itself when a translation is missing.

### Consequences
- **Positive**: one error schema in both languages; the domain stays free of display text.
- **Negative**: every new message needs an entry in both files.

---

## ADR-008: Domain Event Dispatching through DomainEventPublisher

### Status
Accepted

### Context
Aggregates register domain events with `registerDomainEvent(...)`. Because they are not JPA entities, Spring Data does not publish those events on `save()`, and every repository adapter repeated the same publishing code.

### Decision
1. Add `DomainEventPublisher` in `shared.infrastructure.events`.
2. Inject it into every repository adapter whose aggregate registers events.
3. After saving, call `publishAndClearEvents(aggregate)`: it publishes the pending events in order through Spring's `ApplicationEventPublisher`, then clears them.

### Consequences
- **Positive**: one place decides how events leave an aggregate; an event is never published twice.
- **Negative**: listeners run in the request thread, so a slow listener slows the request.

---

## ADR-009: Virtual Threads for Request Handling

### Status
Accepted

### Context
Most of the time of a request is spent waiting for PostgreSQL. With platform threads, waiting requests hold a thread each and exhaust the pool under load.

### Decision
Enable `spring.threads.virtual.enabled=true`, so that Tomcat serves each request on a virtual thread. Asynchronous executors are not configured because the platform has no `@Async` tasks.

### Consequences
- **Positive**: more concurrent requests without reactive programming.
- **Negative**: the database connection pool becomes the limit and must be sized for it.

---

## ADR-010: Development and Production Profiles

### Status
Accepted

### Context
Local development needs defaults and SQL logging. A deployment must not start with a development secret or log every statement.

### Decision
1. Keep shared settings in `application.properties`, with `dev` as the default profile.
2. In `application-dev.properties`, default `DATABASE_URL` to a local database, log the SQL and provide a development `JWT_SECRET`.
3. In `application-prod.properties`, require `DATABASE_URL` and `JWT_SECRET` and turn SQL logging off.
4. Activate `prod` in the Docker image.
5. Keep `ddl-auto=update` in both profiles, because the platform has no migration tool yet.

### Consequences
- **Positive**: a deployment without its secret fails at startup instead of running with a known key.
- **Negative**: the schema is still created by Hibernate; destructive changes have to be applied by hand.

---

## ADR-011: Contract-First OpenAPI Documentation

### Status
Accepted

### Context
The web application was built against a simulated API before the backend existed, and the report documents every endpoint.

### Decision
1. Write the contract first in `openapi/`, one file per bounded context, joined by `openapi.yaml`.
2. Implement the controllers to match it and describe them with Swagger annotations.
3. Serve the generated documentation with springdoc at `/swagger-ui.html` and `/v3/api-docs`.

### Consequences
- **Positive**: frontend and backend agree on paths and payloads before writing code.
- **Negative**: the contract and the annotations can drift apart and have to be reviewed together.

---

## ADR-012: Container Delivery to Azure Container Apps

### Status
Accepted

### Context
The API is deployed for each delivery of the course, and the deployed version must be traceable to a commit.

### Decision
1. On every pull request and push to `develop` or `main`, run the tests against PostgreSQL 18 in GitHub Actions.
2. Build a Java 27 image that runs as a non-root user and publish it to the GitHub Container Registry.
3. Deploy it on Azure Container Apps with Azure Database for PostgreSQL, pinning the image to its `sha-…` tag.

### Consequences
- **Positive**: every deployment is a tested image of a known commit.
- **Negative**: a new version reaches Azure only when the image tag is updated there.
