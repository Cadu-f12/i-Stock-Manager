# DDA Metalúrgica — I-Stock Manager

**English** | [Português](README.pt-BR.md)

I-Stock Manager is a web application for managing the industrial cutting insert inventory used by **DDA Metalúrgica**, in Santa Catarina, Brazil. It centralizes product balances and movement history to help employees keep inventory records up to date and support replenishment decisions.

The project was created in an academic context at **SENAI/SC** by Carlos Eduardo, Eliot, Guilherme and Enrico. Its purpose is to improve a workflow based on spreadsheets and manual records, reducing errors and making stock information easier to consult.

## Available features

- Register inserts by SKU, manufacturer and minimum stock.
- Stock entries and withdrawals with an author and change history.
- Search by ID, SKU, description or manufacturer and filter by alert or availability.
- Sorting and pages of 50, 100 or 200 products.
- User administration and inventory reports for administrators.

## Access and roles

Open the application address provided by whoever runs the system and sign in with your registered email and password. There is no public account registration. If the application has not been set up, the person responsible can follow [DESENVOLVIMENTO.md](DESENVOLVIMENTO.md).

| Role | Available actions |
| --- | --- |
| Operator | View, register and edit products; record entries/withdrawals; manage manufacturers and suppliers and view history |
| Admin | All inventory actions, plus user administration and reports |

Use **Sair** to sign out. When your session expires, sign in again.

## Using the application

### Cutting inserts and stock movements

In **Produtos**, select **Nova pastilha** and enter the SKU, manufacturer and minimum stock. Create categories under **Categorias** and choose one when creating or editing an insert. Select any registered suppliers that provide the insert. Their names appear in the stock table. Use search, availability and alert filters, sorting and pagination to locate an item. Manage contacts under **Fabricantes** and **Fornecedores**.

Each new insert starts with zero units. Use **+** or **−**, choose a reason and enter the quantity. Open **Histórico** to see the operator and date. The alert filter includes balances up to five units above minimum.

### Users

As Admin, open **Usuários** to create, view, edit, activate, deactivate or delete accounts. Choose the user's role and status. New passwords require at least 8 characters and at most 72 UTF-8 bytes; leaving the password empty while editing keeps the existing one.

You cannot delete or deactivate your own account or remove your own Admin role through this screen. If deletion is blocked by linked records, deactivate the account instead.

### Reports

As Admin, open **Metadados**. The category chart counts current inserts and lists registered categories even when they have no inserts. Monthly totals count units from explicit stock entries and withdrawals, grouped by São Paulo time. In **Entradas e saídas por mês**, choose **Pastilhas** or **Estoque anterior** to view each inventory separately. Use the year, category and product filters to narrow the monthly view.

Creating a product or directly editing its quantity does not count as an entry or withdrawal in the monthly chart. Category filters reflect each product's current category.

## Current limitations and help

- There is no product deletion or report export in the interface.
- **Metadados** brings monthly insert movements and historical inventory charts together on one page.
- If the server is unavailable, contact whoever runs the application. If a save conflicts with another change, reload the inventory; if the session expires, sign in again.

## Other documentation

- [DESENVOLVIMENTO.md](DESENVOLVIMENTO.md): environment setup, Docker, local execution, configuration, architecture, APIs, tests and builds.
- [AGENTS.md](AGENTS.md): project purpose, scope, maintenance references and rules for AI agents.
