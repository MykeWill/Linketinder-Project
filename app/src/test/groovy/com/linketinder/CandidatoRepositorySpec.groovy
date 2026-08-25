package com.linketinder.repository

import com.linketinder.model.Candidato
import spock.lang.Specification

class CandidatoRepositorySpec extends Specification {

    def "deve inicializar com 5 candidatos"() {
        given:
        def repository = new CandidatoRepository()

        when:
        def lista = repository.listarTodosCandidatosRepository()

        then:
        lista.size() == 5
        lista[0].nome == "Ana Souza"
        lista[1].nome == "Bruno Lima"
        lista[2].nome == "Carla Dias"
        lista[3].nome == "Diego Alves"
        lista[4].nome == "Elisa Prado"
    }

    def "deve adicionar um novo candidato e atualizar a lista"() {
        given:
        def repository = new CandidatoRepository()
        def novoCandidato = new Candidato(
                "Maria Silva",
                "maria@email.com",
                "999.999.999-99",
                26,
                "CE",
                "60000-000",
                "Analista de dados",
                ["Python", "Pandas"]
        )

        when:
        repository.adicionarCandidatoRepository(novoCandidato)

        then:
        def lista = repository.listarTodosCandidatosRepository()
        lista.size() == 6
        lista.contains(novoCandidato)
        lista.last().nome == "Maria Silva"
    }
}