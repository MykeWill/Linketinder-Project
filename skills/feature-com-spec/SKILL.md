---
name: feature-com-spec
description: "Use ao implementar features ou mudanças seguindo SDD (Spec-Driven Development): contexto, spec aprovada, plano em tarefas pequenas, implementação incremental, testes e commits."
---

# Feature com SDD

## Objetivo

Implementar uma feature seguindo o fluxo **contexto → especificação → plano → implementação → validação**, mantendo o trabalho alinhado ao escopo aprovado.

## Entradas

- Descrição da feature, problema ou mudança solicitada.
- Projeto e repositório onde ela será implementada.
- Preferências, restrições, padrões e critérios de aceite já conhecidos.

Se faltar informação que altere o comportamento, o escopo ou a abordagem, faça perguntas antes de escrever a especificação.

## Passo a passo

### 1. Contexto do projeto

1. Leia o `AGENTS.md` na raiz do projeto, se existir. Trate o Princípio do Anonimato documentado no projeto como restrição dura: antes do match, empresa e candidato só podem enxergar competências, formações e experiências, nunca dados identificáveis.
2. Inspecione os arquivos relevantes para entender a arquitetura, convenções, comportamento atual, comandos de teste e restrições.
3. Identifique dúvidas, riscos e comportamentos existentes que possam ser afetados.
4. Não altere código nesta etapa.

### 2. Especificação

1. Crie `docs/specs/<nome-da-feature>/spec.md` com o objetivo, escopo, regras e critérios de aceite da feature.
2. Descreva comportamentos observáveis, entradas, saídas, erros e casos de borda relevantes.
3. Inclua impacto sobre o comportamento existente e registre explicitamente o que fica fora do escopo.
4. Apresente a especificação ao usuário e aguarde aprovação. Incorpore as decisões e respostas antes de avançar.
5. **Não escreva nem altere código de implementação antes da spec ser aprovada.**

### 3. Plano de implementação

1. Após a aprovação da spec, crie `docs/specs/<nome-da-feature>/plano.md`.
2. Divida o trabalho em tarefas pequenas, ordenadas por dependências. Quando a feature envolver múltiplas camadas, ordene as tarefas: banco → back-end → front-end; omita camadas não envolvidas.
3. Para cada tarefa, indique:
    - o que será feito;
    - os arquivos que serão criados ou alterados;
    - como verificar que está pronta, com o teste ou comando específico;
    - se a tarefa ler ou exibir dados, como verificar que nenhum dado identificável aparece antes do match.
4. Isole correções de bugs e mudanças de comportamento em tarefas próprias, separadas de trabalho não relacionado.
5. Revise o plano com o usuário e ajuste-o antes da implementação, quando solicitado.

### 4. Implementação incremental

1. Implemente **uma tarefa do plano por vez**. Não comece a próxima sem concluir e validar a atual.
2. Antes de editar, confira o estado do repositório e leia os arquivos que serão alterados.
3. Faça alterações pequenas e limitadas aos arquivos e critérios daquela tarefa.
4. Preserve comportamento existente fora do escopo. Se surgir uma necessidade não prevista, pare e alinhe a spec ou o plano com o usuário.
5. Ao concluir a tarefa, execute sua validação e informe o resultado. Atualize o `CHANGELOG.md` na raiz para cada tarefa concluída, seguindo o padrão Keep a Changelog; crie o arquivo se necessário.
6. Crie **um commit por tarefa**, somente depois da validação passar. Use uma mensagem que identifique a tarefa e siga as convenções do repositório.
7. Marque a tarefa como concluída no plano, sem modificar conteúdo não relacionado.

### 5. Validação e encerramento

1. Rode primeiro a validação direcionada à tarefa atual.
2. Verifique os critérios de aceite correspondentes, não apenas se o build compila.
3. **Checklist obrigatório:** para toda tarefa que leia ou exiba dados, confirme: “nenhum dado identificável aparece antes do match”. O Princípio do Anonimato é uma restrição dura e não pode ser enfraquecido.
4. Antes de concluir o conjunto de tarefas, execute a suíte ou validação completa prevista no plano.
5. Analise alterações e estado do repositório para confirmar que não há mudanças fora do escopo.
6. Relate o que foi implementado, os testes executados, seus resultados e os commits criados. Informe claramente qualquer validação não executada ou bloqueio.

## Regras

- Não escrever código antes da spec aprovada.
- Trabalhar em uma tarefa por vez.
- Criar um commit por tarefa validada.
- Não alterar o que estiver fora do escopo aprovado.
- Não enfraquecer critérios de aceite para fazer testes passarem; corrija a implementação ou esclareça o requisito com o usuário.
- Preservar mudanças preexistentes no repositório; não descartar alterações de outras pessoas.
- Usar os comandos, ferramentas e padrões já adotados pelo projeto.
- Tratar o Princípio do Anonimato do `AGENTS.md` como restrição dura em qualquer alteração que leia ou exiba dados.

## Modelos

### Estrutura esperada de `docs/specs/<nome-da-feature>/spec.md`

```markdown
# <Nome da feature>

## Contexto e objetivo
<Problema, motivação e resultado esperado>

## Escopo
<O que será implementado>

## Regras de negócio
- <Regra observável>

## Critérios de aceite
### Cenário: <resultado esperado>
- Dado <contexto inicial>
- Quando <ação>
- Então <resultado observável>

## Impacto no que já existe
<Comportamentos, integrações e compatibilidade afetados>

## Fora do escopo
- <Item explicitamente excluído>
```

Adapte os títulos se necessário, mas mantenha explícitos o objetivo, as regras, os critérios verificáveis, o impacto e os limites do escopo.

### Estrutura esperada de `docs/specs/<nome-da-feature>/plano.md`

```markdown
# Plano de implementação — <Nome da feature>

## 1. <Tarefa pequena e objetiva>
- **O que será feito:** <resultado desta tarefa>
- **Arquivos criados ou alterados:** `<caminhos>`
- **Como validar:** `<comando de teste ou verificação concreta>`
- **Anonimato, se a tarefa ler ou exibir dados:** confirmar que nenhum dado identificável aparece antes do match.

## 2. <Próxima tarefa, se houver dependência>
- **O que será feito:** <resultado desta tarefa>
- **Arquivos criados ou alterados:** `<caminhos>`
- **Como validar:** `<comando de teste ou verificação concreta>`
```

Cada tarefa deve ser pequena o bastante para implementar, validar e registrar em um único commit. Inclua dependências quando necessário e marque tarefas concluídas sem reescrever partes não relacionadas do plano.
