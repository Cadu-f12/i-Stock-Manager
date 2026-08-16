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

## ⚙️ Funcionalidades Planejadas

> ⚠️ **As funcionalidades abaixo ainda estão em processo de definição.**
> A equipe está atualmente analisando os requisitos do sistema e decidindo quais funcionalidades farão parte da primeira versão da aplicação.

Entre as funcionalidades atualmente consideradas estão:

### 📦 Gerenciamento de Estoque

* Cadastro de pastilhas.
* Identificação dos materiais por código e descrição.
* Cadastro de fabricantes e fornecedores.
* Definição de estoque mínimo.
* Consulta das quantidades disponíveis.

### 🔄 Movimentações de Estoque

* Registro de entradas de materiais.
* Registro de saídas de materiais.
* Atualização automática das quantidades em estoque.
* Histórico das movimentações.

### 🚨 Monitoramento do Estoque

* Configuração de níveis mínimos.
* Identificação de materiais em situação crítica.
* Alertas para materiais que necessitam de reposição.

### 📊 Relatórios e Dashboard

* Visão geral do estoque.
* Relatórios de consumo.
* Histórico de movimentações.
* Identificação de materiais que precisam de reposição.
* Situação atual do estoque.

### 🔐 Autenticação e Controle de Acesso

* Autenticação de usuários.
* Controle de acesso para usuários autorizados.
* Identificação dos responsáveis pelas movimentações.

> O conjunto definitivo de funcionalidades será definido durante as etapas de análise de requisitos e planejamento do projeto.

---

## 🏗️ Arquitetura do Sistema

A arquitetura inicial será baseada em uma **aplicação web**, separando o frontend e o backend.

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
               │ Acesso aos dados
               ▼
┌─────────────────────────────┐
│          Database           │
│         PostgreSQL          │
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

* **PostgreSQL**

### Ferramentas de Desenvolvimento

* **Git**
* **GitHub**
* **Maven**

---

## 🔒 Requisitos Não Funcionais

> ⚠️ **Os requisitos não funcionais ainda não foram definidos definitivamente.**
> A equipe está atualmente analisando os requisitos do sistema e definindo os critérios técnicos e de qualidade que deverão ser atendidos pela solução final.

Entre os aspectos atualmente considerados estão:

* Usabilidade.
* Desempenho.
* Segurança.
* Manutenibilidade.
* Escalabilidade.
* Integridade dos dados.
* Compatibilidade com computadores e dispositivos móveis.
* Baixo custo de implantação e manutenção.
* Armazenamento seguro das informações.
* Autenticação e controle de acesso dos usuários.

Os requisitos não funcionais definitivos serão documentados após a conclusão da análise de requisitos.

---

## 🎓 Contexto Acadêmico

Este projeto está sendo desenvolvido como um **projeto acadêmico de engenharia de software** no:

**SENAI/SC — Fraiburgo, Santa Catarina, Brasil**

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

## 🚀 Possíveis Evoluções Futuras

Embora o escopo inicial ainda esteja sendo definido, algumas possíveis evoluções já foram identificadas.

### 🔗 Integração com ERP

Uma das principais possibilidades futuras é a integração do sistema de gerenciamento de estoque com os **sistemas ERP utilizados pela empresa**.

Essa integração poderia permitir o compartilhamento de informações entre o sistema de estoque e outros processos empresariais, como compras, produção e gerenciamento de recursos.

A integração com ERP **não é obrigatória para a primeira versão**, porém a arquitetura do sistema deverá considerar a possibilidade de futuras integrações.

Outras possíveis evoluções incluem:

* Integração com sistemas de compras.
* Leitura de códigos de barras ou QR Codes.
* Importação de dados existentes do estoque.
* Análises avançadas de consumo e estoque.

---

## 📂 Estrutura do Projeto

O projeto está sendo organizado inicialmente utilizando a estrutura padrão de projetos **Maven** para o backend Java.

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
