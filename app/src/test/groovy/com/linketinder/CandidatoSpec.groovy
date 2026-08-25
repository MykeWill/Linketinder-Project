package com.linketinder.model

import spock.lang.Specification

class CandidatoSpec extends Specification {

    def "deve criar um candidato com dados válidos e exibir detalhes"() {
        given:
        def nome = "João Silva"
        def email = "joao@email.com"
        def cpf = "123.456.789-00"
        def idade = 30
        def estado = "SP"
        def cep = "01000-000"
        def descricao = "Desenvolvedor full stack"
        def competencias = ["Java", "Spring", "React"]

        when:
        def candidato = new Candidato(nome, email, cpf, idade, estado, cep, descricao, competencias)

        then:
        candidato.nome == nome
        candidato.email == email
        candidato.cpf == cpf
        candidato.idade == idade
        candidato.estado == estado
        candidato.cep == cep
        candidato.descricao == descricao
        candidato.competencias == competencias
        candidato.exibirDetalhes().contains("João Silva")
        candidato.exibirDetalhes().contains("CPF: 123.456.789-00")
    }
}