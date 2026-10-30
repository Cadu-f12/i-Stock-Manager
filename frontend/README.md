# Frontend iStock integrado

## Iniciar o sistema

Execute o sistema pela raiz com Docker Compose, seguindo o [README principal](../README.md#running-with-docker-compose) ou o [guia em português](../README.pt-BR.md#executar-com-docker-compose). Os guias incluem configuração do ambiente, primeiro acesso e comandos para acompanhar e encerrar os serviços.

React + TypeScript + Vite, conectado ao backend JWT da pasta `../backend`.

## Execução local

Requisitos: Node.js 22.18+ (ou 24), Java 21 e MySQL.

1. Inicie o MySQL e configure as variáveis do backend (`SPRING_MYSQL_HOST`, `SPRING_MYSQL_PORT`, `SPRING_MYSQL_DATABASE`, `SPRING_MYSQL_USERNAME`, `SPRING_MYSQL_PASSWORD`, `JWT_SECRET`). O arquivo `.env` da raiz é lido pelo Docker Compose; Maven não o importa automaticamente.
2. Em `backend`, execute `./mvnw spring-boot:run` (Windows: `./mvnw.cmd spring-boot:run`). Alternativamente, o perfil `local` usa as configurações de desenvolvimento em `application-local.properties`: `./mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"`.
3. Em `frontend`, execute `npm ci` e `npm run dev`.
4. Acesse http://localhost:5173. O proxy encaminha `/api` para http://localhost:8080. Para outro endereço, copie `.env.example` para `.env.local` e ajuste `API_TARGET`.

## Primeiro acesso

O cadastro inicial segue o mecanismo da main: o seeder do backend. Em um banco de desenvolvimento vazio, inicie uma vez com `./mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local" "-Dspring-boot.run.arguments=--seed"`.

O seeder existente cria `admin@istock.com` com senha `admin`, além de dados de exemplo. Altere a senha pela gestão de usuários após entrar. Não há mais cadastro público do primeiro administrador no frontend.

## Docker Compose

Com o Docker Desktop aberto, crie `.env` a partir de `.env.example` caso ainda não exista. Preencha `MYSQL_ROOT_PASSWORD` e `JWT_SECRET` (32 bytes aleatórios codificados em Base64). O banco padrão é `istock`; `MYSQL_DATABASE` pode alterar esse nome.

Encerre eventuais instâncias locais do Spring Boot e Vite para liberar as portas. Na raiz, execute:

```powershell
docker compose up -d --build --wait --wait-timeout 240
```

O Compose espera o MySQL estar pronto antes de iniciar a API, e a API responder antes de iniciar o frontend. Acesse http://127.0.0.1:5173. O login inicial é **admin@istock.com** / **admin**; o seed preserva a conta caso já exista. Para desativar o seed de desenvolvimento, defina `BACKEND_ARGUMENTS=` no `.env`.

O frontend encaminha `/api` para `http://backend:8080`; a API acessa `db:3306` na rede Docker. `SPRING_PORT` e `REACT_PORT` alteram apenas as portas publicadas no computador (padrões 8080 e 5173). O MySQL fica acessível localmente pela porta 3307. Os endereços internos são definidos pelo Compose, independentemente de `MYSQL_HOST`, `MYSQL_PORT` e `MYSQL_ROOT_USERNAME` do `.env`.

Os dados MySQL permanecem em `mysql_data`. O banco H2 do perfil `standalone` é separado e não é migrado automaticamente. Este Compose é para desenvolvimento, com os fontes montados nos contêineres.

```powershell
docker compose ps                 # Estado dos três serviços
docker compose logs -f backend    # Logs da API
docker compose down               # Encerra sem apagar os dados MySQL
```

## Autenticação e permissões

- Login: `POST /api/public/auth/login`, JSON `{ "email": "...", "password": "..." }`.
- Resposta da main preservada: `accessToken` e `tokenType`.
- Token guardado em `sessionStorage`, enviado em `Authorization: Bearer ...`.
- Perfil atual: `GET /api/auth/me`; respostas 401 limpam o token e retornam ao login. A sessão é conferida também ao focar a janela e a cada minuto.
- Logout apaga o token local. Como na main, não há refresh token nem revogação individual; um token copiado continua válido até expirar (15 minutos), salvo remoção/desativação do usuário.
- As autoridades são carregadas do banco em cada requisição JWT. Alterações de papel e desativação passam a valer sem aguardar a expiração.
- A API traduz `ADMIN`/`OPERATOR` para `Admin`/`Operator` nas telas. `/api/admin/**` e as rotas legadas `/user` exigem administrador.

## APIs integradas

- `/api/admin/users`: listar, criar, editar e excluir usuários; BCrypt, validação, proteção contra exclusão/desativação/rebaixamento da própria conta.
- `/api/inventory`: produtos, categorias, histórico e movimentações, com controle de concorrência por versão.
- Os relatórios são calculados no frontend a partir do estoque e histórico retornados pela API.

## Pendências e limites

1. **Unificação do estoque:** as telas usam `inventory_product`, `inventory_category` e `inventory_product_history`, trazidas do projeto frontend. As entidades `Pastille`, `Movement`, `Manufacturer`, `Supplier` e `ReasonMovement` da main foram preservadas e não são sincronizadas com essas tabelas. Portanto, pastilhas criadas pelo seeder não aparecem nas telas. É necessário definir o mapeamento de categorias, fabricantes, fornecedores e motivos antes de migrar para um estoque único.
2. **Implantação:** build gera `dist`; um servidor de produção deve servir esses arquivos e encaminhar `/api` ao backend. O Compose existente continua sendo de desenvolvimento, com Vite e volumes.
3. **Validação:** testes do backend usam H2 e a cadeia real do Spring Security via MockMvc. Para verificar a integração com MySQL, execute também o Compose e confira o login pelo frontend.

## Verificação

Frontend: `npm run build`, `npm run lint`, `npm test`.
Backend: `./mvnw.cmd test` (Java 21). Banco H2 e chave JWT exclusivos para testes; não precisa configurar MySQL para os testes.
