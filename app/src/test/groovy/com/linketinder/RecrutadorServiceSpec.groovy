package com.linketinder

import com.linketinder.exception.DadosInvalidosException
import com.linketinder.exception.RegistroDuplicadoException
import com.linketinder.model.Recrutador
import com.linketinder.repository.RecrutadorRepository
import com.linketinder.service.RecrutadorService
import org.mindrot.jbcrypt.BCrypt
import spock.lang.Specification

import java.time.LocalDate

class RecrutadorServiceSpec extends Specification {

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
