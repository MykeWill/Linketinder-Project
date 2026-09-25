package com.linketinder.model

class Empresa extends PessoaBase{
    Integer id
    String cnpj
    String pais
    String senha

    Empresa(String nome, String email, String cnpj, String pais, String estado, String cep, String descricao, String senha) {
        super(nome, email, estado, cep, descricao)
        this.cnpj = cnpj
        this.pais = pais
        this.senha = senha
    }

    @Override
    String exibirDetalhes() {
        """
        Empresa: ${nome}
        E-mail corporativo: ${email}
        CNPJ: ${cnpj}
        País: ${pais}
        Estado: ${estado}
        CEP: ${cep}
        Descrição: ${descricao}
        """.stripIndent()
    }
}
