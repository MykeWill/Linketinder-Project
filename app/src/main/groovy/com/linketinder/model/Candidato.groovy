package com.linketinder.model

class Candidato extends PessoaBase {
    Integer id
    String cpf
    int idade
    String senha
    List<String> competencias

    Candidato(String nome, String email, String cpf, int idade, String estado,
              String cep, String descricao, String senha, List<String> competencias) {
        super(nome, email, estado, cep, descricao)
        this.cpf = cpf
        this.idade = idade
        this.senha = senha
        this.competencias = competencias ?: []
    }

    List<String> getCompetencias() { competencias }

    @Override
    String exibirDetalhes() {
        """
        Candidato: ${nome}
        E-mail: ${email}
        CPF: ${cpf}
        Idade: ${idade}
        Estado: ${estado}
        CEP: ${cep}
        Descrição: ${descricao}
        Competências: ${competencias.join(', ')}
        """.stripIndent()
    }
}