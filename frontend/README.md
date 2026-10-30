# Frontend iStock integrado

## Iniciar no Windows sem Docker nem MySQL

Na raiz do projeto, abra **iniciar-backend.cmd** e **iniciar-frontend.cmd**. Mantenha as duas janelas abertas. Espere o backend mostrar "Started BackendApplication" e o seed terminar.

Acesse http://127.0.0.1:5173 e entre com **admin@istock.com** / **admin**. A senha pode ser alterada na tela de usuários; o seeder preserva contas existentes.

O perfil `standalone` usa H2 persistente em `backend/.local/istock.mv.db`. Os dados permanecem após reiniciar. Esse banco é independente do MySQL; as tabelas de pastilhas continuam separadas dos produtos das telas.

Os scripts usam o JDK 21+ indicado por JAVA_HOME ou pelo arquivo `.local/java-home.txt`, já configurado nesta máquina. Se mover/apagar o JDK, atualize esse caminho. O frontend requer Node.js 24. A chave JWT é gerada na primeira execução e guardada em `.local/jwt-secret.txt`. Esses arquivos locais não entram no Git.

Para encerrar, pressione Ctrl+C em cada janela. Se aparecer "porta em uso", encerre a instância anterior antes de abrir outra. Você também pode executar os scripts em dois terminais PowerShell na raiz:

```powershell
.\iniciar-backend.cmd
# Em outro terminal:
.\iniciar-frontend.cmd
```

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

Na raiz, copie `.env.example` para `.env`, preencha `MYSQL_ROOT_PASSWORD` e `JWT_SECRET` (32 bytes aleatórios codificados em Base64) e execute `docker compose up --build`. O proxy do frontend usa `http://backend:8080` dentro da rede Docker. Aguarde o banco ficar disponível; em uma primeira inicialização, pode ser necessário reiniciar o backend depois disso. Para provisionar o primeiro usuário, com o banco disponível, execute `docker compose run --rm backend ./mvnw spring-boot:run -Dspring-boot.run.arguments=--seed`; após a mensagem de seed finalizado, encerre esse processo e suba os serviços normalmente.

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
3. **Validação:** testes do backend usam H2 e a cadeia real do Spring Security via MockMvc. Não substituem a verificação com o MySQL real e com Docker, que não foram executados neste ambiente.

## Verificação

Frontend: `npm run build`, `npm run lint`, `npm test`.
Backend: `./mvnw.cmd test` (Java 21). Banco H2 e chave JWT exclusivos para testes; não precisa configurar MySQL para os testes.
