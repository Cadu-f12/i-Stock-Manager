# DDA Metalúrgica — I-Stock Manager

> Academic software development project focused on improving the management of industrial cutting insert inventory used by **DDA Metalúrgica**, Santa Catarina, Brazil.

**🇺🇸 English** | [🇧🇷 Português](README.pt-BR.md)

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
