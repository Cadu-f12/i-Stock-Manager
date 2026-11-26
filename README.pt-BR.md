# DDA Metalúrgica — I-Stock Manager

**Português** | [English](README.md)

O I-Stock Manager é uma aplicação web para gerenciar o estoque de pastilhas industriais utilizadas pela **DDA Metalúrgica**, em Santa Catarina. Centraliza os saldos dos produtos e o histórico de movimentações para ajudar a equipe a manter registros atualizados e apoiar decisões de reposição.

O projeto nasceu no contexto acadêmico do **SENAI/SC**, desenvolvido por Carlos Eduardo, Eliot, Guilherme e Enrico. Seu objetivo é melhorar o acompanhamento feito por planilhas e registros manuais, reduzindo erros e facilitando a consulta ao estoque.

## Funcionalidades disponíveis

- Cadastro de pastilhas por SKU, estoque mínimo e fabricante.
- Entradas e saídas de estoque com identificação do responsável e histórico de alterações.
- Pesquisa por ID, SKU, descrição ou fabricante e filtros de alerta e disponibilidade.
- Ordenação e paginação de 50, 100 ou 200 produtos.
- Administração de usuários e relatórios de estoque para administradores.

## Acesso e perfis

Abra o endereço fornecido pelo responsável pela aplicação e entre com seu e-mail e senha cadastrados. Não há cadastro público de contas. Se o sistema ainda não estiver preparado, o responsável pode seguir [DESENVOLVIMENTO.md](DESENVOLVIMENTO.md).

| Perfil | Ações disponíveis |
| --- | --- |
| Operator | Consultar, cadastrar e editar produtos; registrar entradas/saídas; gerenciar fabricantes e fornecedores e consultar histórico |
| Admin | Todas as ações de estoque, além de administração de usuários e relatórios |

Use **Sair** para encerrar o acesso. Quando a sessão expirar, entre novamente.

## Usar a aplicação

### Pastilhas e movimentações

Em **Produtos**, selecione **Nova pastilha** e informe SKU, fabricante e estoque mínimo. Na aba **Categorias**, a lixeira permite excluir uma categoria sem pastilhas após confirmação. Cadastre categorias na aba **Categorias** e escolha uma ao criar ou editar a pastilha. Marque os fornecedores cadastrados que atendem a pastilha. Os nomes aparecem na tabela de estoque. Use a busca, os filtros, a ordenação e a paginação para localizar o item. Em **Fabricantes** e **Fornecedores**, clique no ícone de lixeira na coluna **Operações** e confirme para excluir um cadastro sem vínculos. Se houver pastilhas vinculadas, altere o fabricante ou remova o fornecedor na edição das pastilhas primeiro.

Em **Produtos → Motivos de movimentação**, cadastre motivos de entrada ou saída. Edite um motivo para renomeá-lo ou reativá-lo. A desativação preserva as movimentações anteriores. Na lista de pastilhas, **Excluir pastilha** remove definitivamente a pastilha e suas movimentações após confirmação, inclusive dos relatórios.

Para registrar uma entrada com motivo de uso único, clique em **+**, selecione **Entrada - Customizada** e escreva o motivo abaixo do seletor. O texto fica no histórico daquela movimentação.

O cadastro inicia com saldo zero. Use **+** ou **−**, selecione o motivo e informe a quantidade. Consulte o responsável e a data em **Histórico**. O filtro de alerta inclui saldos até cinco unidades acima do mínimo.

### Usuários

Como Admin, abra **Usuários** para cadastrar, consultar, editar, ativar, desativar ou excluir contas. Defina o perfil e o status de cada usuário. Senhas novas exigem ao menos 8 caracteres e no máximo 72 bytes UTF-8; deixar a senha vazia durante a edição mantém a anterior.

Você não pode excluir ou desativar sua própria conta nem remover seu próprio papel de Admin por essa tela. Se a exclusão for impedida por registros vinculados, desative a conta.

### Relatórios

Como Admin, abra **Metadados**. O gráfico por categoria conta as pastilhas atuais e lista também as categorias cadastradas sem pastilhas. Os totais mensais somam unidades de entradas e saídas explícitas, agrupadas pelo horário de São Paulo. Em **Entradas e saídas por mês**, selecione **Pastilhas** ou **Estoque anterior** para consultar cada estoque separadamente. Use os filtros de ano, categoria e produto para restringir a visão mensal.

Cadastrar um produto ou editar diretamente sua quantidade não conta como entrada ou saída no gráfico mensal. Os filtros de categoria consideram a categoria atual de cada produto.

## Limitações atuais e ajuda

- A exportação de relatórios está indisponível.
- **Metadados** reúne as movimentações mensais de pastilhas e os gráficos do estoque anterior em uma única página.
- Se o servidor estiver indisponível, contate o responsável pela aplicação. Se uma gravação conflitar com outra alteração, recarregue o estoque; se a sessão expirar, entre novamente.

## Outras documentações

- [DESENVOLVIMENTO.md](DESENVOLVIMENTO.md): preparação do ambiente, Docker, execução local, configuração, arquitetura, APIs, testes e builds.
- [AGENTS.md](AGENTS.md): objetivo, escopo, referências e regras de manutenção para agentes de IA.
