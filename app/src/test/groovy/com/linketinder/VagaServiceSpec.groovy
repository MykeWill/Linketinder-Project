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

    def "atualiza vaga somente após validar que pertence à empresa"() {
        given:
        def vagaRepository = Mock(VagaRepository)
        def competenciaRepository = Mock(CompetenciaRepository)
        def service = new VagaService(vagaRepository, competenciaRepository)
        def vaga = new Vaga(7, 'Desenvolvedor Sênior', 'Descrição atualizada',
                'Remoto', ['Groovy'])
        vaga.id = 51

        when:
        boolean atualizada = service.atualizarVagaService(vaga, 'EMPRESA', 7)

        then:
        1 * vagaRepository.vagaPertenceEmpresa(51, 7) >> true
        1 * competenciaRepository.buscarOuCriar('Groovy') >> 12
        1 * vagaRepository.atualizarVaga(vaga, 'EMPRESA', 7, [12]) >> true
        atualizada
    }

    def "nao atualiza vaga que nao pertence ao recrutador da sessao"() {
        given:
        def vagaRepository = Mock(VagaRepository)
        def service = new VagaService(vagaRepository, Mock(CompetenciaRepository))
        def vaga = new Vaga(7, 'Desenvolvedor', 'Descrição fictícia', 'Remoto', ['Java'])
        vaga.id = 51

        when:
        boolean atualizada = service.atualizarVagaService(vaga, 'RECRUTADOR', 23)

        then:
        1 * vagaRepository.vagaPertenceRecrutador(51, 23) >> false
        0 * vagaRepository.atualizarVaga(_, _, _, _)
        !atualizada
    }
}
