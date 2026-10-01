package com.linketinder.service

import com.linketinder.exception.ErroBancoException
import com.linketinder.exception.RegistroDuplicadoException
import com.linketinder.model.Candidato
import com.linketinder.repository.CandidatoRepository
import com.linketinder.repository.CompetenciaRepository
import spock.lang.Specification

class CandidatoServiceSpec extends Specification {

    def "deve listar candidatos delegando ao repositório"() {
        given:
        def candidatoDaoMock = Mock(CandidatoRepository)
        def competenciaDaoMock = Mock(CompetenciaRepository)
        def service = new CandidatoService(candidatoDaoMock, competenciaDaoMock)
        def listaEsperada = [
                new Candidato("Teste", "teste@email.com", "000.000.000-00", 30, "SP", "00000-000", "desc", "senha123", [])
        ]

        when:
        def resultado = service.listarTodosCandidatosService()

        then:
        1 * candidatoDaoMock.listarTodosCandidatos() >> listaEsperada
        resultado == listaEsperada
    }

    def "deve cadastrar candidato e vincular suas competências"() {
        given:
        def candidatoDaoMock = Mock(CandidatoRepository)
        def competenciaDaoMock = Mock(CompetenciaRepository)
        def service = new CandidatoService(candidatoDaoMock, competenciaDaoMock)
        def candidato = new Candidato("João", "joao@email.com", "111.222.333-44", 25, "RJ", "20000-000", "desc", "senha123", ["Java", "SQL"])

        when:
        service.cadastrarCandidato(candidato)

        then:
        1 * candidatoDaoMock.inserirCandidato(candidato) >> 42
        1 * competenciaDaoMock.buscarOuCriar("Java") >> 1
        1 * candidatoDaoMock.vincularCompetenciaAoCandidato(42, 1)
        1 * competenciaDaoMock.buscarOuCriar("SQL") >> 2
        1 * candidatoDaoMock.vincularCompetenciaAoCandidato(42, 2)
    }

    def "deve lançar RegistroDuplicadoException quando o e-mail já existe"() {
        given:
        def candidatoDaoMock = Mock(CandidatoRepository)
        def competenciaDaoMock = Mock(CompetenciaRepository)
        def service = new CandidatoService(candidatoDaoMock, competenciaDaoMock)
        def candidato = new Candidato("Maria", "maria@email.com", "555.666.777-88", 25, "RJ", "20000-000", "desc", "senha123", [])

        def sqlException = new java.sql.SQLException('ERROR: duplicate key value violates unique constraint "candidato_email_key"')

        when:
        service.cadastrarCandidato(candidato)

        then:
        1 * candidatoDaoMock.inserirCandidato(candidato) >> { throw sqlException }
        thrown(RegistroDuplicadoException)
    }

    def "deve lançar ErroBancoException em erros genéricos do banco"() {
        given:
        def candidatoDaoMock = Mock(CandidatoRepository)
        def competenciaDaoMock = Mock(CompetenciaRepository)
        def service = new CandidatoService(candidatoDaoMock, competenciaDaoMock)
        def candidato = new Candidato("Ana", "ana@email.com", "999.888.777-66", 30, "SP", "01000-000", "desc", "senha123", [])

        def sqlException = new java.sql.SQLException('connection refused')

        when:
        service.cadastrarCandidato(candidato)

        then:
        1 * candidatoDaoMock.inserirCandidato(candidato) >> { throw sqlException }
        thrown(ErroBancoException)
    }
}