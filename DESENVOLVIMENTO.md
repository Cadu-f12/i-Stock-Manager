# Desenvolvimento do I-Stock Manager

Este guia descreve o código inspecionado em 09/11/2026. **Confirmado** significa verificado nos fontes; resultados de execução estão separados ao final. Consulte também [README](README.md), [guia de uso em português](README.pt-BR.md) e [contrato de manutenção](AGENTS.md).

## Arquitetura e arquivos

O navegador executa React e chama `/api` pelo cliente `frontend/src/api.ts`. Vite encaminha as chamadas ao Spring Boot. Controllers delegam aos serviços, que persistem por JPA/Hibernate em MySQL ou H2. Não há serviço externo de autenticação.

```text
frontend/
  src/main.tsx                 Entrada React e AuthGate
  src/AuthGate.tsx             Login, sessão e limpeza de estado
  src/App.tsx                  Estoque, filtros, paginação e navegação por hash
  src/api.ts                   JSON, Bearer e tratamento de falhas HTTP
  src/StockMovement.tsx        Formulário de entrada/saída
  src/ProductHistory.tsx       Exibição do histórico
  src/Users.tsx                Administração de usuários
  src/Reports.tsx              Gráficos e filtros dos relatórios
  src/reportData.ts            Agregações e leitura das descrições do histórico
  tests/                      Testes Node do cliente HTTP e relatórios
  vite.config.ts              Proxy /api no dev e preview
backend/
  pom.xml                     Dependências e build Maven
  src/main/java/com/istock/system/
    BackendApplication.java   Entrada Spring Boot
    security/                 Login, JWT, BCrypt e autorização
    inventory/                Estoque usado pelo frontend
    admin/                    API administrativa e erros de domínio
    controller/UserController.java  API legada /user
    service/UserService.java        Serviço legado de usuários
    entities/, repository/    Usuários e modelo legado de pastilhas
    dtos/                     DTOs de autenticação e modelo legado
    seed/DatabaseSeeder.java  Dados de desenvolvimento com --seed
  src/main/resources/         Perfis de banco e JWT
  src/test/                   Integração MockMvc e contexto Spring
scripts/start-local.ps1       Inicialização Windows com H2 ou Vite
compose.yaml                  MySQL, backend e Vite de desenvolvimento
```

## Dependências declaradas

| Componente | Versão/configuração no repositório |
| --- | --- |
| Java | 21 no `pom.xml` e imagem `eclipse-temurin:21-jdk` |
| Spring Boot | 4.1.1; Web MVC, Data JPA, Security e OAuth2 Resource Server |
| Maven Wrapper | Maven 3.9.16, wrapper 3.3.4 |
| Persistência | MySQL Connector/J, H2 runtime, Flyway Core; versões geridas pelo Spring Boot |
| React / React DOM | `^19.1.0` |
| TypeScript / Vite | `~5.8.3` / `^6.3.5` |
| ESLint | `^9.25.0`, plugins React Hooks e React Refresh |
| Node | Docker usa `node:24`; testes usam remoção nativa de tipos TypeScript |
| MySQL | Imagem `mysql` sem versão fixada |

O frontend usa `package-lock.json` e `npm ci`. Não há pipeline de CI versionado identificado. As imagens sem versão exata podem mudar entre builds.

## Preparação e execução

### Docker Compose

#### 1. Pré-requisitos

Instale e abra o Docker Desktop (ou Docker Engine com o plugin Compose no Linux). No Windows, use contêineres Linux. Execute os comandos na raiz do projeto, onde está `compose.yaml`.

Java, Maven, Node.js e MySQL são executados nos contêineres; não precisam estar instalados no computador. Libere as portas **5173**, **8080** e **3307**, encerrando eventuais instâncias locais do Vite ou Spring Boot.

```sh
docker version
docker compose version
```

#### 2. Configurar o ambiente

Se ainda não houver `.env`, copie o exemplo. Preserve o arquivo caso ele já exista:

```powershell
# Windows / PowerShell
Copy-Item .env.example .env
```

```sh
# Linux / macOS
cp .env.example .env
```

Preencha no `.env`:

```dotenv
MYSQL_DATABASE=istock
MYSQL_ROOT_PASSWORD=substitua_pela_senha_do_banco
JWT_SECRET=substitua_pela_chave_base64_gerada
SPRING_PORT=8080
REACT_PORT=5173
```

Gere a chave JWT uma vez e copie a saída para `JWT_SECRET`. Ela deve conter exatamente 32 bytes codificados em Base64. No PowerShell:

```powershell
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
try { $rng.GetBytes($bytes) } finally { $rng.Dispose() }
[Convert]::ToBase64String($bytes)
```

No Linux/macOS com OpenSSL:

```sh
openssl rand -base64 32
```

Mantenha o `.env` privado e reutilize a chave ao reiniciar. Se `mysql_data` já existir, use a senha originalmente configurada para esse banco; alterar a variável não altera a senha armazenada pelo MySQL.

#### 3. Iniciar o sistema

```sh
docker compose config --quiet
docker compose up -d --build --wait --wait-timeout 240
```

O Compose aguarda o MySQL ficar pronto, inicia a API e depois o frontend. A primeira construção baixa dependências e pode levar alguns minutos. Os serviços ficam em segundo plano; você pode fechar o terminal.

Acesse **http://127.0.0.1:5173**. Login inicial de desenvolvimento:

- E-mail: **admin@istock.com**
- Senha: **admin**

O seed executa na inicialização e preserva contas existentes. Caso a senha já tenha sido alterada, use a senha atual. Para desativar o seed, adicione `BACKEND_ARGUMENTS=` ao `.env`.

O frontend encaminha `/api` para `backend:8080`; a API conecta a `db:3306`. Esses endereços internos são definidos pelo Compose. `MYSQL_HOST`, `MYSQL_PORT` e `MYSQL_ROOT_USERNAME` de arquivos antigos não são usados pelo Compose. `SPRING_PORT` e `REACT_PORT` controlam as portas do computador; o MySQL é publicado em `127.0.0.1:3307`.

#### 4. Verificar, encerrar e iniciar novamente

```sh
docker compose ps
docker compose logs -f backend
docker compose logs -f frontend
docker compose down
```

Os três serviços devem aparecer como `healthy`. Ctrl+C encerra somente a visualização dos logs. `docker compose down` encerra o sistema e preserva os dados MySQL em `mysql_data`.

Para iniciar novamente ou reconstruir após alterações no código/dependências:

```sh
docker compose up -d --build --wait --wait-timeout 240
```

O banco H2 do modo standalone, em `backend/.local`, é separado e não é migrado para o MySQL. Este Compose é para desenvolvimento e monta os fontes do projeto nos contêineres.

Se a inicialização falhar, consulte `docker compose logs --tail 100 db backend frontend`. Para conflitos de porta, encerre o processo que ocupa a porta indicada. Para chave JWT inválida, gere uma chave de 32 bytes em Base64. Se apenas o tempo de espera se esgotar, confira os logs e execute novamente `docker compose up -d --wait --wait-timeout 240`.

O healthcheck MySQL executa `SELECT 1`; o backend é considerado pronto quando `/api/auth/me` sem token retorna 401; o frontend quando sua página responde com sucesso. Isso não substitui um teste funcional de login e estoque. Compose usa os fontes montados, cache Maven do host, volume `backend_target` e dependências frontend no contêiner.

### Windows sem MySQL ou Docker

Com JDK 21 e Node.js 24 instalados, abra dois terminais na raiz:

```powershell
.\scripts\start-local.ps1 -Component backend
```

```powershell
.\scripts\start-local.ps1 -Component frontend
```

Mantenha os terminais abertos. O backend usa `standalone` e `--seed`; o frontend instala dependências se Vite estiver ausente. O script verifica as portas 8080/5173, procura JDK em `JAVA_HOME`, `.local/java-home.txt` ou no compilador disponível e gera uma chave persistente em `.local/jwt-secret.txt`. O banco fica em `backend/.local/istock`. O script usa as autoridades certificadoras do Windows para Node e Maven; não desabilita TLS. Esses arquivos locais não devem ser versionados.

### Execução manual

No backend, configure as variáveis do perfil escolhido no terminal/IDE. Maven não importa o `.env` da raiz automaticamente.

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Para H2, forneça `JWT_SECRET` e selecione o perfil explicitamente:

```powershell
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.profiles=standalone' '-Dspring-boot.run.arguments=--seed'
```

Em outro terminal, a partir da raiz:

```powershell
cd frontend
npm ci
npm run dev
```

Em Linux/macOS, use `./mvnw` no lugar de `.\mvnw.cmd`. O proxy padrão é `http://localhost:8080`; configure `API_TARGET` em `frontend/.env.local` para outro backend. Acesso padrão: `http://localhost:5173`.

### Configuração e perfis

| Contexto | Nomes e comportamento |
| --- | --- |
| Compose, `.env` da raiz | `MYSQL_DATABASE`, `MYSQL_ROOT_PASSWORD`, `JWT_SECRET`, `SPRING_PORT`, `REACT_PORT`, `BACKEND_ARGUMENTS` |
| Compatibilidade do exemplo | `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_ROOT_USERNAME` estão no exemplo, mas não são consumidos pelo Compose atual |
| Backend padrão | `SPRING_MYSQL_HOST`, `SPRING_MYSQL_PORT`, `SPRING_MYSQL_DATABASE`, `SPRING_MYSQL_USERNAME`, `SPRING_MYSQL_PASSWORD`, `JWT_SECRET` |
| Perfil `local` | MySQL em localhost:3307/istock; contém senha e chave JWT fixas de desenvolvimento. Não reproduzir esses valores nem usar esse perfil em produção |
| Perfil `standalone` | H2 persistente relativo ao diretório de execução, JWT por ambiente, bind em 127.0.0.1:8080; Flyway e console H2 desativados |
| Frontend | `API_TARGET` consumido por `vite.config.ts`; não é uma chave JWT nem segredo do navegador |

Compose fixa a rede interna em `db:3306`, `backend:8080` e Vite 5173; publica apenas em loopback, com MySQL na porta 3307. `SPRING_PORT`/`REACT_PORT` alteram portas do host. Não edite `application.properties` apenas para trocar de ambiente: use variáveis/perfis. Para sobrescrever valores fixos do perfil `local`, use propriedades Spring correspondentes, como `SPRING_DATASOURCE_PASSWORD` e `APP_JWT_SECRET`.

## Contratos HTTP

Corpos e respostas são JSON. Login público; demais chamadas exigem `Authorization: Bearer <token>`. A API retorna 200 nas operações bem-sucedidas dos controllers atuais, inclusive criação; exclusão de usuário retorna corpo vazio.

| Método e rota | Entrada / saída e acesso |
| --- | --- |
| `POST /api/public/auth/login` | `{email,password}` → `{accessToken,tokenType}` |
| `GET /api/auth/me` | Usuário atual `{id,name,email,active,role}` |
| `GET /api/admin/users` | Lista de usuários; Admin |
| `POST /api/admin/users` | `{name,email,active,role,password}` → usuário; Admin |
| `PUT /api/admin/users/{id}` | Mesmo formato; senha vazia/ausente mantém a anterior; Admin |
| `DELETE /api/admin/users/{id}` | Exclui conta; Admin |
| `GET /api/inventory` | `{products,categories}`; qualquer conta autenticada ativa |
| `POST /api/inventory/products` | `{name,category,quantity}` → produto |
| `PUT /api/inventory/products/{id}` | `{name,category,quantity,version}` → produto |
| `POST /api/inventory/products/{id}/movements` | `{direction,quantity,version}`, direção `in` ou `out` → produto |
| `POST /api/inventory/categories` | `{name}` → `{name}` |
| `DELETE /api/inventory/categories?name=...` | Nome codificado para URL → estoque atualizado |

Produto: `{id,version,name,category,quantity,updatedAt,history}`. `updatedAt` é data `YYYY-MM-DD`; cada histórico contém `{author,userId,at,changes}`, com instante ISO e lista de descrições. Histórico mais recente primeiro. Não há endpoint de exclusão de produto nem paginação no servidor.

`ApiErrors` converte validações em 400, ausências em 404 e conflitos de saldo, categoria, duplicidade ou versão em 409, com `{message}`. A segurança responde 401/403 com `{status,error,message}`. O cliente trata também indisponibilidade 502/503/504 e falha de rede. Em 409, recarregue o estoque antes de tentar salvar novamente.

A API legada exige Admin: `GET /user`, `GET /user/{id}`, `POST /user/createUser`, `POST /user/fetchUser`, `PUT /user/updateUser/{id}`, `DELETE /user/deleteUser/{id}`. Ela usa `UserService` e a entidade `User`, com validações diferentes de `AdminUserService`; não pressupor proteção contra autoexclusão nem a mesma política de senhas. A senha tem serialização somente de entrada. O frontend usa exclusivamente a API `/api`.

## Autenticação e usuários

JWT HS256 exige exatamente 32 bytes após decodificação Base64, emissor `istock-api` e validade configurada de 900 segundos. O subject é o e-mail. Autoridades e conta ativa são consultadas no banco em cada requisição; mudança de papel/desativação afeta tokens existentes. Alterar o e-mail torna o subject anterior inválido. Trocar a senha não revoga um token já emitido.

O navegador mantém `istock.accessToken` em `sessionStorage`. `AuthGate` consulta `/auth/me` ao iniciar, focar a janela e a cada minuto. `api.ts` limpa a sessão em 401 autenticado apenas se o token da requisição ainda for o atual; 403 não encerra sessão. Logout é local; não há refresh token ou revogação individual.

`AdminUserService` normaliza e-mail para minúsculas, verifica duplicidade sem diferenciar caixa, exige nome até 120 caracteres, e-mail até 60, status informado e papel `Admin` ou `Operator`. No banco, os papéis são `ADMIN`/`OPERATOR`. Senha nova exige pelo menos 8 caracteres e no máximo 72 bytes UTF-8, com BCrypt. A própria conta não pode ser excluída, desativada ou rebaixada por essa API.

## Estoque, relatórios e persistência

`InventoryService` é transacional. Saldo inteiro entre 0 e 999999999; movimentação inteira entre 1 e 999999999. Saldo insuficiente/excedido gera 409. Atualizações e movimentos exigem a versão recebida na consulta, além de `@Version` para concorrência otimista. Nomes de produto/categoria são aparados e têm espaços consecutivos reduzidos, com limites de 120/60 caracteres.

Categorias usam unicidade por nome normalizado sem acentos e sem distinção de caixa, índice único e consulta com lock pessimista. `Sem categoria` representa associação nula, sempre integra a listagem e não pode ser excluída. Excluir categoria move os produtos para essa associação nula e registra histórico na mesma transação. Edição sem mudanças não acrescenta histórico.

O autor e seu ID vêm da conta autenticada, nunca do corpo da requisição. A data de atualização usa `America/Sao_Paulo`; o evento armazena `Instant`. A descrição persistida é dividida em linhas ao montar `changes`.

Relatórios são calculados no navegador a partir do estoque completo. O gráfico por categoria conta produtos, inclusive com saldo zero; o mensal soma unidades apenas de descrições explícitas `Entrada de ... unidade(s): ...` / `Saída de ... unidade(s): ...`. Cadastro e edição direta de saldo não entram nessa soma. Filtros usam a categoria atual do produto, não uma categoria histórica. O agrupamento mensal usa `America/Sao_Paulo`. A tela de relatórios aparece apenas para Admin, mas os dados de estoque/histórico também estão acessíveis a Operator na API.

Existem dois modelos sem sincronização:

- `inventory_product`, `inventory_category`, `inventory_product_history`: usados nas telas.
- `Pastille`, `Movement`, `Manufacturer`, `Supplier`, `ReasonMovement`: modelo legado com SKU, estoque mínimo, fabricantes, fornecedores e motivos. Possui entidades/repositórios, mas não controllers de estoque equivalentes aos de `inventory`.

O seed só executa com `--seed`; Compose e o script Windows fornecem esse argumento por padrão. Preserva a conta inicial quando seu e-mail já existe; para fabricantes, fornecedores, motivos e pastilhas, pula cada grupo se a tabela já contém registros. Não preenche `inventory_*`.

Hibernate usa `ddl-auto=update`. Flyway consta nas dependências, mas não há scripts de migração versionados identificados. Não há migração automática entre H2 e MySQL nem entre modelos de estoque. Faça backup antes de mudanças de esquema; não remova `mysql_data` ou `backend/.local` como solução para falhas de inicialização.

## Validação, build e distribuição

Em `frontend`:

```powershell
npm test
npm run lint
npm run build
npm run preview
```

Em `backend`, com JDK 21:

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
```

O build frontend gera `frontend/dist`; Maven gera `backend/target/system-0.0.1-SNAPSHOT.jar`. Para executar o JAR, configure o ambiente e use `java -jar target/system-0.0.1-SNAPSHOT.jar` dentro de `backend`. Produção requer servidor para `dist` e encaminhamento `/api` à API. O Compose atual executa Maven/Vite de desenvolvimento; não é empacotamento de produção.

`FrontendIntegrationTests` usa H2 e MockMvc com Spring Security real: login, perfis, token inválido, versão obsoleta, movimentação, troca de senha, conta inativa e excluída. `BackendApplicationTests` verifica inicialização do contexto. Testes frontend cobrem cabeçalhos JSON/Bearer, 401/403, categorias e cálculos mensais.

Na revisão documental de 09/11/2026, **`npm test` passou: 5 testes**, com Node v26.12.1. Build, lint, testes Java, Docker e validação visual não foram executados nesta revisão; os comandos acima foram conferidos nos arquivos de configuração. Não foi localizado JDK 21 nem Docker entre os comandos disponíveis no PATH consultado. H2 nos testes não comprova compatibilidade operacional com MySQL.

## Pendências conhecidas

- Unificar o modelo de estoque exige definir mapeamento e migração, preservando IDs, saldos e histórico.
- Descrições de histórico são contrato dos relatórios; mudar texto exige compatibilidade com registros anteriores.
- A API legada de usuários não tem as mesmas proteções da API administrativa.
- O perfil `local` contém valores fixos sensíveis de desenvolvimento; sua remoção/rotação exige tarefa de código/configuração própria.
- Não há teste automatizado de navegador, migração ou concorrência real entre processos identificado.
- A documentação histórica cita tanto Fraiburgo quanto Florianópolis para o contexto acadêmico. Unidade exata não verificada; equipe registrada: Carlos Eduardo, Eliot, Guilherme e Enrico.
