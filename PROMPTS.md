# Metodologia SDD e prompts utilizados

## Metodologia

O código da feature Recrutador foi gerado com o auxílio de uma IA e revisado por uma pessoa. A IA foi guiada pelo método **Spec-Driven Development (SDD)**: primeiro contexto e especificação, depois plano de tarefas e, somente após a aprovação desses artefatos, implementação incremental e validação.

Os exemplos abaixo resumem prompts utilizados durante o desenvolvimento, preservando a essência das instruções. O fluxo começou pela preparação da documentação, definiu o comportamento esperado e avançou pelas camadas de banco de dados, back-end e front-end. As tarefas foram implementadas e validadas em etapas, com atenção especial ao Princípio do Anonimato.

## Preparação — Parte 1

- “Analise o projeto e crie um `AGENTS.md` na raiz com a arquitetura, stack, convenções e regras de negócio existentes. Registre o Princípio do Anonimato como uma restrição obrigatória para qualquer mudança que leia ou exiba dados.”
- “Adapte a skill `feature-com-spec` ao fluxo e às convenções deste repositório, incluindo o anonimato, e crie um `CHANGELOG.md` no padrão Keep a Changelog para registrar as tarefas da feature.”

## Especificação e plano

- “Com base no contexto do projeto, escreva `docs/specs/recrutador/spec.md` para a feature de recrutadores. Defina escopo, regras e critérios de aceite, explicitando quais campos podem ser vistos antes e depois do match e quais dados nunca podem ser expostos. Não implemente código antes da aprovação da spec.”
- “Após a aprovação da spec, crie `plano.md` com tarefas pequenas e ordenadas por dependência. Para cada tarefa, indique arquivos, validação concreta e verificações de anonimato; separe banco, back-end e front-end.”

## Banco de dados

- “Atualize o schema para criar recrutadores vinculados a empresas e associar opcionalmente o recrutador criador a vagas e curtidas. Preserve curtidas e vagas quando um recrutador for removido, conforme as regras da spec.”
- “Revise as consultas SQL de candidatos, vagas e matches: filtre candidatos que a empresa já curtiu, retorne somente campos autorizados antes do match e libere contatos apenas no match. Não inclua CPF, data de nascimento, senha ou hash.”

## Back-end — Groovy

- “Implemente o modelo e a persistência de recrutadores, com validação de e-mail e CPF, idade mínima de 18 anos e senha protegida com bcrypt. Cubra as regras com testes Spock e não retorne dados cadastrais sigilosos.”
- “Crie o controller e os endpoints HTTP para cadastro e remoção, consulta de candidatos anônimos, curtidas e matches. Restrinja explicitamente os campos serializados e adicione testes Spock que verifiquem campos permitidos e proibidos nas respostas.”

## Front-end — TypeScript

- “Adicione os tipos de entrada e resposta e o cliente HTTP para as operações de recrutador. Mantenha dados sigilosos apenas no envio de cadastro; não os renderize nem persista credenciais em `localStorage`.”
- “Implemente a gestão de recrutadores no perfil da empresa, com listagem, cadastro e remoção pela API. Em seguida, integre a área do recrutador às vagas, candidatos anônimos e matches, respeitando os campos liberados em cada etapa.”
