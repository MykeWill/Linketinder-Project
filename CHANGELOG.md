# Changelog

Todas as mudanças notáveis deste projeto são documentadas neste arquivo.

O formato segue o [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/)
e o versionamento segue o [SemVer](https://semver.org/lang/pt-BR/).

## [Não lançado]

### Adicionado
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
- Consultas SQL atualizadas para aplicar filtro de candidatos já curtidos e restringir campos visíveis antes/depois do match.

### Corrigido

### Removido
