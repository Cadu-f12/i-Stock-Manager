# I-Stock Manager (Equipe 67)

## Equipe e papel
- Trabalho em grupo (Equipe 67): Guilherme M.T., Carlos E. C., Eliot Eloi e Enrico F.H.
- Parte do Enrico: front-end, junto com um colega que já começou

## Gestão
- Ferramenta: Jira, projeto "I-Stock manager", esforço estimado pela sequência de Fibonacci
- Cerimônias: Daily às 7:30 todos os dias; Planning ao final de cada Sprint
- Desafio 1: plano de projeto ágil para Sistema de Gestão de Vendas (empresa XYZ), entrega 13/09/2026
- Desafio 2: plano de desenvolvimento de sistema de pedidos online para um restaurante, entrega 15/09/2026
- O projeto real do semestre (controle de estoque de pastilhas da DDA Metalúrgica) é a base desses exercícios

## Protótipo (Figma)
- Telas: login, estoque, colaboradores, pesquisa/operação de produto
- Planejada: tela de relatórios/metadados com relatórios de uso, entrada/saída, estoque mínimo e consumo/venda diário, mensal e anual

## API e segurança
- Contrato OpenAPI 3.0 (API First), Spring Boot, JWT Bearer
- Perfis: ADMINISTRADOR, GESTOR, OPERADOR
- Entrega 2 EaD (Spring Security): Plano e Matriz de Segurança da API (matriz de recurso/operação, ameaça, impacto e controle; plano de segurança; perfis e permissões), que deve orientar a implementação posterior

## Código
- Em 27/10/2026 o Enrico enviou ao chat do Claude o repositório (zip) com o estado atual do código
- Decisão: o modelo de banco de dados é a fonte de verdade para as classes de domínio Java; o openapi.yaml vale como DTOs de API, em camada separada
- Decisão: superclasse abstrata para id/ativo nas entidades, com um único nível de herança
