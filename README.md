# DDA Metalúrgica — I-Stock Manager

**English** | [Português](README.pt-BR.md)

I-Stock Manager is a web application for managing the industrial cutting insert inventory used by **DDA Metalúrgica**, in Santa Catarina, Brazil. It centralizes product balances and movement history to help employees keep inventory records up to date and support replenishment decisions.

The project was created in an academic context at **SENAI/SC** by Carlos Eduardo, Eliot, Guilherme and Enrico. Its purpose is to improve a workflow based on spreadsheets and manual records, reducing errors and making stock information easier to consult.

## Available features

- Product and category registration, product editing and stock balance consultation.
- Stock entries and withdrawals with an author and change history.
- Search by name, ID or category; filters by category, last update date and stock availability.
- Sorting and pages of 50, 100 or 200 products.
- User administration and inventory reports for administrators.

## Access and roles

Open the application address provided by whoever runs the system and sign in with your registered email and password. There is no public account registration. If the application has not been set up, the person responsible can follow [DESENVOLVIMENTO.md](DESENVOLVIMENTO.md).

| Role | Available actions |
| --- | --- |
| Operator | View, register and edit products; record entries/withdrawals; manage categories and view history |
| Admin | All inventory actions, plus user administration and reports |

Use **Sair** to sign out. When your session expires, sign in again.

## Using the application

### Products and categories

In **Estoque**, choose **Nova categoria** to organize products, or use **Sem categoria**. Select **Novo produto**, enter a name, category and whole-number quantity, and save. Product names accept up to 120 characters; category names up to 60.

Use the search and filters to find a product. Open its details to see the balance and history, or choose the edit action to update it. Deleting a category keeps its products and moves them to **Sem categoria**. The default category cannot be deleted.

### Stock entries and withdrawals

Use **+** on a product to register an entry and **−** to register a withdrawal. Enter a positive whole number and confirm. Stock cannot become negative or exceed 999999999 units. Each operation records the responsible user and appears in the product history.

If someone else has changed the product, reload the inventory before trying again. Review the new balance before submitting the operation again.

### Users

As Admin, open **Usuários** to create, view, edit, activate, deactivate or delete accounts. Choose the user's role and status. New passwords require at least 8 characters and at most 72 UTF-8 bytes; leaving the password empty while editing keeps the existing one.

You cannot delete or deactivate your own account or remove your own Admin role through this screen. If deletion is blocked by linked records, deactivate the account instead.

### Reports

As Admin, open **Relatórios**. The category chart counts products, including those with zero stock. Monthly totals count units from explicit stock entries and withdrawals, grouped by São Paulo time. Use the year, category and product filters to narrow the monthly view.

Creating a product or directly editing its quantity does not count as an entry or withdrawal in the monthly chart. Category filters reflect each product's current category.

## Current limitations and help

- Minimum stock alerts, manufacturers, suppliers and movement reasons are not integrated into the current screens.
- There is no product deletion or report export in the interface.
- Initial sample cutting inserts from the older inventory model do not appear in the current inventory. Register products through **Estoque**.
- If the server is unavailable, contact whoever runs the application. If a save conflicts with another change, reload the inventory; if the session expires, sign in again.

## Other documentation

- [DESENVOLVIMENTO.md](DESENVOLVIMENTO.md): environment setup, Docker, local execution, configuration, architecture, APIs, tests and builds.
- [AGENTS.md](AGENTS.md): project purpose, scope, maintenance references and rules for AI agents.
