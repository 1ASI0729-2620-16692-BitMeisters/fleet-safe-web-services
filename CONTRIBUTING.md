# Contributing to FleetSafe Platform

This guide covers how the RESTful API is organized, how work reaches `develop` and `main`, and how commits are written. The reasons behind each rule are recorded in [`docs/adrs.md`](docs/adrs.md).

---

## 1. Architecture

The platform is a modular monolith organized with layered Domain-Driven Design ([ADR-001](docs/adrs.md#adr-001-modular-monolith-with-layered-domain-driven-design)).

- **Bounded contexts**: each one lives in its own package under `com.bitmeisters.fleetsafe.platform`. Shared building blocks live in `shared`.
- **Layers**:
  - `domain`: aggregates, entities, value objects, commands, queries, domain events and repository interfaces. No Spring or JPA annotations.
  - `application`: command and query services, their failures, event handlers and the outbound service ports.
  - `infrastructure`: `*PersistenceEntity`, `*PersistenceAssembler`, `*RepositoryImpl` adapters and technology adapters, packaged as `infrastructure.<technology-type>.<technology>`.
  - `interfaces`: REST controllers, resources and assemblers named `[Target]From[Source]Assembler`.
- **Aggregates**: no setters. Change state through domain methods and rebuild from the database with a static `restore(...)` factory ([ADR-003](docs/adrs.md#adr-003-domain-model-decoupled-from-jpa-persistence)).
- **Across contexts**: reference other aggregates only by their `UUID`, wrapped in a value object of your context ([ADR-005](docs/adrs.md#adr-005-references-across-bounded-contexts-by-uuid-identity)).
- **Failures**: return `Result<T, F>` with a sealed failure type per use case, each failure with a `messageKey()` ([ADR-004](docs/adrs.md#adr-004-sealed-result-type-with-typed-failures-per-use-case)).
- **Messages**: never write display text in code. Add the key to `messages.properties` and `messages_es_419.properties` ([ADR-007](docs/adrs.md#adr-007-localized-error-responses-as-rfc-9457-problem-details)).
- **Domain events**: register them in the aggregate and publish them from the repository adapter with `DomainEventPublisher` ([ADR-008](docs/adrs.md#adr-008-domain-event-dispatching-through-domaineventpublisher)).
- **Contract**: describe every new endpoint in `openapi/` before implementing it ([ADR-011](docs/adrs.md#adr-011-contract-first-openapi-documentation)).

---

## 2. Branches

The repository follows GitFlow. `main` and `develop` are protected: every change reaches them through a pull request.

| Branch | Purpose | From | Into |
|---|---|---|---|
| `main` | published versions, each one tagged | — | — |
| `develop` | integration of the work in progress | `main` | `main`, through a release |
| `feature/<short-description>` | a new capability | `develop` | `develop` |
| `fix/`, `refactor/`, `chore/`, `docs/<short-description>` | work that adds no capability, named after its commit type | `develop` | `develop` |
| `release/<version>` | preparing a version | `develop` | `main`, then `main` back into `develop` |
| `hotfix/<short-description>` | an urgent fix to a published version | `main` | `main` and `develop` |

Branch names are in English, lowercase, with hyphens.

---

## 3. Local Development

1. Start PostgreSQL with `docker compose up -d`.
2. Run the application with the default `dev` profile: `./mvnw spring-boot:run`.
3. Run the tests before opening a pull request: `./mvnw test`. They need the same database.

The `prod` profile and its environment variables are described in the [README](README.md#configuration).

---

## 4. Commits

Commits follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/): `<type>(<scope>): <description>`.

- The description is in English, imperative, lowercase and without a final period.
- The scope is the bounded context or the area affected (`iam`, `evaluation`, `documentation`, `fleet`, `inspection`, `incident`, `shared`, `config`, `openapi`, `adrs`), not the technical layer.
- One commit per finished part of the work, each one complete: its domain, application, persistence and REST changes together.

| Type | Use |
|---|---|
| `feat` | a new capability |
| `fix` | a defect fixed |
| `refactor` | a change that keeps the behavior |
| `test` | tests added or changed |
| `docs` | documentation |
| `build` | dependencies, build or delivery |
| `chore` | anything else, such as configuration |

Examples from this repository:

```text
feat(documentation): register vehicle documents and list the ones about to expire
feat(iam): require a valid bearer token on every protected endpoint
fix(shared): generate timestamps at the precision postgresql stores
refactor(evaluation): separate the domain from its persistence
docs(openapi): add the vehicle documentation contract
chore(config): set the vehicle documentation time zone and status recalculation schedule
```

---

## 5. Versions

Versions follow [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html). The platform stays in `0.x` until its six bounded contexts are complete. Each release updates the version in `pom.xml` and the [CHANGELOG](CHANGELOG.md).

---

## 6. Tests

- Cover the rules of each aggregate and value object with unit tests.
- Cover each command service with Mockito, including every failure it can return.
- Cover each controller with an integration test against PostgreSQL, including the status and body of its errors.
