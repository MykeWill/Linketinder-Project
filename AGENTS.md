# AGENTS.md

## Visão geral

Linketinder combina a ideia de perfis profissionais do LinkedIn com a dinâmica de interesse mútuo do Tinder: candidatos podem curtir vagas, empresas podem curtir candidatos, e o match representa o interesse dos dois lados.

O repositório contém um backend Groovy com JDBC/PostgreSQL, uma interface frontend em TypeScript/Vite que mantém dados em `localStorage` e scripts SQL versionados. O backend também possui interface de terminal e servidor HTTP.

## Stack existente

- Backend: Groovy 4.0.29, JDK 21 e Gradle 9.7.0 pelo Gradle Wrapper.
- Banco no backend: PostgreSQL via JDBC, driver `org.postgresql:postgresql:42.7.4`.
- Testes do backend: Spock 2.3 para Groovy 4.0 e Byte Buddy 1.14.11.
- Outras dependências Gradle: Guava 33.5.0-jre.
- Frontend: TypeScript ~6.0.2, Vite ^8.2.2 e Chart.js ^4.5.1.
- O servidor HTTP do backend usa `com.sun.net.httpserver` do JDK; o JSON usa `groovy.json`.

## Estrutura de diretórios

- `app/src/main/groovy/com/linketinder/`: aplicação backend.
  - `model/`: modelos de domínio.
  - `repository/`: contratos de acesso a dados.
  - `dao/`: implementações JDBC e conexão.
  - `service/`: operações e regras da aplicação.
  - `controller/`: encaminhamento entre interfaces e services.
  - `menu/`: interação de terminal.
  - `server/` e `server/handler/`: servidor HTTP e handlers.
  - `exception/`: exceções e mensagens de erro.
- `app/src/main/resources/`: configuração de banco em `database.properties`.
- `app/src/test/groovy/com/linketinder/`: testes Spock.
- `frontend/src/`: aplicação TypeScript.
  - `pages/`, `repository/`, `service/`, `types/` e `utils/`: telas, persistência local, serviços, tipos e validações.
- `frontend/public/`: recursos estáticos.
- `sql/`: schema, carga de dados e consultas SQL.
- `docs/`: imagem DER do projeto.
- `gradle/` e `gradlew`: configuração e wrapper do Gradle.

## Como executar e testar

Comandos definidos ou suportados pela configuração atual:

- Executar o backend: `./gradlew run --console=plain`
- Executar os testes do backend: `./gradlew test`
- Compilar o frontend: `cd frontend && npm run build`
- Servir o frontend em desenvolvimento: `cd frontend && npm run dev`
- Pré-visualizar o build do frontend: `cd frontend && npm run preview`

O `package.json` do frontend não define script de testes. O backend depende da configuração PostgreSQL indicada em `app/src/main/resources/database.properties`.

## Convenções observadas

- Código Groovy organizado sob o pacote `com.linketinder`, com subpacotes por camada.
- Classes Groovy usam nomes PascalCase; métodos e propriedades usam camelCase.
- Interfaces TypeScript de modelos usam nomes PascalCase terminados em `Interface`; funções e propriedades usam camelCase.
- Testes Groovy usam Spock e nomes de arquivo `*Spec.groovy`.
- Services recebem contratos de repository; DAOs implementam esses contratos e acessam o banco via JDBC.
- Os repositories TypeScript do frontend usam `localStorage`; não são uma camada de conexão com o backend.

## Domínio do Linketinder

### Modelos Groovy

- `Pessoa` é uma interface com os métodos `getNome()`, `getEmail()`, `getEstado()`, `getCep()`, `getDescricao()` e `exibirDetalhes()`.
- `PessoaBase` é abstrata e contém `nome`, `email`, `estado`, `cep` e `descricao`.
- `Candidato` herda de `PessoaBase` e contém `id`, `cpf`, `idade`, `senha` e `competencias`.
- `Empresa` herda de `PessoaBase` e contém `id`, `cnpj`, `pais` e `senha`.
- `Competencia` contém `id` e `nome`.
- `Vaga` contém `id`, `empresaId`, `nome`, `descricao`, `local` e `competencias`.
- `Match` representa os dados pós-match permitidos da empresa, do recrutador executor e do candidato.
- Formação: não implementada no estado atual.
- Experiência: não implementada no estado atual.

### Tabelas SQL e relações

- `candidato`: `id`, `nome`, `email`, `cpf`, `idade`, `estado`, `cep`, `descricao`, `senha`.
- `empresa`: `id`, `nome`, `cnpj`, `email`, `descricao`, `pais`, `estado`, `cep`, `senha`.
- `competencia`: `id`, `nome`.
- `vaga`: `id`, `empresa_id`, `nome`, `descricao`, `local`.
- `candidato_competencia`: `candidato_id`, `competencia_id` (relação N:N entre candidato e competência).
- `vaga_competencia`: `vaga_id`, `competencia_id` (relação N:N entre vaga e competência).
- `curtida_candidato_vaga`: `candidato_id`, `vaga_id`, `data_hora`.
- `curtida_empresa_candidato`: `empresa_id`, `candidato_id`, `data_hora`.
- Cada vaga referencia uma empresa; as tabelas de relação e curtida têm chaves estrangeiras para os registros correspondentes. O schema declara exclusão em cascata nessas referências.

No frontend, `CandidatoInterface` contém `id`, `nome`, `email`, `cpf`, `idade`, `estado`, `cep`, `descricao`, `competencias` e `senha`; `EmpresaInterface` contém `id`, `nome`, `email`, `cnpj`, `pais`, `estado`, `cep`, `descricao` e `senha`; `VagaInterface` contém `id`, `empresaId`, `titulo`, `descricao` e `competencias`.

## Regras de negócio centrais

- O schema registra curtidas de candidato para vaga e de empresa para candidato.
- A consulta de match em `sql/queries.sql` relaciona a curtida candidato-vaga à empresa proprietária da vaga e à curtida dessa empresa pelo mesmo candidato. O match da consulta ocorre quando as duas curtidas correspondentes existem.
- As tabelas possuem chaves primárias compostas para impedir duplicação da mesma curtida.
- Não há operação de curtir ou de match implementada nos services/handlers do backend; a lógica de match presente no repositório está representada nas tabelas e consultas SQL.
- A consulta de match não filtra por competências em comum.

## Princípio do Anonimato — restrição obrigatória

Antes do match, os dados profissionais anônimos permitidos são:

- Na visualização do candidato por empresa: competências e descrição profissional do candidato (`PessoaBase.descricao`).
- Na visualização da vaga pelo candidato: nome, descrição e competências exigidas da vaga.

Não exibir antes do match dados identificáveis ou pessoais do candidato, da empresa ou do recrutador: nome (exceto o nome da vaga), e-mail, CPF, idade, data de nascimento, estado, CEP, telefone ou endereço. O candidato também não pode ver dados da empresa ou do recrutador antes do match. A permissão para mostrar os dados da vaga não inclui dados da empresa nem outros campos da vaga, como local.

Antes de alterar qualquer leitura ou exibição, revisar esse princípio em SQL, DAOs, controllers, handlers HTTP, telas, logs e testes. As consultas, DAOs, handlers e telas existentes não autorizam expor campos fora das permissões acima, mesmo quando retornem registros completos. Não enfraquecer a restrição para facilitar telas, testes ou logs.

## Banco de dados

Os scripts SQL versionados estão em `sql/`:

1. `sql/schema.sql` cria as tabelas.
2. `sql/inserts.sql` carrega dados de exemplo.
3. `sql/queries.sql` contém consultas de exemplo.

Execute o schema antes da carga de dados, usando PostgreSQL. O repositório não contém uma tarefa Gradle ou script próprio para aplicar esses arquivos SQL. A configuração JDBC fica em `app/src/main/resources/database.properties`; não copie valores de configuração ou credenciais para documentação.

Use apenas dados fictícios em scripts de carga e testes.

## Restrições gerais

- Nunca commitar credenciais, senhas ou segredos.
- Manter arquivos `.env` fora do repositório.
- Usar apenas dados fictícios em scripts de carga e testes.
- Ao trabalhar com dados, aplicar sempre o Princípio do Anonimato acima.
