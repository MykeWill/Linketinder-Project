package com.linketinder.model

import spock.lang.Specification

class EmpresaSpec extends Specification {

    def "deve criar uma empresa com dados válidos e exibir detalhes"() {
        given:
        def nome = "TechNova"
        def email = "contato@technova.com"
        def cnpj = "12.345.678/0001-90"
        def pais = "Brasil"
        def estado = "SP"
        def cep = "01000-000"
        def descricao = "Startup de tecnologia"
        def competencias = ["Java", "Cloud"]

        when:
        def empresa = new Empresa(nome, email, cnpj, pais, estado, cep, descricao, competencias)

        then:
        empresa.nome == nome
        empresa.email == email
        empresa.cnpj == cnpj
        empresa.pais == pais
        empresa.estado == estado
        empresa.cep == cep
        empresa.descricao == descricao
        empresa.competencias == competencias
        empresa.exibirDetalhes().contains("TechNova")
        empresa.exibirDetalhes().contains("CNPJ: 12.345.678/0001-90")
    }
}