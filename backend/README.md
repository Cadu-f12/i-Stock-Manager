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

Navigate to the root directory of the **I-STOCK** project, where the `docker-compose.yaml` file is located:

```text
I-STOCK/
├── backend/
├── frontend/
├── docker-compose.yaml
└── ...
```

Run:

```bash
docker compose up --build
```

Docker Compose will build and start the services defined by the project infrastructure.

Because the services run inside the Docker network, the backend can communicate with the database using the `db` hostname configured in `application.properties`.

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