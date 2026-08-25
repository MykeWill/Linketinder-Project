package com.linketinder.service

import com.linketinder.model.Candidato
import com.linketinder.repository.CandidatoRepository
import spock.lang.Specification

class CandidatoServiceSpec extends Specification {

    def "deve listar candidatos delegando ao repositório"() {
        given:
        def repositoryMock = Mock(CandidatoRepository)
        def service = new CandidatoService(repositoryMock)
        def listaEsperada = [new Candidato("Teste", "teste@email.com", "000", 30, "SP", "000", "", [])]

        when:
        def resultado = service.listarTodosCandidatosService()

        then:
        1 * repositoryMock.listarTodosCandidatosRepository() >> listaEsperada
        resultado == listaEsperada
    }

    def "deve cadastrar um candidato chamando o repositório"() {
        given:
        def repositoryMock = Mock(CandidatoRepository)
        def service = new CandidatoService(repositoryMock)
        def candidato = new Candidato("João", "joao@email.com", "111", 25, "RJ", "000", "", [])

        when:
        service.cadastrarCandidatoService(candidato)

        then:
        1 * repositoryMock.adicionarCandidatoRepository(candidato)
    }
}