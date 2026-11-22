# Backend Development Guidelines

## Purpose

The `backend/` directory contains the Spring Boot backend for the I-Stock Manager system.

The backend is responsible for:

- exposing the REST API;
- enforcing business rules;
- persisting inventory data;
- authenticating users and authorizing access;
- managing cutting inserts, manufacturers, suppliers, inventory movements, and users.

These instructions apply to all backend changes. Follow the existing architecture and conventions unless a change explicitly requires an architectural decision.

The backend architecture, package structure, domain characteristics, security model, environment behavior, and database seed behavior are documented in [`docs/architecture.md`](docs/architecture.md). Read that document before making structural or persistence changes.

## API Error Standard

All backend error messages must be written in clear English.

Every error response, including validation, authentication, authorization, not-found, conflict, and unexpected errors, must use the same `ApiError` response contract.

The standard contract is:

```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Request validation failed.",
  "fields": [
    {
      "field": "sku_code",
      "message": "SKU code must not be blank."
    },
    {
      "field": "minimum_stock_level",
      "message": "Minimum stock level must be greater than zero."
    }
  ]
}
```

The `fields` property must be a list of field-specific errors for validation failures. For errors that are not associated with a specific request field, return an empty list.

Each field error must identify the external JSON field name, not the Java property name. For example, use `sku_code`, not `skuCode`.

Error responses must follow these rules:

- `status` contains the HTTP status code.
- `error` contains a stable uppercase `SNAKE_CASE` error code.
- `message` contains a general human-readable English message.
- `fields` contains detailed field-level errors, or an empty list when not applicable.
- Do not expose stack traces, SQL errors, credentials, tokens, or internal implementation details.
- Do not return ad hoc error formats from controllers.
- Handle errors centrally through `GlobalExceptionHandler` whenever possible.

Recommended HTTP status conventions:

- `400 Bad Request`: malformed or invalid request data;
- `401 Unauthorized`: authentication is missing or invalid;
- `403 Forbidden`: the authenticated user does not have permission;
- `404 Not Found`: the requested resource does not exist;
- `409 Conflict`: the request conflicts with existing data;
- `500 Internal Server Error`: an unexpected server-side failure.

## Exception Naming

Custom exception class names and error codes must describe the same failure using the project's naming convention.

Examples:

| Exception class | Error code |
|---|---|
| `PastilleAlreadyExists` | `PASTILLE_ALREADY_EXISTS` |
| `ManufacturerNotFound` | `MANUFACTURER_NOT_FOUND` |
| `UserAlreadyExists` | `USER_ALREADY_EXISTS` |

Use the following rules:

- Exception classes use PascalCase.
- Error codes use uppercase `SNAKE_CASE`.
- Use `AlreadyExists` for uniqueness conflicts and map them to `409 Conflict`.
- Use `NotFound` when a requested resource cannot be found and map it to `404 Not Found`.
- Use meaningful domain-specific names; do not throw generic exceptions for expected business failures.
- Add the corresponding handler to `GlobalExceptionHandler`.

## Validation

Use Jakarta Bean Validation annotations in request DTOs, such as `@NotNull`, `@NotBlank`, `@Size`, `@Positive`, and `@Pattern`.

Validation messages must be written in English and must clearly explain the constraint that failed.

Controllers must use `@Valid` when accepting a request DTO so that validation is executed before the service is called.

Validation failures must be converted into the standard `ApiError` response with one entry in `fields` for each invalid field.

## Development Commands

Run commands from the `backend/` directory.

Start the backend locally:

```bash
./mvnw spring-boot:run
```

Run tests:

```bash
./mvnw test
```

Run the development database seed explicitly:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--seed" -Dspring-boot.run.profiles=local
```

The seed must not run automatically during a normal application startup.

For local execution, use the local database configuration described in `backend/README.md`. For Docker Compose execution, keep the container datasource configuration unchanged and start the infrastructure from the project root.

## Testing Rules

Before completing a backend change:

1. Run the relevant tests.
2. Add or update tests for new business rules and error cases.
3. Verify successful responses and expected failure responses.
4. Verify validation errors include the correct external JSON field names.
5. Verify HTTP status codes and stable error codes.

At minimum, new endpoints should cover:

- a successful request;
- invalid request validation;
- missing related resources;
- duplicate or conflicting data;
- unauthorized and forbidden access when security applies.

Do not rely only on manual testing when the behavior can be covered by an automated test.

## Naming Conventions

- Use English for classes, methods, variables, exception messages, error codes, and API documentation.
- Use PascalCase for classes and records.
- Use camelCase for Java fields, methods, and parameters.
- Use `snake_case` for JSON properties when that is the established API convention, using `@JsonProperty` where necessary.
- Use descriptive names that reflect the domain.
- Keep request DTO names explicit, such as `RegistryPastilleRequest`.
- Keep response DTOs separate from entities, such as `PastilleDTO`.

## Git Commit Conventions

Commit messages must follow the Conventional Commits format:

```text
<type>(<optional scope>): <short imperative description>
```

Use an appropriate commit type, such as:

- `feat`: add a new feature or endpoint;
- `fix`: correct a bug;
- `refactor`: change code structure without changing behavior;
- `test`: add or update tests;
- `docs`: update documentation;
- `chore`: perform maintenance or configuration work.

Commit subjects must be written in English, use the imperative mood, and clearly describe the main change. Keep the subject concise and do not add a commit description/body by default.

Only add a commit description/body when the commit contains many independent or substantial changes. In that case, list every relevant change in the description so the commit can be understood without inspecting the diff.

Preferred examples:

```text
feat(pastilles): add pastille registration endpoint
fix(authentication): handle invalid JWT tokens
refactor(inventory): separate movement business rules
```

Avoid vague subjects such as `updates`, `fix stuff`, or `changes`.

The AI must never execute `git push` or publish changes to a remote repository. Pushing commits is exclusively the developer's responsibility. The AI may inspect repository status, prepare changes, stage files, and create local commits when explicitly requested, but must stop before any push operation.
