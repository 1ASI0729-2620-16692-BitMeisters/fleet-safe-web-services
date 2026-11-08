# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.2.0] - 2026-11-08

### Added
- Spring Boot platform on PostgreSQL with the shared kernel: the `Result` pattern, domain events, the snake case and plural table naming strategy, and errors as RFC 9457 problem details in English and Spanish.
- Identity and Access: sign-in with JWT, the first administrator registered at startup, and the user accounts of the company with their roles.
- Fleet Management: the company, its fleets and vehicles.
- Vehicle Documentation: document types, the documents of each vehicle and their daily validity.
- Pre-Operational Inspection: the item catalog and the inspection of a vehicle, with its results, observations and photos kept in a private Azure Blob Storage container.
- Evaluation and Authorization: evaluation rules, the evaluation of a completed inspection, the operational authorization of each vehicle and the lifting of a block by a supervisor.
- Incident Management: incidents with their corrective actions, repairs and follow-ups.
- `db/demo-data.sql` with the demo company, its vehicles, the document types, the inspection items and the evaluation rules.

## [0.1.0] - 2026-10-08

### Added
- OpenAPI contract with the shared components, Identity and Access, the active vehicle assignment of the driver, and Pre-Operational Inspection with its item catalog grouped by vehicle system.

[Unreleased]: https://github.com/1ASI0729-2620-16692-BitMeisters/fleet-safe-web-services/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/1ASI0729-2620-16692-BitMeisters/fleet-safe-web-services/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/1ASI0729-2620-16692-BitMeisters/fleet-safe-web-services/releases/tag/v0.1.0
