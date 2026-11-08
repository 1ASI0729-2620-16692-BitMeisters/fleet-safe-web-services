# FleetSafe Platform

RESTful API of FleetSafe, the preventive safety control platform for cargo fleets, built with
Spring Boot and Spring Data JPA on PostgreSQL. The `openapi/` folder holds the API contract the
Frontend Web Application is built against.

## Requirements

- JDK 27
- Docker with Docker Compose, for the PostgreSQL 18 database

## Running

Start the database, set its credentials and run the application with the Maven wrapper:

```bash
docker compose up -d
export DATABASE_USER=postgres
export DATABASE_PASSWORD=postgres
./mvnw spring-boot:run
```

`compose.yaml` starts PostgreSQL 18 with a `fleetsafe` database on port 5432 and keeps its data in
the `postgres-data` volume. The same variables run the tests: `./mvnw test`.

## Docker image

The `Dockerfile` builds the application with JDK 27 and runs it on a JRE 27 Alpine image as a
non-root user with the `prod` profile, listening on port 8080:

```bash
docker build -t fleetsafe-api .
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://<host>:5432/fleetsafe \
  -e DATABASE_USER=<user> \
  -e DATABASE_PASSWORD=<password> \
  -e JWT_SECRET=<secret> \
  fleetsafe-api
```

`JAVA_OPTS` can override the JVM options; by default the heap uses up to 75% of the container memory.

## Configuration

Without `SPRING_PROFILES_ACTIVE` the application runs with the `dev` profile, which logs the SQL and
falls back to local defaults. The `prod` profile, active in the Docker image, has no defaults for
`DATABASE_URL` and `JWT_SECRET`: the application does not start until both are set.

| Variable | Purpose | Default |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` or `prod` | `dev`, and `prod` in the Docker image |
| `DATABASE_URL` | JDBC URL of the PostgreSQL database | `jdbc:postgresql://localhost:5432/fleetsafe` in `dev`, none in `prod` |
| `DATABASE_USER` / `DATABASE_PASSWORD` | database credentials | none |
| `CORS_ALLOWED_ORIGINS` | comma-separated origins allowed to call `/api/**` | `http://localhost:4200,https://1asi0729-2620-16692-bitmeisters.github.io` |
| `JWT_SECRET` | secret that signs the access tokens, at least 32 bytes. **Required in `prod`** | a fixed development value in `dev`, none in `prod` |
| `JWT_EXPIRATION_SECONDS` | lifetime of an access token | `86400` (one day) |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD`, `ADMIN_COMPANY_ID` | first administrator, registered at startup if no user has that email. Nothing is seeded unless the three are set | none |
| `DOCUMENTATION_TIME_ZONE` | time zone that decides which day is today for the validity of vehicle documents and for their daily recalculation at 05:00 | `America/Lima` |

The API documentation is served at `/swagger-ui/index.html` and `/v3/api-docs`.

`DATABASE_URL` defaults to `jdbc:postgresql://localhost:5432/fleetsafe`. The API documentation is
served at `http://localhost:8080/swagger-ui.html`.

## Authentication

Every endpoint except `POST /api/v1/tokens` and the documentation requires a JWT:

1. Start the application with `ADMIN_EMAIL`, `ADMIN_PASSWORD` and `ADMIN_COMPANY_ID` set; the first
   administrator is registered at startup.
2. Sign in by creating a token:

   ```bash
   curl -X POST http://localhost:8080/api/v1/tokens \
     -H "Content-Type: application/json" \
     -d '{"email": "<ADMIN_EMAIL>", "password": "<ADMIN_PASSWORD>"}'
   ```

3. Send the `accessToken` of the response in every request: `Authorization: Bearer <accessToken>`.
   In Swagger UI, press **Authorize** and paste the token.

The authenticated user is available in a controller as `@AuthenticationPrincipal UserDetailsImpl`,
with its `id`, `companyId` and role (`ROLE_ADMINISTRATOR`, `ROLE_FLEET_SUPERVISOR`, `ROLE_DRIVER`).
Restrict an operation to a role with `@PreAuthorize("hasRole('ADMINISTRATOR')")`.

## Project structure

One package per bounded context, each with the same four layers, and a `shared` package for what
every context uses. The domain does not depend on JPA: each aggregate has a persistence entity in
the infrastructure layer, and an adapter translates between them.

```
com.bitmeisters.fleetsafe.platform
├── shared/
│   ├── application/patterns/              Result<T, E>
│   ├── domain/model/                      AbstractDomainAggregateRoot, Timestamps
│   ├── infrastructure/persistence/jpa/    AuditableAbstractPersistenceEntity, auditing, snake_case plural tables
│   └── interfaces/rest/                   GlobalExceptionHandler
└── <bounded-context>/
    ├── domain/
    │   ├── model/                         aggregates, entities, valueobjects, commands, queries, events
    │   └── repositories/                  repository interfaces
    ├── application/                       commandservices, queryservices and their failures
    │   └── internal/                      service implementations and outbound service ports
    ├── infrastructure/persistence/jpa/
    │   ├── entities/                      <Aggregate>PersistenceEntity, the only classes with @Entity
    │   ├── assemblers/                    <Aggregate>PersistenceAssembler, domain ⇄ entity
    │   ├── repositories/                  <Aggregate>PersistenceRepository, the JpaRepository
    │   └── adapters/                      <Aggregate>RepositoryImpl, implements the domain repository
    └── interfaces/rest/                   controllers, resources, transform
```

The structure follows the `learning-center-platform` reference of the 2026-10 cycle. The `iam` and
`evaluation` contexts are complete examples to copy.

## Conventions

An aggregate is a plain class that extends `AbstractDomainAggregateRoot`, keeps its rules, and is
rebuilt from the database through a `restore` factory:

```java
@Getter
public class Inspection extends AbstractDomainAggregateRoot<Inspection> {

    private UUID id;
    private Integer odometer;
    private Instant createdAt;
    private Instant updatedAt;

    public Inspection(StartInspectionCommand command) {
        this.odometer = command.odometer();
    }

    public static Inspection restore(UUID id, Integer odometer, Instant createdAt, Instant updatedAt) { … }
}
```

Its table is a persistence entity, and the adapter publishes the domain events after saving:

```java
@Entity
@Table(name = "inspections")
public class InspectionPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "odometer", nullable = false)
    private Integer odometer;
}

@Override
public Inspection save(Inspection inspection) {
    var events = List.copyOf(inspection.domainEvents());
    var saved = inspectionPersistenceRepository.save(InspectionPersistenceAssembler.toPersistenceFromDomain(inspection));
    events.forEach(eventPublisher::publishEvent);
    inspection.clearDomainEvents();
    return InspectionPersistenceAssembler.toDomainFromPersistence(saved);
}
```

Every command service declares its failures as a sealed interface:

```java
public sealed interface InspectionCommandFailure permits InspectionCommandFailure.AlreadyInProgress {
    String messageKey();

    record AlreadyInProgress() implements InspectionCommandFailure {
        @Override
        public String messageKey() {
            return "inspection.error.already-in-progress";
        }
    }
}
```

- Identifiers are UUIDs, generated by JPA in `AuditableAbstractPersistenceEntity`.
- Value objects are records that validate themselves. The persistence entity stores their plain value, and the
  assembler builds them back.
- Command services return a `Result` with a sealed failure type instead of throwing. Each failure
  carries a `messageKey`, and the interface layer turns it into a `ProblemDetail` with its status.
- Every message has its key in `messages.properties` (English) and `messages_es_419.properties`
  (Spanish), resolved from the `Accept-Language` header.
- The current time comes from `Timestamps.now()`, never from `Instant.now()`: it is truncated to
  microseconds, the precision PostgreSQL stores, so a date in memory equals the stored one.
- Endpoints live under `/api/v1`.
- Commits follow Conventional Commits, scoped to the bounded context: `feat(inspection): …`.
