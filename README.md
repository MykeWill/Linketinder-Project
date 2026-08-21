# Linketinder

> Projeto desenvolvido por **Myke William** como parte do desafio da ZG- Acelera ZG).

## Sobre o projeto

O Linketinder é um MVP de sistema de contratação de funcionários, inspirado no conceito do LinkedIn (perfis com competências) combinado com a dinâmica de match do Tinder, unindo candidatos e empresas por meio de competências em comum.

Este projeto foi desenvolvido a pedido do Dr. Antônio Paçoca, empresário e investidor, dono das empresas Arroz-Gostoso e Império do Boliche, com o objetivo de resolver a dificuldade de recrutadores em identificar candidatos com potencial, sem depender de perfis mais "populares" ou com maior destaque em outras plataformas.

O projeto foi implementado em **Groovy**, utilizando conceitos de Programação Orientada a Objetos (POO) e estruturas de dados, seguindo os requisitos definidos para a fase de MVP.

## Funcionalidades

- Cadastro (pré-carregado) de no mínimo 5 candidatos, contendo: nome, e-mail, CPF, idade, estado, CEP, descrição pessoal e lista de competências.
- Cadastro (pré-carregado) de no mínimo 5 empresas, contendo: nome, e-mail corporativo, CNPJ, país, estado, CEP, descrição e lista de competências esperadas dos candidatos.
- Menu interativo via terminal, com as opções:
    - Listar todos os candidatos cadastrados
    - Listar todas as empresas cadastradas
    - Sair do programa

## Arquitetura

O projeto segue uma separação em camadas:

```
com.linketinder
├── model         -> Pessoa (interface), PessoaBase (classe abstrata), Candidato, Empresa
├── repository     -> CandidatoRepository, EmpresaRepository (dados em memória)
├── service        -> CandidatoService, EmpresaService (regras de negócio)
├── menu           -> MenuPrincipal (interação com o usuário via terminal)
└── Main.groovy    -> ponto de entrada da aplicação
```

A classe `Pessoa` define os métodos obrigatórios comuns a candidatos (pessoa física) e empresas (pessoa jurídica). A classe abstrata `PessoaBase` implementa `Pessoa` e concentra os atributos gerais, sendo estendida por `Candidato` e `Empresa`, que adicionam seus atributos específicos.

## Tecnologias utilizadas

- [Groovy](https://groovy-lang.org/) 5.1
- [Gradle](https://gradle.org/) (build tool, com Gradle Wrapper incluso)
- JDK 21

## Como executar o projeto

### Pré-requisitos

- JDK 17 ou superior instalado
- Não é necessário instalar o Gradle nem o Groovy manualmente — o projeto já inclui o Gradle Wrapper (`gradlew`)

### Passos

1. Clone o repositório:
   ```bash
   git clone https://github.com/MykeWill/Linketinder-Project.git
   cd Linketinder-Project
   ```

2. Execute a aplicação:
   ```bash
   ./gradlew run --console=plain
   ```
   > O parâmetro `--console=plain` garante que o menu interativo funcione corretamente lendo a entrada do teclado.

3. No menu exibido, digite o número da opção desejada e pressione Enter:
   ```
   ===== Linketinder =====
   1 - Listar candidatos
   2 - Listar empresas
   0 - Sair
   ========================
   ```

## Próximos passos (requisitos opcionais)

- Implementar cadastro de novos candidatos e empresas via terminal.
- Evoluir a lógica de "match" entre candidatos e empresas com base nas competências em comum.

## Autor

Desenvolvido por **Myke William Silva** durante o Acelera ZG.