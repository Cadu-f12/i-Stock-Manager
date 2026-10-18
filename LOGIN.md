# Login e cadastro de usuários

O login usa os usuários persistidos pelo backend Spring Boot. As senhas são armazenadas com BCrypt, a sessão usa cookie HttpOnly e as operações de escrita exigem CSRF. Não há senha padrão.

## Executar localmente

Requisitos: Java 21 e Node.js 24. Em dois terminais:

```powershell
cd backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

```powershell
cd frontend
npm install
npm run dev
```

Abra `http://localhost:5173`. O perfil `local` grava os usuários no banco H2 em `backend/data/`, preservando-os após reiniciar o servidor. O perfil padrão mantém a conexão MySQL existente. O Docker Compose configura o proxy do frontend para o serviço `backend`.

## Primeiro acesso

1. Com o banco de usuários vazio, a tela permite criar o primeiro Admin, com nome, e-mail e senha de pelo menos oito caracteres.
2. Após entrar, abra **Usuários → Novo usuário** e informe nome, e-mail, senha, papel e status.
3. Clique em **Sair** para entrar com a nova conta.

O cadastro inicial é encerrado assim que existe um usuário. Faça essa configuração antes de disponibilizar a instalação na rede. Os usuários fictícios da antiga demonstração não eram gravados no banco e precisam ser cadastrados nesta tela.

Somente Admin pode consultar ou alterar usuários, inclusive pela API. Operador acessa o estoque. Contas inativas ou excluídas não podem entrar; sessões existentes são invalidadas no próximo pedido ao servidor. Alterar a senha também invalida sessões anteriores. A sessão expira após 30 minutos de inatividade. Não é permitido excluir, desativar ou retirar o papel de Admin da própria conta.

Na edição de usuário, a senha em branco mantém a senha atual. Uma nova senha substitui a anterior. Nenhuma senha ou hash é devolvido pela API. Em HTTPS, configure `SESSION_COOKIE_SECURE=true` e encaminhe `/api` ao backend no mesmo domínio do frontend.

O estoque ainda usa dados de demonstração em memória. Seu histórico identifica automaticamente o usuário autenticado e é preservado ao trocar de conta na mesma página, mas produtos e histórico continuam sendo reiniciados ao recarregar a página. Usuários e senhas são persistentes.

## Verificação

```powershell
cd backend
.\mvnw.cmd test
```

Os testes usam H2 isolado em memória e verificam criação inicial, login, CSRF, autorização, duplicação de e-mail, desativação, troca de senha, exclusão e logout. No frontend, execute `npm run build` e `npm run lint`.

Referência da implementação: [sessões](https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html) e [CSRF](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html) do Spring Security.
