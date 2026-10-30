# I-Stock Manager — Backend

## 📌 About

The **I-Stock Manager Backend** is the server-side application responsible for managing the business logic, data persistence, authentication, and REST API of the I-Stock Manager system.

The system is being developed to support **DDA Metalúrgica** in managing the inventory of industrial cutting inserts used in machining and manufacturing processes.

The backend provides the services required to register and manage cutting inserts, manufacturers, suppliers, inventory movements, users, and other inventory-related information.

---

## 🚀 Running the Backend

The backend can be executed in two different ways:

- Locally, while running only the backend application.
- With the complete project infrastructure using Docker Compose.

### First-Time Setup — Environment Variables

Before running the backend for the first time, configure the project's environment variables.

Environment variables are managed from the **root directory of the I-Stock Manager project**, rather than inside the `backend/` directory.

The project structure is organized as follows:

```text
i-Stock-Manager/
├── backend/
├── frontend/
├── .env
├── .env.example
└── README.md
```

#### 1. Create the environment file

From the project root directory, copy `.env.example` to `.env`:

```bash
cp .env.example .env
```

On Windows (PowerShell):

```powershell
Copy-Item .env.example .env
```

The `.env.example` file documents the required environment variables, while `.env` contains the actual values used by your local environment.

**Never commit the `.env` file or secret keys to version control.**

#### 2. Generate the JWT Secret Key

The backend uses JSON Web Tokens (JWT) for authentication.

JWTs are signed using the **HS256 algorithm**, which requires a secret key to generate and validate token signatures.

Generate a secure random key using OpenSSL:

```bash
openssl rand -base64 32
```

Copy the generated value and add it to the `.env` file:

```dotenv
JWT_SECRET=<your-generated-secret-key>
```

The key must be generated only once for your local environment and reused between application restarts.

**Important:** Changing `JWT_SECRET` invalidates previously issued JWTs signed with the old key.

#### 3. Make the environment variables available to Spring Boot

Spring Boot does not automatically load the `.env` file located in the project root when running directly through Maven.

For local execution on Linux or macOS, load the variables from the project root before starting the backend:

```bash
set -a
source .env
set +a

cd backend
./mvnw spring-boot:run
```

Only source trusted `.env` files, since this command executes their contents as shell code.

On Windows, configure `JWT_SECRET` as an environment variable in your terminal or IDE before running the backend.

For Docker Compose, the root `.env` file can be used for variable substitution, but the required variables must also be explicitly passed to the backend container through the Compose configuration.

#### 4. JWT Configuration

The backend reads the JWT secret from the environment:

```properties
app.jwt.secret=${JWT_SECRET}
app.jwt.issuer=istock-api
app.jwt.expiration-seconds=900
```

- `JWT_SECRET`: Secret key used to sign and validate JWTs.
- `app.jwt.issuer`: Identifier of the token issuer.
- `app.jwt.expiration-seconds`: Token validity period in seconds (900 seconds = 15 minutes).

Once the environment variables are configured, continue with the local or Docker Compose startup instructions below.

### Running Locally

When running the backend directly on your machine, the application needs to connect to the MySQL container through the port exposed to the host.

Open:

```text
src/main/resources/application.properties
```

Find the datasource URL:

```properties
spring.datasource.url=jdbc:mysql://db:3306/istock
```

Change the database host from `db` to `localhost` and use port `3307`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/istock
```

Make sure the MySQL database is running and accessible on port `3307`.

Then, from the backend directory, start the Spring Boot application:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

---

### Running with Docker Compose

To run the backend together with the project infrastructure, **do not change the default `application.properties` datasource configuration**.

Navigate to the root directory of the **I-STOCK** project, where the `compose.yaml` file is located:

```text
I-STOCK/
├── backend/
├── frontend/
├── compose.yaml
└── ...
```

Run:

```bash
docker compose up -d --build --wait --wait-timeout 240
```

Docker Compose waits for MySQL before starting the API, then waits for the API before starting the frontend. Stop local instances on ports 8080 and 5173 first. See the [main README](../README.md#running-with-docker-compose) for prerequisites and environment setup.

The Compose environment connects the backend to `db:3306` and the frontend proxy to `backend:8080`. Open http://127.0.0.1:5173. MySQL data is persisted in `mysql_data`; the standalone H2 database is separate and is not automatically migrated.

This development Compose enables `--seed` by default, creating `admin@istock.com` / `admin` only if the account does not exist. Set `BACKEND_ARGUMENTS=` in `.env` to disable it. Inspect service status with `docker compose ps` and API logs with `docker compose logs -f backend`.

To stop the infrastructure, use:

```bash
docker compose down
```

---

## 🌱 Database Seed

The backend provides a database seed mechanism for generating a small set of initial development data.

The seed is **not executed during a normal application startup**. It must be explicitly requested through the command line.

From the backend directory, run:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--seed" -Dspring-boot.run.profiles=local
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--seed" -Dspring-boot.run.profiles=local
```

The seed creates development data such as an administrator user, manufacturers, suppliers, movement reasons, and cutting inserts.

---

## 🔐 Authentication

The backend provides user authentication using email and password.

User passwords are encoded before being persisted in the database and are never stored as plain text.

For authentication in the **development environment**, use the administrator account created by the database seed:

```text
Email: admin@istock.com
Password: admin
```

> **Note:** These credentials are intended exclusively for development and testing purposes.
