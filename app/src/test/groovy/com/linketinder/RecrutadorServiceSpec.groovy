package com.linketinder

import com.linketinder.exception.DadosInvalidosException
import com.linketinder.exception.RegistroDuplicadoException
import com.linketinder.model.Candidato
import com.linketinder.model.Match
import com.linketinder.model.Recrutador
import com.linketinder.repository.RecrutadorRepository
import com.linketinder.service.RecrutadorService
import org.mindrot.jbcrypt.BCrypt
import spock.lang.Specification

import java.time.LocalDate

class RecrutadorServiceSpec extends Specification {

    def "lista recrutadores da empresa pelo repository"() {
        given:
        def repository = Mock(RecrutadorRepository)
        def service = new RecrutadorService(repository)
        def recrutador = novoRecrutador()
        recrutador.id = 23

        when:
        List<Recrutador> resultado = service.listarRecrutadoresPorEmpresa(1)

        then:
        1 * repository.listarRecrutadoresPorEmpresa(1) >> [recrutador]
        resultado == [recrutador]
    }

    def "lista matches da empresa do recrutador com os campos pós-match permitidos"() {
        given:
        def repository = Mock(RecrutadorRepository)
        def service = new RecrutadorService(repository)
        def match = new Match(
                'Empresa Fictícia',
                'Descrição da empresa',
                'empresa@exemplo.com',
                'Recrutador Fictício',
                'Tecnologia',
                'recrutador@exemplo.com',
                'Candidato Fictício',
                30,
                'candidato@exemplo.com',
                ['Groovy', 'SQL'],
                'Descrição profissional',
                'SP'
        )

        when:
        List<Match> resultado = service.listarMatchesDoRecrutador(23)

        then:
        1 * repository.listarMatchesDoRecrutador(23) >> [match]
        resultado == [match]
        resultado.first().candidatoCompetencias == ['Groovy', 'SQL']
        !resultado.first().properties.keySet().any {
            it in ['cpf', 'dataNascimento', 'candidatoCpf', 'candidatoDataNascimento',
                   'recrutadorCpf', 'recrutadorDataNascimento']
        }
    }

    def "retorna lista vazia quando o recrutador não tem matches"() {
        given:
        def repository = Mock(RecrutadorRepository)
        def service = new RecrutadorService(repository)

        when:
        List<Match> resultado = service.listarMatchesDoRecrutador(23)

        then:
        1 * repository.listarMatchesDoRecrutador(23) >> []
        resultado.empty
    }

    def "remove recrutador pelo repository"() {
        given:
        def repository = Mock(RecrutadorRepository)
        def service = new RecrutadorService(repository)

        when:
        service.removerRecrutador(23)

        then:
        1 * repository.removerRecrutador(23)
    }

    def "lista candidatos disponíveis sem carregar dados pessoais"() {
        given:
        def repository = Mock(RecrutadorRepository)
        def service = new RecrutadorService(repository)
        def candidatoAnonimo = new Candidato(
                null, null, null, 0, null, null, 'Experiência profissional', null, ['Groovy']
        )
        candidatoAnonimo.id = 8

        when:
        def resultado = service.listarCandidatosDisponiveis(23)

        then:
        1 * repository.listarCandidatosDisponiveis(23) >> [candidatoAnonimo]
        resultado == [candidatoAnonimo]
    }

    def "curte candidato em nome da empresa do recrutador"() {
        given:
        def repository = Mock(RecrutadorRepository)
        def service = new RecrutadorService(repository)
        def recrutador = novoRecrutador()
        recrutador.id = 23

        when:
        service.curtirCandidato(23, 8)

        then:
        1 * repository.buscarRecrutadorPorId(23) >> recrutador
        1 * repository.empresaJaCurtiuCandidato(1, 8) >> false
        1 * repository.curtirCandidato(1, 8, 23)
    }

    def "não repete curtida já feita pela empresa"() {
        given:
        def repository = Mock(RecrutadorRepository)
        def service = new RecrutadorService(repository)
        def recrutador = novoRecrutador()
        recrutador.id = 23

        when:
        service.curtirCandidato(23, 8)

        then:
        1 * repository.buscarRecrutadorPorId(23) >> recrutador
        1 * repository.empresaJaCurtiuCandidato(1, 8) >> true
        0 * repository.curtirCandidato(_, _, _)
        thrown(RegistroDuplicadoException)
    }

    def "rejeita e-mail inválido"() {
        given:
        def repository = Mock(RecrutadorRepository)
        def service = new RecrutadorService(repository)
        def recrutador = novoRecrutador('email-invalido')

        when:
        service.cadastrarRecrutador(recrutador, 'senha-ficticia')

        then:
        thrown(DadosInvalidosException)
        0 * repository._
    }

    def "aceita recrutador@exemplo.com"() {
        given:
        def repository = Mock(RecrutadorRepository)
        def service = new RecrutadorService(repository)
        def recrutador = novoRecrutador('recrutador@exemplo.com')

        when:
        Integer id = service.cadastrarRecrutador(recrutador, 'senha-ficticia')

        then:
        1 * repository.existeEmail('recrutador@exemplo.com') >> false
        1 * repository.existeCpf(recrutador.cpf) >> false
        1 * repository.inserirRecrutador(_ as Recrutador) >> 42
        id == 42
    }

    def "rejeita e-mail duplicado"() {
        given:
        def repository = Mock(RecrutadorRepository)
        def service = new RecrutadorService(repository)
        def recrutador = novoRecrutador()

        when:
        service.cadastrarRecrutador(recrutador, 'senha-ficticia')

        then:
        1 * repository.existeEmail(recrutador.email) >> true
        0 * repository._
        thrown(RegistroDuplicadoException)
    }

    def "rejeita CPF inválido"() {
        given:
        def repository = Mock(RecrutadorRepository)
        def service = new RecrutadorService(repository)
        def recrutador = novoRecrutador('recrutador@exemplo.com', '111.111.111-11')

        when:
        service.cadastrarRecrutador(recrutador, 'senha-ficticia')

        then:
        thrown(DadosInvalidosException)
        0 * repository._
    }

    def "rejeita CPF duplicado"() {
        given:
        def repository = Mock(RecrutadorRepository)
        def service = new RecrutadorService(repository)
        def recrutador = novoRecrutador()

        when:
        service.cadastrarRecrutador(recrutador, 'senha-ficticia')

        then:
        1 * repository.existeEmail(recrutador.email) >> false
        1 * repository.existeCpf(recrutador.cpf) >> true
        0 * repository._
        thrown(RegistroDuplicadoException)
    }

    def "rejeita recrutador menor de 18 anos"() {
        given:
        def repository = Mock(RecrutadorRepository)
        def service = new RecrutadorService(repository)
        def recrutador = novoRecrutador()
        recrutador.dataNascimento = LocalDate.now().minusYears(18).plusDays(1)

        when:
        service.cadastrarRecrutador(recrutador, 'senha-ficticia')

        then:
        thrown(DadosInvalidosException)
        0 * repository._
    }

    def "cadastra recrutador usando hash bcrypt em vez da senha pura"() {
        given:
        def repository = Mock(RecrutadorRepository)
        def service = new RecrutadorService(repository)
        def recrutador = novoRecrutador()
        String senhaPura = 'senha-ficticia'

        when:
        Integer id = service.cadastrarRecrutador(recrutador, senhaPura)

        then:
        1 * repository.existeEmail(recrutador.email) >> false
        1 * repository.existeCpf(recrutador.cpf) >> false
        1 * repository.inserirRecrutador({
            it.senhaHash != senhaPura && BCrypt.checkpw(senhaPura, it.senhaHash)
        }) >> 42
        id == 42
    }

    private Recrutador novoRecrutador(String email = 'recrutador@exemplo.com',
                                       String cpf = '529.982.247-25') {
        new Recrutador(
                1,
                'Recrutador Fictício',
                'Tecnologia',
                'Descrição fictícia',
                email,
                cpf,
                LocalDate.now().minusYears(30),
                null
        )
    }
}
