package com.linketinder.service

import com.linketinder.dao.EmpresaDao
import com.linketinder.exception.ErroBancoException
import com.linketinder.exception.RegistroDuplicadoException
import com.linketinder.model.Empresa
import spock.lang.Specification

class EmpresaServiceSpec extends Specification {

    def "deve listar empresas delegando ao DAO"() {
        given:
        def empresaDaoMock = Mock(EmpresaDao)
        def service = new EmpresaService(empresaDaoMock)
        def listaEsperada = [
                new Empresa("Teste", "teste@email.com", "00.000.000/0000-00", "Brasil", "SP", "01000-000", "desc", "senha123")
        ]

        when:
        def resultado = service.listarTodasEmpresasService()

        then:
        1 * empresaDaoMock.listarTodasEmpresas() >> listaEsperada
        resultado == listaEsperada
    }

    def "deve cadastrar uma empresa chamando o DAO"() {
        given:
        def empresaDaoMock = Mock(EmpresaDao)
        def service = new EmpresaService(empresaDaoMock)
        def empresa = new Empresa("Tech", "tech@email.com", "11.111.111/0001-11", "Brasil", "SP", "01000-000", "desc", "senha123")

        when:
        service.cadastrarEmpresa(empresa)

        then:
        1 * empresaDaoMock.inserirEmpresa(empresa) >> 1
    }

    def "deve lançar RegistroDuplicadoException quando o CNPJ já existe"() {
        given:
        def empresaDaoMock = Mock(EmpresaDao)
        def service = new EmpresaService(empresaDaoMock)
        def empresa = new Empresa("OutraTech", "outra@tech.com", "12.345.678/0001-90", "Brasil", "RJ", "20000-000", "desc", "senha123")

        def sqlException = new java.sql.SQLException('ERROR: duplicate key value violates unique constraint "empresa_cnpj_key"')

        when:
        service.cadastrarEmpresa(empresa)

        then:
        1 * empresaDaoMock.inserirEmpresa(empresa) >> { throw sqlException }
        thrown(RegistroDuplicadoException)
    }

    def "deve lançar RegistroDuplicadoException quando o e-mail já existe"() {
        given:
        def empresaDaoMock = Mock(EmpresaDao)
        def service = new EmpresaService(empresaDaoMock)
        def empresa = new Empresa("Tech", "tech@email.com", "11.111.111/0001-11", "Brasil", "SP", "01000-000", "desc", "senha123")

        def sqlException = new java.sql.SQLException('ERROR: duplicate key value violates unique constraint "empresa_email_key"')

        when:
        service.cadastrarEmpresa(empresa)

        then:
        1 * empresaDaoMock.inserirEmpresa(empresa) >> { throw sqlException }
        thrown(RegistroDuplicadoException)
    }

    def "deve lançar ErroBancoException em erros genéricos do banco"() {
        given:
        def empresaDaoMock = Mock(EmpresaDao)
        def service = new EmpresaService(empresaDaoMock)
        def empresa = new Empresa("Tech", "tech@email.com", "11.111.111/0001-11", "Brasil", "SP", "01000-000", "desc", "senha123")

        def sqlException = new java.sql.SQLException('connection refused')

        when:
        service.cadastrarEmpresa(empresa)

        then:
        1 * empresaDaoMock.inserirEmpresa(empresa) >> { throw sqlException }
        thrown(ErroBancoException)
    }
}