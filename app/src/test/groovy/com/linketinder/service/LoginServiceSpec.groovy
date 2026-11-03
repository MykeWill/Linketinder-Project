package com.linketinder.service

import com.linketinder.exception.DadosInvalidosException
import com.linketinder.model.Candidato
import com.linketinder.model.Empresa
import com.linketinder.model.Recrutador
import com.linketinder.repository.CandidatoRepository
import com.linketinder.repository.EmpresaRepository
import com.linketinder.repository.RecrutadorRepository
import org.mindrot.jbcrypt.BCrypt
import spock.lang.Specification

import java.time.LocalDate

class LoginServiceSpec extends Specification {

    def "autentica candidato com credenciais válidas"() {
        given:
        def candidatoRepository = Mock(CandidatoRepository)
        def empresaRepository = Mock(EmpresaRepository)
        def recrutadorRepository = Mock(RecrutadorRepository)
        def service = new LoginService(candidatoRepository, empresaRepository, recrutadorRepository)
        def candidato = new Candidato('Ana Souza', 'ana@example.com', '123.456.789-00',
                28, 'SP', '01000-000', 'Descrição fictícia',
                BCrypt.hashpw('senha-segura', BCrypt.gensalt()), [])
        candidato.id = 42

        when:
        def sessao = service.autenticar('ana@example.com', 'senha-segura', 'CANDIDATO')

        then:
        1 * candidatoRepository.buscarPorEmail('ana@example.com') >> candidato
        0 * empresaRepository._
        0 * recrutadorRepository._
        sessao == [id: 42, nome: 'Ana Souza', tipo: 'CANDIDATO']
    }

    def "autentica empresa com credenciais válidas"() {
        given:
        def candidatoRepository = Mock(CandidatoRepository)
        def empresaRepository = Mock(EmpresaRepository)
        def recrutadorRepository = Mock(RecrutadorRepository)
        def service = new LoginService(candidatoRepository, empresaRepository, recrutadorRepository)
        def empresa = new Empresa('Empresa Exemplo', 'rh@example.com', '12.345.678/0001-90',
                'Brasil', 'SP', '01000-000', 'Descrição fictícia',
                BCrypt.hashpw('senha-segura', BCrypt.gensalt()))
        empresa.id = 73

        when:
        def sessao = service.autenticar('rh@example.com', 'senha-segura', 'empresa')

        then:
        1 * empresaRepository.buscarPorEmail('rh@example.com') >> empresa
        0 * candidatoRepository._
        0 * recrutadorRepository._
        sessao == [id: 73, nome: 'Empresa Exemplo', tipo: 'EMPRESA']
    }

    def "autentica recrutador com senha bcrypt e inclui empresa associada na sessão"() {
        given:
        def candidatoRepository = Mock(CandidatoRepository)
        def empresaRepository = Mock(EmpresaRepository)
        def recrutadorRepository = Mock(RecrutadorRepository)
        def service = new LoginService(candidatoRepository, empresaRepository, recrutadorRepository)
        def recrutador = new Recrutador(19, 'Rui Silva', 'Tecnologia', 'Descrição fictícia',
                'rui@example.com', '123.456.789-09', LocalDate.of(1980, 1, 1),
                BCrypt.hashpw('senha-segura', BCrypt.gensalt()))
        recrutador.id = 81

        when:
        def sessao = service.autenticar('rui@example.com', 'senha-segura', 'RECRUTADOR')

        then:
        1 * recrutadorRepository.buscarPorEmail('rui@example.com') >> recrutador
        0 * candidatoRepository._
        0 * empresaRepository._
        sessao == [id: 81, nome: 'Rui Silva', tipo: 'RECRUTADOR', empresaId: 19]
    }

    def "rejeita senha inválida"() {
        given:
        def candidatoRepository = Mock(CandidatoRepository)
        def service = new LoginService(
                candidatoRepository,
                Mock(EmpresaRepository),
                Mock(RecrutadorRepository)
        )
        def candidato = new Candidato('Ana Souza', 'ana@example.com', '123.456.789-00',
                28, 'SP', '01000-000', 'Descrição fictícia',
                BCrypt.hashpw('senha-correta', BCrypt.gensalt()), [])

        when:
        service.autenticar('ana@example.com', 'senha-incorreta', 'CANDIDATO')

        then:
        1 * candidatoRepository.buscarPorEmail('ana@example.com') >> candidato
        thrown(DadosInvalidosException)
    }
}
