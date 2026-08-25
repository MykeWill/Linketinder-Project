package com.linketinder.repository

import com.linketinder.model.Empresa
import spock.lang.Specification

class EmpresaRepositorySpec extends Specification {

    def "deve inicializar com 5 empresas"() {
        given:
        def repository = new EmpresaRepository()

        when:
        def lista = repository.listarTodasEmpresasRepository()

        then:
        lista.size() == 5
        lista[0].nome == "Arroz-Gostoso"
        lista[1].nome == "Império do Boliche"
        lista[2].nome == "TechNova"
        lista[3].nome == "QualiTest Corp"
        lista[4].nome == "DevBridge"
    }

    def "deve adicionar uma nova empresa e atualizar a lista"() {
        given:
        def repository = new EmpresaRepository()
        def novaEmpresa = new Empresa(
                "NovaTech",
                "contato@novatech.com",
                "99.999.999/0001-99",
                "Brasil",
                "RS",
                "90000-000",
                "Empresa de inovação",
                ["Kotlin", "Docker"]
        )

        when:
        repository.adicionarEmpresaRepository(novaEmpresa)

        then:
        def lista = repository.listarTodasEmpresasRepository()
        lista.size() == 6
        lista.contains(novaEmpresa)
        lista.last().nome == "NovaTech"
    }
}