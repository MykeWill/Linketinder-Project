package com.linketinder.service

import com.linketinder.model.Empresa
import com.linketinder.repository.EmpresaRepository
import spock.lang.Specification

class EmpresaServiceSpec extends Specification {

    def "deve listar empresas delegando ao repositório"() {
        given:
        def repositoryMock = Mock(EmpresaRepository)
        def service = new EmpresaService(repositoryMock)
        def listaEsperada = [new Empresa("Teste", "teste@email.com", "000", "BR", "SP", "000", "", [])]

        when:
        def resultado = service.listarTodasEmpresasService()

        then:
        1 * repositoryMock.listarTodasEmpresasRepository() >> listaEsperada
        resultado == listaEsperada
    }

    def "deve cadastrar uma empresa chamando o repositório"() {
        given:
        def repositoryMock = Mock(EmpresaRepository)
        def service = new EmpresaService(repositoryMock)
        def empresa = new Empresa("Tech", "tech@email.com", "111", "BR", "SP", "000", "", [])

        when:
        service.cadastrarEmpresaService(empresa)

        then:
        1 * repositoryMock.adicionarEmpresaRepository(empresa)
    }

    def "não deve cadastrar empresa com CNPJ duplicado"() {
        given:
        def repositoryMock = Mock(EmpresaRepository)
        def service = new EmpresaService(repositoryMock)
        def empresaExistente = new Empresa(
                "TechNova",
                "contato@technova.com",
                "12.345.678/0001-90",
                "Brasil",
                "SP",
                "01000-000",
                "Startup de tecnologia",
                ["Java", "Cloud"]
        )
        def novaEmpresa = new Empresa(
                "OutraTech",
                "outra@tech.com",
                "12.345.678/0001-90",  // mesmo CNPJ
                "Brasil",
                "RJ",
                "20000-000",
                "Outra empresa",
                ["Python"]
        )

        repositoryMock.listarTodasEmpresasRepository() >> [empresaExistente]

        when:
        service.cadastrarEmpresaService(novaEmpresa)

        then:
        thrown(IllegalArgumentException)
    }
}