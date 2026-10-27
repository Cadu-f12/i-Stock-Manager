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
 
## Relatórios do administrador

Após entrar como Admin, abra **Relatórios** no menu lateral (`#relatorios`). A tela apresenta uma pizza com a quantidade de produtos cadastrados por categoria e barras com as unidades de entradas e saídas por mês. Selecione o ano, a categoria e/ou o produto para filtrar a comparação mensal; a pizza permanece com a distribuição geral. A tabela expansível permite consultar os valores exatos.

Os relatórios utilizam o histórico persistido do estoque, considerando o horário de Brasília e a categoria atual de cada produto. Somente entradas e saídas explícitas são somadas; cadastro com saldo inicial e edições manuais de quantidade não são movimentações nesse relatório. Meses sem movimentação são exibidos com zero.

Para validar os cálculos no frontend com Node.js 24 ou superior: `node --experimental-strip-types --test --test-isolation=none tests/reportData.test.ts`.

## Primeiro acesso

1. Com o banco de usuários vazio, a tela permite criar o primeiro Admin, com nome, e-mail e senha de pelo menos oito caracteres.
2. Após entrar, abra **Usuários → Novo usuário** e informe nome, e-mail, senha, papel e status.
3. Clique em **Sair** para entrar com a nova conta.

O cadastro inicial é encerrado assim que existe um usuário. Faça essa configuração antes de disponibilizar a instalação na rede. Os usuários fictícios da antiga demonstração não eram gravados no banco e precisam ser cadastrados nesta tela.

Somente Admin pode consultar ou alterar usuários, inclusive pela API. Operator acessa o estoque. Contas inativas ou excluídas não podem entrar; sessões existentes são invalidadas no próximo pedido ao servidor. Alterar a senha também invalida sessões anteriores. A sessão expira após 30 minutos de inatividade. Não é permitido excluir, desativar ou retirar o papel de Admin da própria conta.

Na edição de usuário, a senha em branco mantém a senha atual. Uma nova senha substitui a anterior. Nenhuma senha ou hash é devolvido pela API. Em HTTPS, configure `SESSION_COOKIE_SECURE=true` e encaminhe `/api` ao backend no mesmo domínio do frontend.

Produtos, categorias, saldos e histórico são persistidos no banco pela API `/api/inventory`. O histórico identifica o usuário autenticado no servidor. Entradas e saídas conferem a versão do produto para impedir que operações simultâneas sobrescrevam o saldo. Ao receber um conflito, feche o formulário e use **Atualizar estoque** antes de repetir a operação.

No perfil `local`, os dados ficam em `backend/data/istock.mv.db` quando o backend é iniciado a partir da pasta `backend`. No Docker, os dados ficam no MySQL, no volume `mysql_data`. Os dois ambientes têm bancos independentes; mantenha essas pastas para preservar os cadastros entre reinicializações. Os antigos produtos de demonstração existiam apenas na memória do navegador e não podem ser recuperados depois de recarregar a página.

O cadastro genérico de produtos da interface usa as tabelas `inventory_product`, `inventory_category` e `inventory_product_history`. As tabelas de pastilhas, fabricantes e fornecedores já existentes são preservadas.

## Verificação

```powershell
cd backend
.\mvnw.cmd test
```

Os testes usam H2 isolado em memória e verificam criação inicial, login, CSRF, autorização, duplicação de e-mail, desativação, troca de senha, exclusão e logout. No frontend, execute `npm run build` e `npm run lint`.

Referência da implementação: [sessões](https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html) e [CSRF](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html) do Spring Security.
