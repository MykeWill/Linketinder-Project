# Changelog

Todas as mudanças notáveis deste projeto são documentadas neste arquivo.

O formato segue o [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/)
e o versionamento segue o [SemVer](https://semver.org/lang/pt-BR/).

## [Não lançado]

### Adicionado
- `AGENTS.md` com visão geral, stack, estrutura, domínio do Linketinder, regras de negócio centrais e o Princípio do Anonimato como restrição explícita.
- `skills/feature-com-spec/SKILL.md` adaptada ao Linketinder: specs em `docs/specs/<nome-da-feature>/`, checklist com Princípio do Anonimato e atualização deste changelog a cada tarefa concluída.
- Tabela recrutador com vínculo à empresa e unicidade de e-mail e CPF; campos recrutador_id em vaga e curtida_empresa_candidato.

### Modificado
- Consultas SQL atualizadas para aplicar filtro de candidatos já curtidos e restringir campos visíveis antes/depois do match.

### Corrigido

### Removido
