# DDA Metalúrgica — I-Stock Manager

> Academic software development project focused on improving the management of industrial cutting insert inventory used by **DDA Metalúrgica**, Santa Catarina, Brazil.

**🇺🇸 English** | [🇧🇷 Português](README.pt-BR.md)

> Integração do frontend, autenticação JWT, execução e pendências: [guia do frontend](frontend/README.md).

---

## Running with Docker Compose

### 1. Prerequisites

Install Docker Desktop (or Docker Engine with the Compose plugin on Linux) and start it. On Windows, use Linux containers. Run all commands below from the project root, where `compose.yaml` is located.

Java, Maven, Node.js and MySQL run inside the containers; they do not need to be installed on the host. Ports **5173**, **8080** and **3307** must be available. Stop any local Vite or Spring Boot instance using these ports.

```sh
docker version
docker compose version
```

### 2. Configure the environment

If `.env` does not exist, copy the example. Keep an existing `.env`:

```powershell
# Windows / PowerShell
Copy-Item .env.example .env
```

```sh
# Linux / macOS
cp .env.example .env
```

Fill in these values in `.env`:

```dotenv
MYSQL_DATABASE=istock
MYSQL_ROOT_PASSWORD=replace_with_your_database_password
JWT_SECRET=replace_with_a_generated_base64_key
SPRING_PORT=8080
REACT_PORT=5173
```

Generate the JWT key once, then copy the output into `JWT_SECRET`. It must decode to exactly 32 bytes. In PowerShell:

```powershell
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
try { $rng.GetBytes($bytes) } finally { $rng.Dispose() }
[Convert]::ToBase64String($bytes)
```

On Linux/macOS with OpenSSL:

```sh
openssl rand -base64 32
```

Keep `.env` private and reuse the same key between restarts. For an existing `mysql_data` directory, use the database password originally configured; changing the environment variable does not change the stored MySQL password.

### 3. Start the system

```sh
docker compose config --quiet
docker compose up -d --build --wait --wait-timeout 240
```

Compose starts MySQL, waits for it to be ready, then starts the API and frontend. The first build downloads dependencies and may take several minutes. The services run in the background, so the terminal can be closed.

Open **http://127.0.0.1:5173**. Initial development login:

- Email: **admin@istock.com**
- Password: **admin**

The development seed runs on startup and preserves existing accounts. If the password was changed, use the current one. To disable the seed, add `BACKEND_ARGUMENTS=` to `.env`.

The frontend proxies `/api` to `backend:8080`; the API connects to `db:3306`. These internal addresses are fixed by Compose. `MYSQL_HOST`, `MYSQL_PORT` and `MYSQL_ROOT_USERNAME` from older environment files are not used by Compose. `SPRING_PORT` and `REACT_PORT` control host ports; MySQL is exposed at `127.0.0.1:3307`.

### 4. Check, stop and start again

```sh
docker compose ps
docker compose logs -f backend
docker compose logs -f frontend
docker compose down
```

All three services should show `healthy`. Ctrl+C stops log viewing only. `docker compose down` stops the system and preserves MySQL data in `mysql_data`.

To start again or rebuild after code/dependency changes:

```sh
docker compose up -d --build --wait --wait-timeout 240
```

The standalone H2 database in `backend/.local` is separate and is not migrated into MySQL. This Compose setup is for development and mounts the project source into the containers.

If startup fails, run `docker compose logs --tail 100 db backend frontend`. A port conflict requires stopping the application using that port; an invalid JWT key requires a Base64 key containing 32 bytes. If startup only exceeded the wait timeout, inspect the logs and run `docker compose up -d --wait --wait-timeout 240` again.

---

## 📌 About the Project

The **DDA Metalúrgica Inventory Management System** is a web application being developed to improve the management of industrial cutting inserts used in the company's machining and manufacturing processes.

Currently, DDA Metalúrgica manages its cutting insert inventory through **spreadsheets and manual data entry**. This approach makes it difficult to maintain accurate and up-to-date information, increases the possibility of human error, and can delay the identification of materials that need to be replenished.

The proposed solution aims to centralize inventory information in a single system, allowing employees to register, monitor, and track inventory movements more efficiently.

The project is being developed as part of an academic project at **SENAI/SC — Fraiburgo**.

---

## 🎯 Problem

DDA Metalúrgica uses several types of industrial cutting inserts, each designed for specific machining applications.

Because these materials are essential to the production process and can have significant consumption, maintaining accurate inventory information is critical.

The current spreadsheet-based process presents several challenges:

* Manual registration of inventory movements.
* Increased risk of human errors.
* Difficulty keeping inventory information up to date.
* Limited visibility of current stock levels.
* Difficulty identifying materials approaching critical stock levels.
* Delays in identifying replenishment needs.
* Lack of centralized movement history.
* Difficulty generating reliable consumption and inventory reports.

These problems can potentially lead to **material shortages and interruptions in production**.

---

## 💡 Proposed Solution

The proposed solution is a **web-based inventory management system** designed specifically for managing industrial cutting inserts.

The system will centralize inventory data and provide tools for controlling material entries and exits, monitoring minimum stock levels, tracking historical movements, and supporting purchasing decisions.

The application is intended to provide a simple and intuitive interface that allows employees and inventory managers to quickly access reliable information about the current state of the inventory.

---

## 🏗️ System Architecture

The initial architecture is planned around a **web application** using a separated frontend and backend.

```text
┌─────────────────────────────┐
│          Frontend           │
│          React + Vite       │
└──────────────┬──────────────┘
               │
               │ HTTP / REST API
               ▼
┌─────────────────────────────┐
│           Backend           │
│       Java + Spring Boot    │
└──────────────┬──────────────┘
               │
               │ Data Access
               ▼
┌─────────────────────────────┐
│          Database           │
│         MySQL               │
└─────────────────────────────┘
```

This architecture allows the frontend and backend to evolve independently while communicating through a RESTful API.

---

## 🛠️ Technologies

The initial technology stack planned for the project is:

### Backend

* **Java**
* **Spring Boot**
* **Spring Data JPA**
* **Spring Security**
* **Hibernate**
* **REST API**

### Frontend

* **Angular**
* **TypeScript**
* **HTML**
* **CSS**

### Database

* **MySQL**

### Development Tools

* **Git**
* **GitHub**
* **Maven**

---

## 🎓 Academic Context

This project is being developed as an **academic software engineering project** at:

**SENAI/SC — Florianópolis, Santa Catarina, Brazil**

The project provides an opportunity to apply software development concepts to a real-world business problem, including:

* Requirements analysis.
* Software architecture.
* Database modeling.
* Backend development.
* Frontend development.
* REST API development.
* Authentication and authorization.
* Version control.
* Testing.
* Software documentation.

---

## 📂 Project Structure

The project is organized as a **full-stack application**, separating the backend and frontend into independent directories.

```text
i-Stock-Manager/
│
├── backend/                    # Application backend
│   ├── pom.xml                # Maven configuration and dependencies
│   │
│   └── src/
│       ├── main/
│       │   ├── java/          # Application source code
│       │   │   └── ...
│       │   │
│       │   └── resources/     # Configuration files and resources
│       │       └── ...
│       │
│       └── test/
│           └── java/          # Automated tests
│               └── ...
│
├── frontend/                   # Application frontend
│   ├── package.json            # Project dependencies and scripts
│   ├── vite.config.ts          # Vite configuration
│   ├── tsconfig.json           # TypeScript configuration
│   │
│   └── src/
│       └── ...                 # User interface source code
│
├── mysql_data/                 # Data of mysql volume (Do not remove!)
│
├── compose.yaml                # Docker compose file to containers
│
├── .env                        # Your environment variables
├── .env.example                # Keys of variables
│ 
└── README.md
```

The frontend structure and the overall repository organization will be defined as the project architecture evolves.

---

## 👥 Development Team

This project is being developed collaboratively by students from **SENAI/SC — Fraiburgo**.

### Team Members

* **Carlos Eduardo**
* **Eliot**
* **Guilherme**
* **Enrico**

---

## 🌐 Documentation

* 🇺🇸 **English** — `README.md`
* 🇧🇷 **Português** — [`README.pt-BR.md`](README.pt-BR.md)
