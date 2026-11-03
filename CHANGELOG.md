# Changelog

Todas as mudanças notáveis deste projeto são documentadas neste arquivo.

O formato segue o [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/)
e o versionamento segue o [SemVer](https://semver.org/lang/pt-BR/).

## [Não lançado]

### Adicionado
- Edição dos dados de recrutadores pela empresa proprietária, com senha opcional protegida por bcrypt e sem leitura ou alteração de CPF.
- Espaçamento superior de 16px no botão de cadastro de recrutadores no perfil da empresa.
- Edição de perfil de candidatos e empresas com atualização de senha opcional via bcrypt e edição de vagas por empresa ou recrutador responsável.
- Sessões bearer com validade de oito horas para proteger a leitura/edição de perfis e a edição de vagas, vinculadas ao usuário autenticado.
- Bcrypt implementado no cadastro e login de candidatos e empresas.
- Autenticação de candidatos, empresas e recrutadores via API, com sessão local e perfis vinculados ao usuário autenticado.
- Curtidas de vagas pelo candidato com confirmação de match, apresentação de matches atualizada e botão de ação no perfil do candidato.
- Perfil do candidato integrado à API para exibir vagas anônimas e contatos de matches pós-match permitidos pela spec.
- Área do recrutador no frontend para consultar vagas, criar vagas pela API, curtir candidatos anônimos e visualizar matches pós-match.
- Listagem HTTP de recrutadores por empresa com resposta restrita aos campos permitidos e gestão de listagem, cadastro e remoção no perfil da empresa.
- `AGENTS.md` com visão geral, stack, estrutura, domínio do Linketinder, regras de negócio centrais e o Princípio do Anonimato como restrição explícita.
- `skills/feature-com-spec/SKILL.md` adaptada ao Linketinder: specs em `docs/specs/<nome-da-feature>/`, checklist com Princípio do Anonimato e atualização deste changelog a cada tarefa concluída.
- Tabela recrutador com vínculo à empresa e unicidade de e-mail e CPF; campos recrutador_id em vaga e curtida_empresa_candidato.
- Modelo `Recrutador` e contrato `RecrutadorRepository` para cadastro, busca, listagem e remoção.
- DAO e serviço do recrutador com validações de e-mail, CPF e idade mínima, verificação de duplicidade e hash bcrypt da senha; testes Spock incluindo aceitação de e-mail válido.
- Vagas associadas opcionalmente ao recrutador criador, com consulta do escopo de vagas da empresa e do recrutador.
- Listagem anônima de candidatos disponíveis para recrutadores e curtidas registradas em nome da empresa, com bloqueio de curtidas duplicadas.
- Modelo `Match` e leitura de matches da empresa do recrutador, liberando contatos e os dados pós-match especificados sem CPF ou data de nascimento.
- Handler HTTP e controller para cadastro/remoção, listagem anônima de candidatos, curtidas e matches de recrutadores.
- Testes Spock adicionais para remoção, anonimato de matches e associação de vagas ao recrutador criador.
- Tipos TypeScript para recrutador, candidatos anônimos e matches, além de cliente HTTP para cadastro, listagem de candidatos, curtidas e matches.

### Modificado
- Contas antigas de desenvolvimento com senhas em texto puro precisam ser recriadas; não é possível convertê-las em hashes bcrypt sem as senhas originais.
- Listagens de vagas por candidato, recrutador e empresa priorizam o vínculo obrigatório com a empresa e não exigem recrutador criador.
- Vagas anônimas incluem apenas um ID técnico não exibido na tela, necessário para registrar a curtida, além dos campos públicos permitidos.
- Endpoints públicos de listagem de vagas restritos a nome, descrição e competências; adicionada listagem dos matches de um candidato com campos pós-match permitidos.
- Consultas SQL atualizadas para aplicar filtro de candidatos já curtidos e restringir campos visíveis antes/depois do match.
- Endpoint HTTP de vagas ampliado para consultar o escopo do recrutador.

### Corrigido
- Cadastro de vagas pela empresa persistido na API para aparecer nas listagens vinculadas ao banco.
- Normalização de CPF para dígitos nos cadastros de candidato e recrutador e de CNPJ no cadastro de empresa.
- CPF aceita entrada com ou sem pontuação no formulário; data de nascimento de recrutador é enviada em ISO.
- Payloads de cadastro de candidatos e empresas enviados à API no formato esperado pelos handlers.
- Vagas já curtidas são omitidas da listagem do candidato.
- Match passa a ser contabilizado por vaga, permitindo múltiplos matches com a mesma empresa.
- Corrigida a listagem de matches nos perfis de candidato e recrutador para refletir os matches recém-criados.
- Força reload da página pós-match para garantir exibição dos dados.
- Adiciona verificação defensiva para dados nulos e estrutura JSON inválida na tela do candidato.
- Refatora a tabela de matches do candidato para criar elementos DOM e preencher conteúdo sem template strings.

### Removido
