# DDA Metalúrgica — Sistema de Gerenciamento de Estoque

## Executar com Docker Compose

### 1. Pré-requisitos

Instale e abra o Docker Desktop (ou Docker Engine com o plugin Compose no Linux). No Windows, use contêineres Linux. Execute os comandos na raiz do projeto, onde está `compose.yaml`.

Java, Maven, Node.js e MySQL são executados nos contêineres; não precisam estar instalados no computador. Libere as portas **5173**, **8080** e **3307**, encerrando eventuais instâncias locais do Vite ou Spring Boot.

```sh
docker version
docker compose version
```

### 2. Configurar o ambiente

Se ainda não houver `.env`, copie o exemplo. Preserve o arquivo caso ele já exista:

```powershell
# Windows / PowerShell
Copy-Item .env.example .env
```

```sh
# Linux / macOS
cp .env.example .env
```

Preencha no `.env`:

```dotenv
MYSQL_DATABASE=istock
MYSQL_ROOT_PASSWORD=substitua_pela_senha_do_banco
JWT_SECRET=substitua_pela_chave_base64_gerada
SPRING_PORT=8080
REACT_PORT=5173
```

Gere a chave JWT uma vez e copie a saída para `JWT_SECRET`. Ela deve conter exatamente 32 bytes codificados em Base64. No PowerShell:

```powershell
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
try { $rng.GetBytes($bytes) } finally { $rng.Dispose() }
[Convert]::ToBase64String($bytes)
```

No Linux/macOS com OpenSSL:

```sh
openssl rand -base64 32
```

Mantenha o `.env` privado e reutilize a chave ao reiniciar. Se `mysql_data` já existir, use a senha originalmente configurada para esse banco; alterar a variável não altera a senha armazenada pelo MySQL.

### 3. Iniciar o sistema

```sh
docker compose config --quiet
docker compose up -d --build --wait --wait-timeout 240
```

O Compose aguarda o MySQL ficar pronto, inicia a API e depois o frontend. A primeira construção baixa dependências e pode levar alguns minutos. Os serviços ficam em segundo plano; você pode fechar o terminal.

Acesse **http://127.0.0.1:5173**. Login inicial de desenvolvimento:

- E-mail: **admin@istock.com**
- Senha: **admin**

O seed executa na inicialização e preserva contas existentes. Caso a senha já tenha sido alterada, use a senha atual. Para desativar o seed, adicione `BACKEND_ARGUMENTS=` ao `.env`.

O frontend encaminha `/api` para `backend:8080`; a API conecta a `db:3306`. Esses endereços internos são definidos pelo Compose. `MYSQL_HOST`, `MYSQL_PORT` e `MYSQL_ROOT_USERNAME` de arquivos antigos não são usados pelo Compose. `SPRING_PORT` e `REACT_PORT` controlam as portas do computador; o MySQL é publicado em `127.0.0.1:3307`.

### 4. Verificar, encerrar e iniciar novamente

```sh
docker compose ps
docker compose logs -f backend
docker compose logs -f frontend
docker compose down
```

Os três serviços devem aparecer como `healthy`. Ctrl+C encerra somente a visualização dos logs. `docker compose down` encerra o sistema e preserva os dados MySQL em `mysql_data`.

Para iniciar novamente ou reconstruir após alterações no código/dependências:

```sh
docker compose up -d --build --wait --wait-timeout 240
```

O banco H2 do modo standalone, em `backend/.local`, é separado e não é migrado para o MySQL. Este Compose é para desenvolvimento e monta os fontes do projeto nos contêineres.

Se a inicialização falhar, consulte `docker compose logs --tail 100 db backend frontend`. Para conflitos de porta, encerre o processo que ocupa a porta indicada. Para chave JWT inválida, gere uma chave de 32 bytes em Base64. Se apenas o tempo de espera se esgotar, confira os logs e execute novamente `docker compose up -d --wait --wait-timeout 240`.

> Projeto acadêmico de desenvolvimento de software voltado para o gerenciamento de estoque de pastilhas industriais utilizadas pela **DDA Metalúrgica**, em Santa Catarina, Brasil.

**🇧🇷 Português** | [🇺🇸 English](README.md)

> Integração do frontend, autenticação JWT, execução e pendências: [guia do frontend](frontend/README.md).

---

## 📌 Sobre o Projeto

O **Sistema de Gerenciamento de Estoque da DDA Metalúrgica** é uma aplicação web que está sendo desenvolvida com o objetivo de melhorar o gerenciamento das pastilhas industriais utilizadas nos processos de usinagem e fabricação da empresa.

Atualmente, a DDA Metalúrgica realiza o controle do estoque de pastilhas por meio de **planilhas eletrônicas e registros manuais**. Esse processo dificulta a manutenção de informações precisas e atualizadas, aumenta a possibilidade de erros humanos e pode atrasar a identificação de materiais que precisam ser repostos.

A solução proposta busca centralizar as informações do estoque em um único sistema, permitindo que os colaboradores registrem, acompanhem e consultem as movimentações dos materiais de maneira mais eficiente.

O projeto está sendo desenvolvido como parte de um projeto acadêmico no **SENAI/SC — Fraiburgo**.

---

## 🎯 Problema

A DDA Metalúrgica utiliza diversos tipos de pastilhas industriais, cada uma destinada a aplicações específicas nos processos de usinagem.

Como esses materiais são essenciais para a produção e possuem consumo significativo, manter informações precisas sobre o estoque é fundamental.

O processo atual baseado em planilhas apresenta diversos desafios:

* Registro manual das movimentações de estoque.
* Maior possibilidade de erros humanos.
* Dificuldade para manter as informações atualizadas.
* Visibilidade limitada sobre os níveis atuais do estoque.
* Dificuldade para identificar materiais próximos de níveis críticos.
* Atrasos na identificação da necessidade de reposição.
* Falta de um histórico centralizado das movimentações.
* Dificuldade na geração de relatórios confiáveis sobre consumo e estoque.

Esses problemas podem resultar na **falta de materiais e em possíveis interrupções na produção**.

---

## 💡 Solução Proposta

A solução proposta consiste em um **sistema web de gerenciamento de estoque**, desenvolvido especificamente para o controle das pastilhas industriais utilizadas pela empresa.

O sistema deverá centralizar as informações do estoque e fornecer recursos para controlar entradas e saídas de materiais, acompanhar níveis mínimos, consultar o histórico de movimentações e auxiliar no planejamento de reposições.

A aplicação pretende oferecer uma interface simples e intuitiva, permitindo que os responsáveis pelo estoque tenham acesso rápido a informações confiáveis sobre a situação atual dos materiais.

---

## 🏗️ Arquitetura do Sistema

A arquitetura inicial será baseada em uma **aplicação web**, separando o frontend e o backend.

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
               │ Acesso aos dados
               ▼
┌─────────────────────────────┐
│          Database           │
│         MySQL               │
└─────────────────────────────┘
```

Essa arquitetura permitirá que frontend e backend sejam desenvolvidos de maneira independente, comunicando-se por meio de uma API REST.

---

## 🛠️ Tecnologias

A stack tecnológica inicialmente planejada para o projeto é:

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

### Banco de Dados

* **MySQL**

### Ferramentas de Desenvolvimento

* **Git**
* **GitHub**
* **Maven**

---

## 🎓 Contexto Acadêmico

Este projeto está sendo desenvolvido como um **projeto acadêmico de engenharia de software** no:

**SENAI/SC — Florianópolis, Santa Catarina, Brasil**

O projeto proporciona a aplicação prática de conceitos de desenvolvimento de software na resolução de um problema real de negócio, incluindo:

* Análise de requisitos.
* Arquitetura de software.
* Modelagem de banco de dados.
* Desenvolvimento backend.
* Desenvolvimento frontend.
* Desenvolvimento de APIs REST.
* Autenticação e autorização.
* Controle de versão.
* Testes.
* Documentação de software.

---

## 📂 Estrutura do Projeto

O projeto está organizado em uma estrutura **full stack**, separando o backend e o frontend em diretórios independentes.

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

A estrutura do frontend e a organização geral do repositório serão definidas conforme a arquitetura do projeto evoluir.

---

## 👥 Equipe de Desenvolvimento

Este projeto está sendo desenvolvido colaborativamente por alunos do **SENAI/SC — Fraiburgo**.

### Integrantes

* **Carlos Eduardo**
* **Eliot**
* **Guilherme**
* **Enrico**

---

## 🌐 Documentação

* 🇧🇷 **Português** — `README.pt-BR.md`
* 🇺🇸 **English** — [`README.md`](README.md)
