# Referência permanente para IA — I-Stock Manager

## Objetivo do projeto

O I-Stock Manager é uma aplicação web para o controle de estoque de pastilhas industriais da DDA Metalúrgica. Seu propósito é centralizar informações hoje acompanhadas em planilhas e registros manuais, reduzir erros de registro, dar visibilidade aos saldos e manter a rastreabilidade das movimentações para apoiar a reposição de materiais.

O público principal é a equipe que consulta e movimenta o estoque e os administradores responsáveis por contas e acompanhamento. Priorize fluxos compreensíveis para essas pessoas, integridade dos saldos e identificação de quem realizou cada alteração. O contexto acadêmico no SENAI/SC não reduz a importância da preservação dos dados e das regras de negócio.

## Escopo e continuidade entre tarefas

- **Implementado:** autenticação e perfis, produtos e categorias, entradas/saídas, histórico, pesquisa/filtros/paginação, administração de usuários e relatórios de estoque.
- **Intenção de domínio ainda incompleta:** controle especializado de pastilhas, estoque mínimo, fabricantes, fornecedores e motivos de movimentação aparecem no modelo legado, mas não estão integrados às telas. Não apresentar essa intenção como funcionalidade pronta.
- **Pendências, não autorização:** unificar os estoques, integrar o modelo legado ou preparar produção exige uma solicitação que inclua esse trabalho e análise de impacto. Não transformar pendências em tarefas implícitas.
- **Limite de expansão:** não ampliar o produto para outros domínios ou substituir sua arquitetura por preferência do agente. Relacione cada mudança solicitada ao objetivo do estoque e preserve o restante do escopo.

Ao iniciar ou retomar uma tarefa, use este arquivo para recuperar o contexto duradouro e confira o código atual. Uma solicitação pontual não redefine por si só o propósito do projeto. Quando o usuário mudar explicitamente o objetivo, escopo ou uma regra, implemente a mudança autorizada e atualize esta referência na mesma tarefa.

Registre aqui decisões duradouras, justificativas, contratos e pendências que afetem trabalhos futuros. Não use este arquivo como diário de prompts ou lista de resultados temporários. Diferencie decisões confirmadas, intenções e pontos não verificados; não deduza novos requisitos apenas de nomes de entidades ou ideias antigas.

## Responsabilidade de cada documento

Esta divisão foi definida pelo usuário e deve ser preservada:

| Documento | Responsabilidade |
| --- | --- |
| `README.md` e `README.pt-BR.md` | Explicar a aplicação, seu público, funcionalidades, limitações visíveis e como utilizá-la. Não incluir preparação de ambiente, comandos de execução, Docker ou detalhes internos |
| `DESENVOLVIMENTO.md` | Centralizar tudo que é necessário para preparar, configurar, executar e desenvolver a aplicação, incluindo Docker, banco de dados, execução local, dependências, arquitetura, APIs, testes, build e diagnóstico |
| `AGENTS.md` | Dar à IA contexto duradouro sobre objetivo, escopo, decisões, mapa do código, contratos, invariantes e procedimento de manutenção, evitando perda de direção entre prompts |
| READMEs de componentes | Breve apresentação e links para os documentos centrais, sem duplicar instruções de execução |

Use links para conectar os documentos. Comandos operacionais completos pertencem a DESENVOLVIMENTO; AGENTS pode indicar as validações relevantes para uma alteração. Preserve as versões em português e inglês do README com o mesmo escopo funcional.

## Escopo e fontes

Aplicação acadêmica para estoque de pastilhas industriais da DDA Metalúrgica. React/TypeScript/Vite em `frontend`; Java 21/Spring Boot/JPA/Security em `backend`. Leia [README.md](README.md), [README.pt-BR.md](README.pt-BR.md) e [DESENVOLVIMENTO.md](DESENVOLVIMENTO.md) conforme a tarefa. Este contrato registra comportamentos confirmados por inspeção; não substitui testes automatizados nem revisão humana.

## Antes de qualquer alteração

1. Leia este `AGENTS.md`; consulte os guias de uso e desenvolvimento conforme a tarefa.
2. Inspecione os arquivos envolvidos e suas dependências.
3. Identifique contratos, regras de negócio, formatos persistidos e comportamento observável afetados.
4. Avalie risco de regressão e testes necessários.
5. Preserve funcionalidades fora do escopo e evite refatorações paralelas.
6. Trate mudanças de comportamento somente dentro do escopo explicitamente autorizado.

Classifique o impacto:

| Nível | Critério |
| --- | --- |
| Baixo | Alteração localizada sem efeitos funcionais ou contratuais previstos |
| Médio | Componentes múltiplos, estado compartilhado ou possíveis efeitos indiretos |
| Alto | Regras de negócio, segurança, persistência, API, formato de dados ou fluxo principal |

Para alto impacto, aprofunde dependências, compatibilidade, migração e regressões antes de editar. Não confunda regra documental com autorização para mudar comportamento.

## Mapa e fluxos

- `frontend/src/main.tsx` → `AuthGate.tsx` → `App.tsx`: autenticação, estado e telas por hash; `api.ts` centraliza HTTP.
- `App.tsx`, `StockMovement.tsx`, `ProductHistory.tsx`, `inventoryData.ts`: produtos, categorias, saldo, versão e histórico.
- `Users.tsx`/`usersData.ts`: gestão administrativa; `Reports.tsx`/`reportData.ts`: agregações do estoque recebido.
- `backend/src/main/java/com/istock/system/security`: JWT, login e autorização.
- `inventory/InventoryController` e `InventoryService`: consulta e operações transacionais de estoque.
- `admin/FrontendUserController`, `AdminUserService`, `ApiErrors`: usuários e erros da API atual.
- `controller/UserController` e `service/UserService`: API legada `/user`, com regras diferentes.
- `entities`, `repository`, `seed/DatabaseSeeder`: modelo legado, usuários compartilhados e dados iniciais.
- `backend/src/main/resources`, `compose.yaml`, Dockerfiles e `scripts/start-local.ps1`: configuração, perfis e execução.

Fluxo principal: login fornece JWT; frontend consulta perfil e estoque; alteração envia versão; serviço valida, grava saldo e histórico na mesma transação e devolve produto atualizado; só então o frontend substitui seu estado. Em conflito, preserve o erro e a opção de recarregar os dados.

## Invariantes a preservar salvo solicitação explícita

### Estoque e histórico

- `InventoryService.quantity/move`: saldo inteiro de 0 a 999999999; movimentos positivos inteiros até esse limite. Não permitir saldo negativo nem estouro do limite.
- `checkVersion` e `InventoryProduct.@Version`: edição e movimentação exigem versão atual; conflito retorna 409. Não remover o controle de concorrência nem sobrescrever silenciosamente versões obsoletas.
- `save`: nome de produto até 120 caracteres; categoria até 60 em `createCategory`; aparar e condensar espaços. Edição sem alteração não cria evento.
- `normalize`/`InventoryCategoryRepository`: categorias únicas sem acentos/distinção de caixa, com restrição única no banco e lock na consulta. Não depender somente de validação no navegador.
- `Sem categoria` representa categoria nula e não pode ser excluída. Excluir outra categoria preserva produtos, saldos e histórico, reclassificando-os e registrando a alteração de forma transacional.
- `record`: autor e ID vêm da autenticação; data de atualização em `America/Sao_Paulo`, eventos em `Instant`, mais recentes primeiro. Não aceitar autor arbitrário enviado pelo cliente.
- O histórico expõe `changes` como lista de linhas. `reportData.movementAmount` depende dos textos `Entrada de ... unidade(s): ...` e `Saída de ... unidade(s): ...`, inclusive quantidades em notação BigDecimal. Mudar esse formato exige analisar dados já persistidos e testes de compatibilidade.
- Relatório mensal exclui cadastro e edição direta de quantidade. Gráfico de categorias conta produtos, não unidades, e inclui saldo zero. Agrupamento mensal usa São Paulo e filtros usam categoria atual.

### Autenticação e usuários

- `JwtConfig/JwtService`: HS256, chave Base64 de exatamente 32 bytes, validação de emissor e tempo; subject por e-mail. Validade configurada atualmente em 900 segundos.
- `SecurityConfig`: `/api/public/**` público; `/api/admin/**` e `/user`/`/user/**` exigem ADMIN; demais requisições exigem autenticação. Contas e autoridades são relidas do banco em cada JWT.
- Senhas usam BCrypt e nunca são retornadas: `User.password` é somente de entrada e `UserView` não a inclui.
- `AdminUserService`: papéis externos `Admin`/`Operator`, internos `ADMIN`/`OPERATOR`; e-mail normalizado e único sem diferenciar caixa; nome até 120, e-mail até 60, status obrigatório. Senha nova tem mínimo de 8 caracteres e máximo de 72 bytes UTF-8; vazia/ausente na edição mantém a anterior.
- Pela API administrativa, impedir autoexclusão, autodesativação e remoção do próprio papel Admin. Essas garantias **não se aplicam automaticamente** à API legada; não documentá-la como equivalente.
- `api.ts`: token em `sessionStorage` sob `istock.accessToken`; JSON e Bearer centralizados; login não envia Bearer. Em 401, limpar somente o token ainda correspondente à requisição; 403 não encerra sessão.
- `AuthGate`: revalidar ao focar e a cada minuto, limpar produtos/categorias ao sair ou expirar. Logout não revoga token no servidor; troca de senha também não implementa revogação.
- Relatórios e usuários são exibidos a Admin; Operator pode consultar e modificar estoque pela API. Ocultar navegação não substitui autorização no backend.

### Persistência e compatibilidade

- `inventory_*` e entidades legadas `Pastille/Movement/Manufacturer/Supplier/ReasonMovement` não são sincronizados. Não unificar nem migrar dados incidentalmente.
- Seed depende de `--seed`, preserva conta inicial existente e pula grupos legados já preenchidos; não cria produtos na interface atual. Compose/script Windows habilitam seed por padrão.
- MySQL em `mysql_data` e H2 em `backend/.local` são bases independentes. Não apagar dados para resolver erro de ambiente; planejar backup/migração para alterações de esquema.
- `ddl-auto=update` não equivale a migração versionada. Flyway existe como dependência, mas não há scripts de migração identificados.
- Não versionar `.env`, chaves, tokens, dados locais ou dumps privados. Não reproduzir os valores fixos do perfil `local` em documentação. Separar sua correção de tarefas exclusivamente documentais.
- Preserve os formatos JSON e rotas enumerados em `DESENVOLVIMENTO.md`, inclusive `version`, nomes de papéis e erros com `message`. Não inventar endpoint de exclusão de produto, refresh token ou exportação de relatórios.

## Validação por área

| Área alterada | Verificação relevante |
| --- | --- |
| Cliente HTTP, autenticação frontend | `npm test` em `frontend`; conferir 401/403, login e token substituído |
| Relatórios e histórico | `npm test`; fronteiras de mês/fuso, filtros, saldo zero, edição versus movimentação e formato BigDecimal |
| Componentes React/estilos | `npm run lint`, `npm run build`; verificar visualmente fluxo alterado, erros e permissões |
| API, estoque, segurança e usuários | `./mvnw test` ou `.\mvnw.cmd test` em `backend`, com JDK 21; ampliar casos conforme regra afetada |
| Esquema, locks ou configuração MySQL | Testes H2 e verificação específica em MySQL; revisar migração e backups |
| Docker/scripts | Conferir variáveis sem expor valores, `docker compose config --quiet` com ambiente configurado e inicialização quando disponível |
| Documentação | Caminhos, links, comandos nos manifests e `git diff --check` |

Os testes existentes não cobrem todas as invariantes: há teste de contexto, integração MockMvc e cinco testes Node; não foi identificado teste de navegador, migração ou corrida entre processos. Não afirmar validação MySQL apenas com H2. Não adicionar testes que apenas reproduzem documentação ou alterações triviais sem comportamento.

## Depois de qualquer alteração

1. Revise o diff e procure mudanças inesperadas.
2. Execute testes adequados quando possível; documente testes não executados e limitações.
3. Confira invariantes e contratos afetados.
4. Avalie os três documentos e atualize os afetados na mesma tarefa: README para uso/funcionalidades; DESENVOLVIMENTO para arquitetura/configuração/build; AGENTS para regras/contratos/manutenção. Mantenha a versão portuguesa e guias dos componentes coerentes.
5. Descreva o que mudou, por quê, e o que foi ou não validado.

Se a solicitação mudar explicitamente um comportamento, atualize a regra e remova descrições obsoletas. Preserve conteúdo válido e específico; não altere código em uma tarefa exclusivamente documental.

## Limitações e decisões pendentes

Confirmado nos fontes: coexistência de estoques, relatórios baseados em texto, ausência de revogação individual, validações diferentes nas APIs de usuários e Compose de desenvolvimento. Não verificados nesta revisão: implantação em produção, migração H2/MySQL, concorrência entre instâncias e unidade acadêmica exata (os documentos históricos divergem). Resultados de execução da revisão estão em `DESENVOLVIMENTO.md`.
