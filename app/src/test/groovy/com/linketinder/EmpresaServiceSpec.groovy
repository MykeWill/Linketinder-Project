package com.linketinder.service

import com.linketinder.exception.ErroBancoException
import com.linketinder.exception.RegistroDuplicadoException
import com.linketinder.model.Empresa
import com.linketinder.repository.EmpresaRepository
import org.mindrot.jbcrypt.BCrypt
import spock.lang.Specification

class EmpresaServiceSpec extends Specification {

    def "deve listar empresas delegando ao repositório"() {
        given:
        def empresaDaoMock = Mock(EmpresaRepository)
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

    def "deve cadastrar empresa com hash bcrypt"() {
        given:
        def empresaDaoMock = Mock(EmpresaRepository)
        def service = new EmpresaService(empresaDaoMock)
        def empresa = new Empresa("Tech", "tech@email.com", "11.111.111/0001-11", "Brasil", "SP", "01000-000", "desc", "senha123")

        when:
        service.cadastrarEmpresa(empresa)

        then:
        1 * empresaDaoMock.inserirEmpresa({
            it.is(empresa) &&
                    it.senha != 'senha123' &&
                    BCrypt.checkpw('senha123', it.senha)
        }) >> 1
    }

    def "atualiza empresa e aplica hash bcrypt à nova senha"() {
        given:
        def empresaDaoMock = Mock(EmpresaRepository)
        def service = new EmpresaService(empresaDaoMock)
        def empresa = new Empresa("Tech Exemplo", "rh@example.test", "11.111.111/0001-11",
                "Brasil", "SP", "01000-000", "Descrição comercial fictícia", "senha-nova")
        empresa.id = 8

        when:
        service.atualizarEmpresaService(empresa)

        then:
        1 * empresaDaoMock.atualizarEmpresa({
            it.id == 8 && it.cnpj == '11111111000111' &&
                    BCrypt.checkpw('senha-nova', it.senha)
        }) >> true
    }

    def "normaliza CNPJ para somente digitos antes de cadastrar"() {
        given:
        def empresaDaoMock = Mock(EmpresaRepository)
        def service = new EmpresaService(empresaDaoMock)
        def empresa = new Empresa("Tech", "tech@email.com", "11.111.111/0001-11",
                "Brasil", "SP", "01000-000", "Descrição fictícia", "senha123")

        when:
        service.cadastrarEmpresa(empresa)

        then:
        1 * empresaDaoMock.inserirEmpresa({
            it.cnpj == '11111111000111'
        }) >> 1
    }

    def "deve lançar RegistroDuplicadoException quando o CNPJ já existe"() {
        given:
        def empresaDaoMock = Mock(EmpresaRepository)
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
        def empresaDaoMock = Mock(EmpresaRepository)
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
        def empresaDaoMock = Mock(EmpresaRepository)
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