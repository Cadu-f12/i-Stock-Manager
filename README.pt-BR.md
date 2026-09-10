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
