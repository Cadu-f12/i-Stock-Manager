# DDA Metalúrgica — I-Stock Manager

**Português** | [English](README.md)

O I-Stock Manager é uma aplicação web para gerenciar o estoque de pastilhas industriais utilizadas pela **DDA Metalúrgica**, em Santa Catarina. Centraliza os saldos dos produtos e o histórico de movimentações para ajudar a equipe a manter registros atualizados e apoiar decisões de reposição.

O projeto nasceu no contexto acadêmico do **SENAI/SC**, desenvolvido por Carlos Eduardo, Eliot, Guilherme e Enrico. Seu objetivo é melhorar o acompanhamento feito por planilhas e registros manuais, reduzindo erros e facilitando a consulta ao estoque.

## Funcionalidades disponíveis

- Cadastro de produtos e categorias, edição de produtos e consulta de saldos.
- Entradas e saídas de estoque com identificação do responsável e histórico de alterações.
- Pesquisa por nome, ID ou categoria; filtros por categoria, data de atualização e disponibilidade.
- Ordenação e paginação de 50, 100 ou 200 produtos.
- Administração de usuários e relatórios de estoque para administradores.

## Acesso e perfis

Abra o endereço fornecido pelo responsável pela aplicação e entre com seu e-mail e senha cadastrados. Não há cadastro público de contas. Se o sistema ainda não estiver preparado, o responsável pode seguir [DESENVOLVIMENTO.md](DESENVOLVIMENTO.md).

| Perfil | Ações disponíveis |
| --- | --- |
| Operator | Consultar, cadastrar e editar produtos; registrar entradas/saídas; gerenciar categorias e consultar histórico |
| Admin | Todas as ações de estoque, além de administração de usuários e relatórios |

Use **Sair** para encerrar o acesso. Quando a sessão expirar, entre novamente.

## Usar a aplicação

### Produtos e categorias

Em **Estoque**, escolha **Nova categoria** para organizar os produtos ou mantenha **Sem categoria**. Selecione **Novo produto**, informe nome, categoria e quantidade inteira e salve. Nomes de produto aceitam até 120 caracteres; categorias, até 60.

Use a pesquisa e os filtros para localizar um produto. Abra os detalhes para consultar saldo e histórico ou selecione a ação de edição para atualizá-lo. Excluir uma categoria preserva seus produtos e os move para **Sem categoria**. A categoria padrão não pode ser excluída.

### Entradas e saídas

Use **+** no produto para registrar uma entrada e **−** para registrar uma saída. Informe uma quantidade inteira positiva e confirme. O saldo não pode ficar negativo nem superar 999999999 unidades. Cada operação registra o responsável e aparece no histórico do produto.

Se outra pessoa já tiver alterado o produto, recarregue o estoque antes de tentar novamente. Confira o novo saldo antes de repetir a operação.

### Usuários

Como Admin, abra **Usuários** para cadastrar, consultar, editar, ativar, desativar ou excluir contas. Defina o perfil e o status de cada usuário. Senhas novas exigem ao menos 8 caracteres e no máximo 72 bytes UTF-8; deixar a senha vazia durante a edição mantém a anterior.

Você não pode excluir ou desativar sua própria conta nem remover seu próprio papel de Admin por essa tela. Se a exclusão for impedida por registros vinculados, desative a conta.

### Relatórios

Como Admin, abra **Relatórios**. O gráfico por categoria conta produtos, inclusive aqueles com saldo zero. Os totais mensais somam unidades de entradas e saídas explícitas, agrupadas pelo horário de São Paulo. Use os filtros de ano, categoria e produto para restringir a visão mensal.

Cadastrar um produto ou editar diretamente sua quantidade não conta como entrada ou saída no gráfico mensal. Os filtros de categoria consideram a categoria atual de cada produto.

## Limitações atuais e ajuda

- Alertas de estoque mínimo, fabricantes, fornecedores e motivos de movimentação ainda não estão integrados às telas atuais.
- Não há exclusão de produtos nem exportação de relatórios na interface.
- As pastilhas de exemplo do modelo anterior de estoque não aparecem no estoque atual. Cadastre os produtos pela tela **Estoque**.
- Se o servidor estiver indisponível, contate o responsável pela aplicação. Se uma gravação conflitar com outra alteração, recarregue o estoque; se a sessão expirar, entre novamente.

## Outras documentações

- [DESENVOLVIMENTO.md](DESENVOLVIMENTO.md): preparação do ambiente, Docker, execução local, configuração, arquitetura, APIs, testes e builds.
- [AGENTS.md](AGENTS.md): objetivo, escopo, referências e regras de manutenção para agentes de IA.
