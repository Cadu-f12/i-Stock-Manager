# DDA Metalúrgica — Sistema de Gerenciamento de Estoque

> Projeto acadêmico de desenvolvimento de software voltado para o gerenciamento de estoque de pastilhas industriais utilizadas pela **DDA Metalúrgica**, em Santa Catarina, Brasil.

**🇧🇷 Português** | [🇺🇸 English](README.md)

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

## Como executar o projeto

Execute os comandos a partir da raiz do repositório (`i-Stock-Manager`).

### Testar a aplicação integrada sem Docker

Com Java 21 e Node.js com npm instalados, abra dois terminais na raiz do repositório. No primeiro, inicie o backend com o perfil `local` (banco H2 persistido em `backend/data`, separado do MySQL):

```powershell
cd backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

No Linux/macOS, use `sh mvnw spring-boot:run -Dspring-boot.run.profiles=local` dentro de `backend`.

No segundo terminal:

```sh
cd frontend
npm install
npm run dev
```

Após aparecer `Started BackendApplication`, abra http://localhost:5173. Se o banco estiver vazio, a tela solicitará a criação do primeiro administrador. Depois de entrar, cadastre categorias e produtos, registre entradas e saídas e consulte o histórico. Administradores também podem gerenciar usuários. Os dados são enviados à API e permanecem no banco após atualizar a página ou reiniciar o backend.

O frontend usa `/api` no mesmo endereço da página; o proxy do Vite encaminha as chamadas ao backend e mantém os cookies de sessão e a proteção CSRF. Para usar outra porta do backend, copie `frontend/.env.example` para `frontend/.env.local`, ajuste `API_TARGET` e reinicie o Vite. O proxy também funciona com `npm run preview`; em hospedagem estática, configure o servidor para encaminhar `/api` ao backend.

### Com Docker Compose

Com o Docker em execução, inicie o banco de dados, o backend e o frontend:

```sh
docker compose up --build
```

Por padrão, o frontend estará em http://localhost:5173 e o backend em http://localhost:8080. Para encerrar os serviços, pressione `Ctrl+C` e execute `docker compose down`.

### Backend e frontend em terminais separados

Pré-requisitos: Java 21, Node.js com npm e Docker para iniciar o MySQL. O backend utiliza o Maven Wrapper incluído no repositório.

Primeiro, inicie apenas o banco de dados e aguarde o MySQL ficar pronto para receber conexões:

```sh
docker compose up -d db
```

No Windows, abra o Docker Desktop e aguarde o mecanismo do Docker iniciar antes de executar esse comando. Confira a inicialização do banco com `docker compose logs -f db`; quando aparecer `ready for connections` na porta `3306`, pressione `Ctrl+C` (o banco continuará rodando).

Se o backend encerrar com `Communications link failure` ou `Connection refused`, confira se o MySQL está rodando e se a porta publicada corresponde a `SPRING_MYSQL_PORT`. A mensagem `BUILD SUCCESS` do Maven, sozinha, não confirma que a aplicação iniciou; procure também por `Started BackendApplication`.

No primeiro terminal, execute o backend. No Windows (PowerShell):

```powershell
cd backend
$env:SPRING_MYSQL_HOST = "localhost"
$env:SPRING_MYSQL_PORT = "3307"
$env:SPRING_MYSQL_DATABASE = "istock"
$env:SPRING_MYSQL_USERNAME = "root"
$env:SPRING_MYSQL_PASSWORD = "123"
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```sh
cd backend
export SPRING_MYSQL_HOST=localhost
export SPRING_MYSQL_PORT=3307
export SPRING_MYSQL_DATABASE=istock
export SPRING_MYSQL_USERNAME=root
export SPRING_MYSQL_PASSWORD=123
sh mvnw spring-boot:run
```

Essas variáveis correspondem aos valores padrão do `compose.yaml`. Caso personalize o banco ou a porta publicada, ajuste-as antes de iniciar o backend.

No segundo terminal, partindo da raiz do repositório, execute o frontend:

```sh
cd frontend
npm install
npm run dev
```

Acesse o endereço exibido pelo Vite (por padrão, http://localhost:5173). As requisições para `/api` são encaminhadas ao backend em http://localhost:8080.

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
├── backend/                    # Backend da aplicação
│   ├── pom.xml                # Configuração e dependências do Maven
│   │
│   └── src/
│       ├── main/
│       │   ├── java/          # Código-fonte da aplicação
│       │   │   └── ...
│       │   │
│       │   └── resources/     # Configurações e recursos
│       │       └── ...
│       │
│       └── test/
│           └── java/          # Testes automatizados
│               └── ...
│
├── frontend/                   # Frontend da aplicação
│   ├── package.json            # Dependências e scripts do projeto
│   ├── vite.config.ts          # Configuração do Vite
│   ├── tsconfig.json           # Configuração do TypeScript
│   │
│   └── src/
│       └── ...                 # Código-fonte da interface
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
