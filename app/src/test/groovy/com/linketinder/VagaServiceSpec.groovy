package com.linketinder.service

import com.linketinder.model.Vaga
import com.linketinder.repository.CompetenciaRepository
import com.linketinder.repository.VagaRepository
import spock.lang.Specification

class VagaServiceSpec extends Specification {

    def "cadastra vaga associada ao recrutador"() {
        given:
        def vagaRepository = Mock(VagaRepository)
        def competenciaRepository = Mock(CompetenciaRepository)
        def service = new VagaService(vagaRepository, competenciaRepository)
        def vaga = new Vaga(7, 'Desenvolvedor', 'Descrição fictícia', 'Remoto', [], null)

        when:
        Integer id = service.cadastrarVagaService(vaga, 23)

        then:
        1 * vagaRepository.inserirVaga({ it.recrutadorId == 23 }) >> 51
        id == 51
        vaga.recrutadorId == 23
    }

    def "cadastra vaga sem recrutador"() {
        given:
        def vagaRepository = Mock(VagaRepository)
        def competenciaRepository = Mock(CompetenciaRepository)
        def service = new VagaService(vagaRepository, competenciaRepository)
        def vaga = new Vaga(7, 'Analista', 'Descrição fictícia', 'Remoto', [])

        when:
        Integer id = service.cadastrarVagaService(vaga)

        then:
        1 * vagaRepository.inserirVaga({ it.recrutadorId == null }) >> 52
        id == 52
    }

    def "lista todas as vagas da empresa independentemente do recrutador criador"() {
        given:
        def vagaRepository = Mock(VagaRepository)
        def competenciaRepository = Mock(CompetenciaRepository)
        def service = new VagaService(vagaRepository, competenciaRepository)
        def esperadas = [
                new Vaga(7, 'Criada pela empresa', 'Descrição', 'Remoto', [], null),
                new Vaga(7, 'Criada pelo recrutador', 'Outra descrição', 'Remoto', [], 1)
        ]

        when:
        def resultado = service.listarVagasVisiveisParaRecrutador(7)

        then:
        1 * vagaRepository.listarVagasVisiveisParaRecrutador(7) >> esperadas
        resultado == esperadas
        resultado*.recrutadorId == [null, 1]
    }

    def "lista vagas ainda nao curtidas pelo candidato"() {
        given:
        def vagaRepository = Mock(VagaRepository)
        def competenciaRepository = Mock(CompetenciaRepository)
        def service = new VagaService(vagaRepository, competenciaRepository)
        def esperadas = [new Vaga(7, 'Desenvolvedor', 'Descrição', 'Remoto', [], 23)]

        when:
        def resultado = service.listarVagasNaoCurtidasPorCandidato(1)

        then:
        1 * vagaRepository.listarVagasNaoCurtidasPorCandidato(1) >> esperadas
        resultado == esperadas
    }
}
