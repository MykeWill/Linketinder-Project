# Plano de implementação — Recrutador

Cada tarefa deve ser implementada, validada e commitada separadamente, na ordem abaixo. A integração do frontend será feita com a API HTTP Groovy; a persistência de recrutadores e senhas será no backend/PostgreSQL, não em `localStorage`.

## Banco de dados

### 1. ✅ Criar tabela de recrutadores e relacionamentos
- **O que será feito:** Criar a tabela `recrutador` com vínculo obrigatório à empresa, e adicionar `recrutador_id` nullable a `vaga` e `curtida_empresa_candidato`, com as regras de exclusão definidas na spec.
- **Camada:** banco.
- **Arquivos criados/alterados:** `sql/schema.sql`.
- **Como validar:** Aplicar `sql/schema.sql` em um banco PostgreSQL de teste vazio, usando credenciais configuradas fora do repositório; conferir FKs, unicidades e nulabilidade; inserir e remover recrutador de teste e confirmar `ON DELETE CASCADE` na empresa e `ON DELETE SET NULL` nas vagas e curtidas.
- **Anonimato:** DDL não cria saídas de dados; CPF, data de nascimento e senha/hash não devem ser incluídos em consultas de leitura pública.

### 2. ✅ Atualizar consultas de candidatos, vagas e matches
- **O que será feito:** Atualizar as consultas SQL para filtrar candidatos já curtidos pela empresa, expor para cada perspectiva somente os campos autorizados na spec e incluir o recrutador responsável e os contatos apenas na consulta de match.
- **Camada:** banco.
- **Arquivos criados/alterados:** `sql/queries.sql`.
- **Como validar:** Executar `sql/inserts.sql` e as consultas em banco PostgreSQL de teste; cobrir dois recrutadores da mesma empresa, empresas distintas e match; conferir os campos retornados em cada consulta.
- **Anonimato:** Antes do match, a leitura do candidato pela empresa contém apenas competências e descrição profissional; a do candidato contém apenas nome, descrição e competências da vaga, sem dados da empresa/recrutador. Pós-match, retornar somente os campos autorizados; nunca incluir CPF ou data de nascimento do recrutador, nem senha/hash.

## Back-end — Groovy e testes Spock

### 3. ✅ Criar o modelo e o contrato de persistência do recrutador
- **O que será feito:** Definir o modelo `Recrutador` e as operações de persistência necessárias ao cadastro, consulta e remoção, vinculadas a `empresa_id`.
- **Camada:** back-end.
- **Arquivos criados/alterados:** `app/src/main/groovy/com/linketinder/model/Recrutador.groovy`, `app/src/main/groovy/com/linketinder/repository/RecrutadorRepository.groovy`.
- **Como validar:** Rodar `./gradlew compileGroovy --console=plain` e verificar os tipos e campos do modelo e contrato.
- **Anonimato:** O modelo de entrada poderá conter CPF, data de nascimento e senha para cadastro; nenhum objeto de resposta deve expor CPF, data de nascimento ou senha/hash.

### 4. ✅ Implementar persistência e validações do recrutador
- **O que será feito:** Persistir recrutadores com e-mail e CPF únicos; validar formato do e-mail, dígitos verificadores do CPF e idade mínima de 18 anos; gerar e persistir senha com bcrypt. Adicionar a dependência de bcrypt ao Gradle, se necessária.
- **Camada:** back-end.
- **Arquivos criados/alterados:** `app/src/main/groovy/com/linketinder/dao/RecrutadorDao.groovy`, `app/src/main/groovy/com/linketinder/service/RecrutadorService.groovy`, `app/src/main/groovy/com/linketinder/exception/DadosInvalidosException.groovy`, `app/src/main/groovy/com/linketinder/exception/MensagensErro.groovy`, `app/src/test/groovy/com/linketinder/RecrutadorServiceSpec.groovy`, `app/build.gradle`, `gradle/libs.versions.toml`.
- **Como validar:** Executar os testes Spock específicos do serviço; verificar e-mail inválido/duplicado, CPF inválido/duplicado, menor de 18 anos, hash bcrypt verificável e ausência de senha em texto puro no valor persistido. Rodar `./gradlew test --console=plain`.
- **Anonimato:** CPF e data de nascimento são usados apenas para cadastro/validação e nunca aparecem em respostas ou logs; senha e hash não são retornados nem registrados em logs.

### 5. Registrar o criador da vaga e limitar sua consulta
- **O que será feito:** Adicionar `recrutadorId` ao modelo `Vaga` e propagar o campo na criação, leitura e consultas de vagas; permitir que o recrutador consulte as vagas da empresa e as vagas que criou, sem edição.
- **Camada:** back-end.
- **Arquivos criados/alterados:** `app/src/main/groovy/com/linketinder/model/Vaga.groovy`, `app/src/main/groovy/com/linketinder/repository/VagaRepository.groovy`, `app/src/main/groovy/com/linketinder/dao/VagaDao.groovy`, `app/src/main/groovy/com/linketinder/service/VagaService.groovy`, `app/src/test/groovy/com/linketinder/VagaServiceSpec.groovy`.
- **Como validar:** Executar testes Spock de vaga e persistência; confirmar que vaga criada pelo recrutador guarda seu id, que vaga criada pela empresa mantém `NULL`, e que remoção do recrutador preserva a vaga com `recrutador_id = NULL`.
- **Anonimato:** A consulta de vaga visível ao candidato retorna apenas nome, descrição e competências exigidas; não inclui informações da empresa nem do recrutador.

### 6. Implementar listagem anônima e curtida da empresa pelo recrutador
- **O que será feito:** Adicionar ao repositório/DAO/serviço as operações de listagem de candidatos disponíveis e de curtida; associar a curtida à empresa e ao recrutador executor, rejeitando repetição para o mesmo par empresa-candidato.
- **Camada:** back-end.
- **Arquivos criados/alterados:** `app/src/main/groovy/com/linketinder/repository/RecrutadorRepository.groovy`, `app/src/main/groovy/com/linketinder/dao/RecrutadorDao.groovy`, `app/src/main/groovy/com/linketinder/service/RecrutadorService.groovy`, `app/src/main/groovy/com/linketinder/exception/RegistroDuplicadoException.groovy`, `app/src/test/groovy/com/linketinder/RecrutadorServiceSpec.groovy`.
- **Como validar:** Executar testes Spock com mocks/repositórios para confirmar que a curtida usa empresa e recrutador corretos, que repetição pela mesma empresa é rejeitada e que uma empresa diferente pode curtir o mesmo candidato.
- **Anonimato:** A lista pré-match inclui apenas competências e descrição profissional do candidato; não retorna nome, e-mail, CPF, idade, estado ou CEP. O id necessário à operação não deve ser apresentado como campo do perfil.

### 7. Implementar leitura do match e contatos pós-match
- **O que será feito:** Derivar matches candidato ↔ empresa das curtidas existentes e disponibilizar os dados de match, incluindo o recrutador que executou a curtida e os contatos liberados.
- **Camada:** back-end.
- **Arquivos criados/alterados:** `app/src/main/groovy/com/linketinder/repository/RecrutadorRepository.groovy`, `app/src/main/groovy/com/linketinder/dao/RecrutadorDao.groovy`, `app/src/main/groovy/com/linketinder/service/RecrutadorService.groovy`.
- **Como validar:** Executar testes Spock para ausência/presença de match; confirmar que o match permanece da empresa após remover o recrutador e que só é reconhecido com as duas curtidas correspondentes.
- **Anonimato:** Pós-match, o recrutador recebe apenas nome, idade, e-mail, competências, descrição profissional e estado do candidato. O candidato recebe os campos pós-match permitidos da empresa e do recrutador executor. Nenhuma resposta inclui CPF, data de nascimento, senha ou hash.

### 8. Expor operações HTTP com respostas restritas
- **O que será feito:** Criar controller e handler HTTP para cadastro/remoção de recrutadores, consulta de vagas e candidatos, curtida e leitura de matches; registrar o handler no servidor e converter resultados para respostas com campos explicitamente permitidos.
- **Camada:** back-end.
- **Arquivos criados/alterados:** `app/src/main/groovy/com/linketinder/controller/RecrutadorController.groovy`, `app/src/main/groovy/com/linketinder/server/handler/RecrutadorHandler.groovy`, `app/src/main/groovy/com/linketinder/server/ServidorHttp.groovy`, `app/src/main/groovy/com/linketinder/Main.groovy`.
- **Como validar:** Rodar `./gradlew test --console=plain`; testar os endpoints HTTP em execução local para cadastro, listagem, curtida, remoção, match e status de erro de validação/duplicidade.
- **Anonimato:** Validar os formatos JSON pré e pós-match contra a tabela da spec. CPF, data de nascimento, senha e hash não podem aparecer em nenhuma resposta; logs não devem registrar esses valores.

### 9. Cobrir regras de negócio e anonimato com Spock
- **O que será feito:** Criar testes Spock para vínculo recrutador-empresa, cadastro/validações, bcrypt, escopo de vagas, curtida, bloqueio de curtida duplicada, match, remoção e campos visíveis/proibidos nas respostas.
- **Camada:** back-end.
- **Arquivos criados/alterados:** `app/src/test/groovy/com/linketinder/RecrutadorServiceSpec.groovy`, `app/src/test/groovy/com/linketinder/VagaServiceSpec.groovy`, `app/src/test/groovy/com/linketinder/RecrutadorHandlerSpec.groovy`.
- **Como validar:** Executar `./gradlew test --tests 'com.linketinder.service.RecrutadorServiceSpec' --tests 'com.linketinder.service.VagaServiceSpec' --tests 'com.linketinder.server.handler.RecrutadorHandlerSpec' --console=plain` e depois `./gradlew test --console=plain`.
- **Anonimato:** Os testes devem afirmar positivamente os campos permitidos e negativamente a ausência de campos proibidos em cada resposta, inclusive CPF/data de nascimento do recrutador em qualquer endpoint.

## Front-end — TypeScript

### 10. Criar tipos e cliente HTTP para recrutadores
- **O que será feito:** Definir tipos de entrada e saída sem misturar dados cadastrais sigilosos nas respostas; criar chamadas HTTP para os endpoints do recrutador, vagas, candidatos, curtidas e matches.
- **Camada:** front-end.
- **Arquivos criados/alterados:** `frontend/src/types/models.ts`, `frontend/src/repository/recrutadorRepository.ts`, `frontend/src/service/recrutadorService.ts`, `frontend/src/service/api.ts`.
- **Como validar:** Rodar `cd frontend && npm run build`; conferir chamadas em ambiente local contra a API e tratamento de respostas de erro sem persistir credenciais em `localStorage`.
- **Anonimato:** Tipos de resposta e chamadas de listagem pré-match refletem somente os campos permitidos; CPF/data de nascimento/senha existem apenas no envio de cadastro e nunca são renderizados ou registrados.

### 11. Adicionar gestão de recrutadores no perfil da empresa
- **O que será feito:** Permitir à empresa consultar, cadastrar e remover recrutadores pelo frontend, enviando os dados cadastrais necessários à API.
- **Camada:** front-end.
- **Arquivos criados/alterados:** `frontend/src/pages/perfilEmpresa.ts`, `frontend/src/pages/cadastroRecrutador.ts`, `frontend/src/router.ts`, `frontend/src/main.ts`.
- **Como validar:** Rodar `cd frontend && npm run build`; iniciar backend e frontend localmente e verificar cadastro, validações retornadas pela API, listagem e remoção.
- **Anonimato:** O perfil interno da empresa pode mostrar a descrição do recrutador conforme a spec; nunca renderizar CPF, data de nascimento, senha ou hash, e nunca expor a descrição do recrutador ao candidato.

### 12. Adicionar área do recrutador para vagas, candidatos e matches
- **O que será feito:** Criar a tela do recrutador para consultar vagas da empresa, criar vaga, visualizar candidatos disponíveis, curtir candidato e consultar matches pós-match.
- **Camada:** front-end.
- **Arquivos criados/alterados:** `frontend/src/pages/perfilRecrutador.ts`, `frontend/src/pages/perfilEmpresa.ts`, `frontend/src/router.ts`, `frontend/src/main.ts`.
- **Como validar:** Rodar `cd frontend && npm run build`; com backend e frontend locais, verificar consulta/criação de vaga, desaparecimento do candidato após curtida por outro recrutador da mesma empresa e visualização dos dados pós-match.
- **Anonimato:** Antes do match, mostrar do candidato apenas competências e descrição profissional; ocultar dados identificáveis. Pós-match, mostrar somente os campos autorizados, sem CPF/data de nascimento do candidato ou dados sigilosos do recrutador.

### 13. Exibir vagas anônimas e contatos de match no perfil do candidato
- **O que será feito:** Integrar o perfil do candidato à API para listar vagas com os campos permitidos e, após match, exibir informações da empresa e do recrutador responsável e os e-mails de contato liberados.
- **Camada:** front-end.
- **Arquivos criados/alterados:** `frontend/src/pages/perfilCandidato.ts`, `frontend/src/service/vagaService.ts`, `frontend/src/repository/vagaRepository.ts`, `frontend/src/service/recrutadorService.ts`.
- **Como validar:** Rodar `cd frontend && npm run build`; verificar no navegador antes do match que a vaga não revela empresa/recrutador e, depois do match, conferir os contatos e campos liberados.
- **Anonimato:** Antes do match, exibir apenas nome, descrição e competências exigidas da vaga. Depois, exibir apenas nome/descrição/e-mail da empresa e nome/id/área/e-mail do recrutador executor; nunca CPF, data de nascimento, idade ou descrição do recrutador.
