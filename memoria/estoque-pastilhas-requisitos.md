# Domínio: controle de estoque de pastilhas industriais

## Demanda
- Origem: SENAI/SC Fraiburgo, empresa DDA Usinagem Industrial (DDA Metalúrgica); hoje o controle é manual em planilhas
- Não contatar a empresa real; pesquisa própria
- Objetivo final: solução genérica, vendável para qualquer empresa

## Funcionalidades esperadas
- Cadastro de pastilhas, fabricantes e fornecedores; identificação por código/descrição
- Entradas e saídas; consulta de quantidade em tempo real
- Estoque mínimo configurável por item; alertas de estoque crítico; indicar quando e quanto repor
- Histórico de movimentações; painel de acompanhamento
- Relatórios de consumo, movimentação, reposição, desempenho, financeiros e de mercado, com gráficos
- Importação das planilhas atuais para popular o banco
- Integração com ERP/compras: possível no futuro, não obrigatória na v1

## Restrições
- Interface simples e intuitiva; baixo custo de implantação e manutenção
- Compatível com computador e mobile
- Armazenamento seguro; controle de acesso por autenticação

## Escopo da v1.0 do banco (RF01-RF10, RNF01-RNF05, RN01-RN04)
- Apenas controle de estoque; sem vendas, compras e financeiro por ora
- Entidades: Pastilha, Fabricante, Fornecedor, Pastilha_Fornecedor (N:N), Usuario, Motivo_Movimentacao, Movimentacao
- id_fabricante em Pastilha é NOT NULL; toda Pastilha tem ao menos 1 Fornecedor (garantido por trigger/lógica de aplicação)
- Exclusão lógica via campo `ativo` (default true) em Fabricante, Fornecedor, Usuario e Motivo_Movimentacao
- FKs com ON DELETE RESTRICT e ON UPDATE CASCADE
- saldo_atual em Pastilha é denormalizado por desempenho (RNF02); a atualização fica na camada de implementação
- Normas pesquisadas: ISO 1832 e ISO 513
