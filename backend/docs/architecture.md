# Backend Architecture

## Purpose

The `backend/` directory contains the Spring Boot backend for the I-Stock Manager system.

The backend is responsible for:

- exposing the REST API;
- enforcing business rules;
- persisting inventory data;
- authenticating users and authorizing access;
- managing cutting inserts, manufacturers, suppliers, inventory movements, and users.

This document describes the backend architecture and project-specific behavior. Engineering rules and contribution guidelines are defined in `backend/AGENTS.md`.

## Source Code Structure

Main application code is located under `backend/src/main/java/com/istock/system/`:

```text
com/istock/system/
├── controller/   # REST endpoints and HTTP request/response handling
├── dtos/
│   ├── request/  # Request payloads accepted by the API
│   └── response/ # Response payloads returned by the API
├── entities/     # JPA entities mapped to database tables
├── exception/    # Custom exceptions and centralized error handling
├── repository/   # Spring Data repositories and persistence queries
├── security/     # Authentication, authorization, and JWT configuration
├── seed/         # Explicit development database seed logic
└── service/      # Business rules and application use cases
```

The application entry point is `BackendApplication`.

## Layer Responsibilities

Keep responsibilities separated:

- Controllers handle HTTP concerns only. They validate input, delegate to services, and build HTTP responses.
- Services contain business rules and coordinate repositories and domain objects.
- Repositories contain persistence operations and database queries.
- Entities represent the persisted domain model and must not be exposed directly as API contracts.
- Request and response DTOs define the public API contract.
- Exceptions represent meaningful business or application failures.
- `GlobalExceptionHandler` converts exceptions into standardized API error responses.

This separation keeps business logic independent from HTTP and persistence details, makes code easier to test, and prevents changes in one layer from unnecessarily affecting the others.

## Domain and Persistence Characteristics

- Cutting inserts are represented by the `Pastille` entity.
- Each pastille has a unique SKU code.
- Each pastille must reference a manufacturer.
- A pastille may be associated with multiple suppliers.
- Inventory balance is represented by `currentBalance`.
- Inventory movements are the domain flow for changing inventory balances; a registration request starts with a balance of zero.
- Minimum stock levels are used to identify pastilles that require attention.
- JPA entities are persisted through Spring Data repositories.
- Referenced entities, such as manufacturers and suppliers, must exist before relationships are persisted.
- Database constraints are part of the domain rules, including unique SKU codes and non-negative stock levels.

## Authentication and Security

Authentication uses Spring Security and JSON Web Tokens (JWT).

- Authentication and authorization logic belongs in the `security/` package and Spring Security configuration.
- Passwords are encoded before persistence and must never be stored or logged in plain text.
- JWTs are signed using the HS256 algorithm.
- The JWT secret is provided through the `JWT_SECRET` environment variable.
- Changing `JWT_SECRET` invalidates tokens signed with the previous secret.
- JWT issuer and expiration are configured through Spring properties.
- Authentication failures use `401 Unauthorized`.
- Authorization failures use `403 Forbidden`.
- Tokens, secret keys, passwords, and other credentials must never be logged or committed.

## Configuration and Environments

Environment variables are managed from the project root, not from the `backend/` directory:

```text
i-Stock-Manager/
├── backend/
├── frontend/
├── .env
├── .env.example
└── README.md
```

The `.env` file contains local values and must never be committed. `.env.example` documents the required variables without exposing secrets.

When running locally, the backend connects to the MySQL container through `localhost:3307`. When running with Docker Compose, the backend uses the `db` hostname inside the Docker network.

The active datasource configuration must match the execution environment. Do not change the Docker datasource configuration when running the complete infrastructure with Docker Compose.

## Database Seed

The database seed is intended for development and testing only.

It is not executed during normal application startup. It must be explicitly requested with the `--seed` argument and the local profile.

The seed creates development data such as:

- an administrator user;
- manufacturers;
- suppliers;
- movement reasons;
- cutting inserts.

Production behavior must not depend on seed data.

## API Design Characteristics

- REST endpoints are exposed through controllers under the `controller/` package.
- Request payloads use request DTOs and response payloads use response DTOs.
- JSON properties follow the established `snake_case` API convention where applicable.
- New resources should return their created representation with `201 Created` when registration succeeds.
- API errors follow the standard defined in `backend/AGENTS.md`.
