# DDA Metalúrgica — Inventory Management System

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

## ⚙️ Planned Features

> ⚠️ **The features listed below are currently under discussion.**
> The project team is still analyzing the requirements and deciding which functionalities will be included in the first version of the system.

Possible features currently being considered include:

### 📦 Inventory Management

* Registration of cutting inserts.
* Identification of materials by code and description.
* Registration of manufacturers and suppliers.
* Definition of minimum stock levels.
* Consultation of current stock quantities.

### 🔄 Inventory Movements

* Registration of incoming materials.
* Registration of outgoing materials.
* Automatic inventory quantity updates.
* Inventory movement history.

### 🚨 Stock Monitoring

* Configuration of minimum stock levels.
* Identification of items with critical inventory levels.
* Alerts for materials requiring replenishment.

### 📊 Reports and Dashboard

* Inventory overview.
* Consumption reports.
* Inventory movement history.
* Materials requiring replenishment.
* Current inventory status.

### 🔐 Authentication and Access Control

* User authentication.
* Access control for authorized users.
* Tracking of users responsible for inventory operations.

> The final feature set will be defined during the requirements analysis and project planning phases.

---

## 🏗️ System Architecture

The initial architecture is planned around a **web application** using a separated frontend and backend.

```text
┌─────────────────────────────┐
│          Frontend           │
│          Angular            │
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
│         PostgreSQL          │
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
* **Hibernate**
* **REST API**

### Frontend

* **Angular**
* **TypeScript**
* **HTML**
* **CSS**

### Database

* **PostgreSQL**

### Development Tools

* **Git**
* **GitHub**
* **Maven**

---

## 🔒 Non-Functional Requirements

> ⚠️ **The non-functional requirements have not yet been finalized.**
> The project team is currently analyzing the system requirements and defining the technical and quality criteria that the final solution must satisfy.

The following aspects are currently being considered during this process:

* Usability.
* Performance.
* Security.
* Maintainability.
* Scalability.
* Data integrity.
* Compatibility with computers and mobile devices.
* Low implementation and maintenance costs.
* Secure storage of information.
* User authentication and access control.

The final non-functional requirements will be documented after the requirements analysis is completed.

---

## 🎓 Academic Context

This project is being developed as an **academic software engineering project** at:

**SENAI/SC — Fraiburgo, Santa Catarina, Brazil**

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

## 🚀 Future Improvements

Although the initial scope is still being defined, some possible future directions have already been identified.

### 🔗 ERP Integration

One of the main future goals is the possibility of integrating the inventory management system with the **ERP systems used by the company**.

This could allow information to be exchanged between the inventory system and other business processes, such as purchasing, production, and resource management.

ERP integration is **not considered mandatory for the initial version**, but the system architecture should take future integration requirements into consideration.

Other possible future improvements include:

* Integration with purchasing systems.
* Barcode or QR code scanning.
* Importing existing inventory data.
* Advanced inventory analytics.

---

## 📂 Project Structure

The project is currently being organized using the standard **Maven project structure** for the Java backend.

```text
backend/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── ...
    │   └── resources/
    │       └── ...
    │
    └── test/
        └── java/
            └── ...
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
