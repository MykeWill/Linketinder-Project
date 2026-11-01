# Recrutador

## Contexto e objetivo

Adicionar recrutadores vinculados a empresas para que uma empresa possa cadastrar e remover seus recrutadores, e para que eles possam consultar vagas da empresa e curtir candidatos em nome dela. A curtida e o match continuam pertencendo à empresa; o recrutador que executou a curtida é registrado como histórico.

## Escopo

- Criar a entidade e a tabela `recrutador`, vinculada a uma empresa, com os campos `id`, `empresa_id`, `nome`, `area_recrutamento`, `descricao`, `email`, `cpf`, `data_nascimento` e `senha_hash`.
- Permitir que a empresa cadastre e remova seus recrutadores.
- Permitir ao recrutador consultar as vagas da empresa e as vagas que ele próprio criou, e criar vagas. Cada vaga tem no máximo um recrutador criador.
- Adicionar `recrutador_id` nullable a `vaga`, identificando o recrutador criador quando preenchido. `NULL` identifica vaga criada pela empresa.
- Permitir ao recrutador curtir candidatos. A curtida é registrada como curtida da empresa, com `recrutador_id` nullable para registrar quem a executou.
- Impedir mais de uma curtida da mesma empresa para o mesmo candidato, independentemente do recrutador que tentou curtir.
- Derivar o match candidato ↔ empresa das curtidas existentes; não criar tabela de match.
- Liberar os dados de contato especificados após o match: e-mail da empresa, e-mail do recrutador que executou a curtida e e-mail do candidato.
- Aplicar as regras de anonimato e validação descritas nesta especificação.

## Regras de negócio

### Vínculo, vagas e recrutadores

- Um recrutador pertence a exatamente uma empresa; uma empresa pode ter vários recrutadores.
- A empresa cadastra e remove os recrutadores que lhe pertencem.
- O recrutador consulta as vagas da empresa a que pertence e as vagas que ele próprio criou. Pode criar vagas, mas não editá-las nesta feature.
- `vaga.recrutador_id` referencia `recrutador.id` e aceita `NULL`. Sua exclusão usa `ON DELETE SET NULL`: remover o recrutador mantém as vagas, que passam a ter `recrutador_id = NULL`.
- Uma vaga tem apenas um criador: a empresa (`recrutador_id = NULL`) ou um recrutador (`recrutador_id` preenchido).

### Curtidas e match

- A curtida feita por um recrutador conta como curtida da empresa à qual ele pertence.
- `curtida_empresa_candidato.recrutador_id` é nullable, referencia o recrutador que executou a curtida e usa `ON DELETE SET NULL`. A curtida permanece como histórico da empresa mesmo se o recrutador for removido.
- Se a empresa já curtiu um candidato por meio de qualquer recrutador, esse candidato não aparece na lista de candidatos disponíveis para os demais recrutadores da mesma empresa. A consulta que lista candidatos deve aplicar esse filtro por empresa.
- Não pode existir mais de uma curtida da mesma empresa para o mesmo candidato. A restrição vale entre todos os recrutadores da empresa, não apenas por recrutador.
- O match ocorre entre candidato e empresa quando existem a curtida do candidato em uma vaga da empresa e a curtida da empresa nesse candidato. A curtida executada pelo recrutador registra quem curtiu, mas o match pertence à empresa.
- Remover o recrutador não desfaz o match da empresa. Se o recrutador associado à curtida for removido, `recrutador_id` fica `NULL`; a associação da curtida e o match continuam.

### Dados e validações do recrutador

- Os campos do recrutador são nome, área de recrutamento (string), descrição, e-mail, CPF, data de nascimento e senha armazenada no banco como `senha_hash`.
- O e-mail deve ter formato válido e ser único; o CPF deve ser válido pelos dígitos verificadores e único.
- A data de nascimento deve corresponder a uma idade mínima de 18 anos.
- A senha deve ser armazenada com hash bcrypt, nunca em texto puro.
- CPF e data de nascimento existem para cadastro e validação, mas nunca podem ser exibidos em telas, respostas ou logs. A senha e o hash também não podem ser retornados em respostas ou exibidos em telas ou logs.
- A descrição do recrutador só pode ser vista no perfil interno da empresa; nunca pelo candidato.
- Credenciais de banco ficam em `.env` ou `database.properties` fora do repositório, e nunca devem ser incluídas na feature ou em seus dados de teste.

### Anonimato: quem vê o quê, antes e depois do match

O perfil na tabela representa quem consulta os dados. Os campos não listados como visíveis não são liberados por esta feature. CPF e data de nascimento do recrutador nunca são exibidos em tela, resposta ou log.

| Perfil | Momento | Campos visíveis | Campos proibidos |
|---|---|---|---|
| Recrutador | Antes do match | Do candidato: competências e descrição profissional (`PessoaBase.descricao`). | Do candidato: nome, e-mail, CPF, idade, estado e CEP. |
| Recrutador | Depois do match | Do candidato: nome, idade, e-mail, competências, descrição profissional e estado. | Do candidato: CPF e data de nascimento; quaisquer campos além dos explicitamente listados não são liberados. |
| Empresa | Antes do match | A descrição do recrutador pode ser vista somente no perfil interno da empresa. | CPF e data de nascimento do recrutador em qualquer tela, resposta ou log. |
| Empresa | Depois do match | A descrição do recrutador continua restrita ao perfil interno da empresa. O match e a curtida permanecem associados à empresa. | CPF e data de nascimento do recrutador em qualquer tela, resposta ou log. |
| Candidato | Antes do match | Da vaga: nome, descrição e competências exigidas. | Dados da empresa: nome, CNPJ, e-mail, país, estado, CEP e descrição. Todos os dados do recrutador. |
| Candidato | Depois do match | Da empresa: nome, descrição e e-mail de contato. Do recrutador que executou a curtida: nome, id, área de recrutamento e e-mail. | Da empresa: CNPJ, país, estado e CEP. Do recrutador: CPF, data de nascimento, idade e descrição. |

Após o match, o e-mail do candidato é liberado para a empresa/recrutador, e o e-mail da empresa e o do recrutador responsável pela curtida são liberados para o candidato. Não há chat nem e-mail mascarado nesta feature.

## Critérios de aceite

### Cenário: empresa cadastra recrutador
- **Dado** uma empresa existente
- **Quando** ela cadastrar um recrutador com nome, área de recrutamento, descrição, e-mail válido, CPF válido, data de nascimento que atenda à idade mínima e senha
- **Então** o recrutador será criado vinculado à empresa, com e-mail e CPF únicos e senha armazenada como hash bcrypt, nunca em texto puro

### Cenário: cadastro rejeita dados inválidos ou duplicados
- **Dado** que a empresa informa e-mail malformado ou já cadastrado, CPF com dígitos verificadores inválidos ou já cadastrado, data de nascimento que não atenda à idade mínima, ou senha sem aplicação de bcrypt
- **Quando** tentar cadastrar o recrutador
- **Então** o cadastro será rejeitado e nenhum recrutador inválido será persistido

### Cenário: recrutador cria vaga
- **Dado** um recrutador vinculado a uma empresa
- **Quando** criar uma vaga dessa empresa
- **Então** a vaga será vinculada à empresa e terá `recrutador_id` igual ao id desse recrutador

### Cenário: recrutador consulta o escopo de vagas
- **Dado** um recrutador vinculado a uma empresa com vagas da empresa e vagas que ele criou
- **Quando** consultar vagas
- **Então** verá as vagas da empresa e as vagas que ele próprio criou, sem poder editar vagas nesta feature

### Cenário: recrutador curte candidato
- **Dado** um recrutador cuja empresa ainda não curtiu determinado candidato
- **Quando** o recrutador curtir esse candidato
- **Então** será registrada uma curtida da empresa com o recrutador responsável associado

### Cenário: outro recrutador não recebe candidato já curtido pela empresa
- **Dado** que um recrutador de uma empresa já curtiu determinado candidato
- **Quando** outro recrutador da mesma empresa consultar candidatos disponíveis
- **Então** o candidato não aparecerá nessa lista, e uma nova curtida dessa empresa para esse candidato não poderá ser registrada

### Cenário: empresas diferentes podem curtir o mesmo candidato
- **Dado** que uma empresa já curtiu um candidato
- **Quando** um recrutador de outra empresa consultar e curtir esse candidato
- **Então** a curtida será permitida, pois a unicidade é por empresa e candidato

### Cenário: match pertence à empresa e registra o recrutador
- **Dado** que um candidato curtiu uma vaga e um recrutador da empresa proprietária da vaga curtiu esse candidato
- **Quando** as curtidas forem consultadas para identificar matches
- **Então** será identificado um match entre o candidato e a empresa, e a curtida registrará o recrutador responsável

### Cenário: recrutador vê somente os dados permitidos antes do match
- **Dado** que ainda não existe match entre o candidato e a empresa do recrutador
- **Quando** o recrutador consultar o perfil do candidato
- **Então** verá somente competências e descrição profissional, sem nome, e-mail, CPF, idade, estado ou CEP

### Cenário: recrutador vê os dados permitidos depois do match
- **Dado** que existe match entre o candidato e a empresa do recrutador
- **Quando** o recrutador consultar o perfil do candidato
- **Então** verá nome, idade, e-mail, competências, descrição profissional e estado, sem CPF ou data de nascimento

### Cenário: candidato vê vaga sem identificar empresa ou recrutador antes do match
- **Dado** que ainda não existe match entre o candidato e a empresa proprietária da vaga
- **Quando** o candidato consultar a vaga
- **Então** verá somente nome, descrição e competências exigidas da vaga, sem dados da empresa ou do recrutador

### Cenário: candidato vê dados de empresa e recrutador depois do match
- **Dado** que existe match entre o candidato e a empresa
- **Quando** o candidato consultar as informações pós-match
- **Então** verá nome, descrição e e-mail de contato da empresa, e nome, id, área de recrutamento e e-mail do recrutador que executou a curtida; não verá CNPJ, país, estado ou CEP da empresa, nem CPF, data de nascimento, idade ou descrição do recrutador

### Cenário: contatos são liberados após o match
- **Dado** que existe match entre candidato e empresa
- **Quando** cada lado consultar os contatos pós-match
- **Então** o candidato poderá ver o e-mail da empresa e do recrutador responsável pela curtida, e a empresa/recrutador poderá ver o e-mail do candidato

### Cenário: remoção de recrutador preserva vagas e match
- **Dado** que um recrutador criou uma vaga e executou uma curtida que participa de um match
- **Quando** a empresa remover esse recrutador
- **Então** a vaga permanecerá com `recrutador_id = NULL`, a curtida e o match continuarão pertencendo à empresa, e a referência histórica ao recrutador na curtida ficará `NULL`

### Cenário: dados sigilosos do recrutador nunca são expostos
- **Dado** que existe um recrutador cadastrado
- **Quando** qualquer tela, resposta ou log relacionado ao recrutador, à curtida ou ao match for produzido
- **Então** CPF, data de nascimento, senha e hash da senha não serão exibidos

### Cenário: senha é persistida com bcrypt
- **Dado** que a empresa fornece uma senha válida ao cadastrar o recrutador
- **Quando** o recrutador for persistido
- **Então** o banco conterá somente o hash bcrypt da senha, sem armazenar ou retornar o texto puro

## Impacto no que já existe

- Adiciona a tabela `recrutador`, com FK para `empresa` e exclusão em cascata quando a empresa for removida.
- Altera `vaga` com `recrutador_id` nullable e FK para `recrutador` com `ON DELETE SET NULL`.
- Altera `curtida_empresa_candidato` com `recrutador_id` nullable e FK para `recrutador` com `ON DELETE SET NULL`, preservando a curtida como registro da empresa.
- A unicidade da curtida empresa-candidato continua garantindo no máximo uma curtida por par, independentemente do recrutador. A consulta de candidatos disponíveis precisa aplicar o filtro por empresa.
- O match permanece derivado das curtidas existentes e não ganha tabela própria.
- O comportamento existente de candidato, empresa, vaga e curtida não muda fora da nova camada de recrutadores e das garantias de unicidade e filtragem especificadas.
- As leituras e respostas novas devem cumprir a tabela de anonimato; os campos CPF e data de nascimento do recrutador nunca podem ser expostos.

## Fora do escopo

- Formação e experiência do candidato.
- Chat ou e-mail mascarado após o match.
- Área de recrutamento como entidade separada; `area_recrutamento` é uma string.
- Edição de vaga por recrutador; nesta feature, o recrutador pode apenas criar vagas.
- Mais de um criador por vaga.
