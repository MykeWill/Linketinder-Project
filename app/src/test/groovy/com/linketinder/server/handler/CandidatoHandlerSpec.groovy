package com.linketinder.server.handler

import com.linketinder.controller.CandidatoController
import com.linketinder.model.Candidato
import com.linketinder.repository.CandidatoRepository
import com.linketinder.repository.CompetenciaRepository
import com.linketinder.server.SessaoHttp
import com.linketinder.service.CandidatoService
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import groovy.json.JsonSlurper
import org.mindrot.jbcrypt.BCrypt
import spock.lang.Specification

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URI

class CandidatoHandlerSpec extends Specification {

    def "cadastro converte idade para inteiro e encaminha competencias como lista"() {
        given:
        def candidatoRepository = Mock(CandidatoRepository)
        def competenciaRepository = Mock(CompetenciaRepository)
        def service = new CandidatoService(candidatoRepository, competenciaRepository)
        def controller = new CandidatoController(service)
        def handler = new CandidatoHandler(controller)
        def resposta = criarExchange('POST', '/candidatos', '''
            {
                "nome": "Teste Fictício",
                "email": "teste.candidato@example.test",
                "cpf": "123.456.789-10",
                "idade": 30,
                "estado": "SP",
                "cep": "01000-000",
                "descricao": "Perfil fictício para teste",
                "senha": "senha-ficticia",
                "competencias": ["Java", "SQL"]
            }
        ''')

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        1 * candidatoRepository.inserirCandidato({
            Candidato candidato ->
                candidato.nome == 'Teste Fictício' &&
                        candidato.email == 'teste.candidato@example.test' &&
                        candidato.cpf == '12345678910' &&
                        candidato.idade.class == Integer &&
                        candidato.idade == 30 &&
                        candidato.estado == 'SP' &&
                        candidato.cep == '01000-000' &&
                        candidato.descricao == 'Perfil fictício para teste' &&
                        BCrypt.checkpw('senha-ficticia', candidato.senha) &&
                        candidato.competencias == ['Java', 'SQL']
        }) >> 56
        1 * competenciaRepository.buscarOuCriar('Java') >> 10
        1 * candidatoRepository.vincularCompetenciaAoCandidato(56, 10)
        1 * competenciaRepository.buscarOuCriar('SQL') >> 11
        1 * candidatoRepository.vincularCompetenciaAoCandidato(56, 11)
        1 * resposta.exchange.sendResponseHeaders(201, _)
        json == [id: 56]
    }

    def "listagem de candidatos nao expoe dados pessoais"() {
        given:
        def candidatoRepository = Mock(CandidatoRepository)
        def competenciaRepository = Mock(CompetenciaRepository)
        def service = new CandidatoService(candidatoRepository, competenciaRepository)
        def controller = new CandidatoController(service)
        def handler = new CandidatoHandler(controller)
        def resposta = criarExchange('GET', '/candidatos')
        1 * candidatoRepository.listarCandidatosAnonimos() >> [[
                id: 7,
                descricao: 'Descrição profissional',
                competencias: ['Groovy'],
                nome: 'Nome privado',
                email: 'privado@exemplo.com',
                cpf: '529.982.247-25',
                idade: 30,
                estado: 'SP',
                cep: '01000-000'
        ]]

        when:
        handler.handle(resposta.exchange)
        String corpo = resposta.body.toString('UTF-8')
        def json = new JsonSlurper().parseText(corpo)

        then:
        json[0].keySet().toSet() == ['id', 'descricao', 'competencias'].toSet()
        !corpo.contains('Nome privado')
        !corpo.contains('privado@exemplo.com')
        !corpo.contains('529.982.247-25')
        !corpo.contains('01000-000')
    }

    def "contagem por competencia retorna somente valores agregados"() {
        given:
        def candidatoRepository = Mock(CandidatoRepository)
        def service = new CandidatoService(candidatoRepository, Mock(CompetenciaRepository))
        def handler = new CandidatoHandler(new CandidatoController(service))
        def resposta = criarExchange('GET', '/candidatos/competencias')
        1 * candidatoRepository.contarCandidatosPorCompetencia() >> [Groovy: 2, Java: 1]

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        json == [Groovy: 2, Java: 1]
    }

    def "matches do candidato retornam somente contatos permitidos apos o match"() {
        given:
        def candidatoRepository = Mock(CandidatoRepository)
        def competenciaRepository = Mock(CompetenciaRepository)
        def service = new CandidatoService(candidatoRepository, competenciaRepository)
        def controller = new CandidatoController(service)
        def handler = new CandidatoHandler(controller)
        def resposta = criarExchange('GET', '/candidatos/7/matches')
        1 * candidatoRepository.listarMatchesDoCandidato(7) >> [[
                vagaId: 42,
                vagaNome: 'Desenvolvedor Groovy',
                vagaDescricao: 'Vaga fictícia',
                empresaNome: 'Empresa Fictícia',
                empresaDescricao: 'Descrição comercial',
                empresaEmail: 'contato@exemplo.com',
                recrutadorId: 12,
                recrutadorNome: 'Recrutador Fictício',
                recrutadorAreaRecrutamento: 'Tecnologia',
                recrutadorEmail: 'recrutador@exemplo.com',
                candidatoCpf: '529.982.247-25',
                recrutadorDataNascimento: '1990-01-01',
                recrutadorDescricao: 'Descrição interna'
        ]]

        when:
        handler.handle(resposta.exchange)
        String corpo = resposta.body.toString('UTF-8')
        def json = new JsonSlurper().parseText(corpo)

        then:
        json[0].keySet().toSet() == [
                'vagaId', 'vagaNome', 'vagaDescricao',
                'empresaNome', 'empresaDescricao', 'empresaEmail', 'recrutadorId',
                'recrutadorNome', 'recrutadorAreaRecrutamento', 'recrutadorEmail'
        ].toSet()
        json[0].vagaId == 42
        json[0].vagaNome == 'Desenvolvedor Groovy'
        json[0].vagaDescricao == 'Vaga fictícia'
        json[0].empresaNome == 'Empresa Fictícia'
        json[0].empresaDescricao == 'Descrição comercial'
        json[0].empresaEmail == 'contato@exemplo.com'
        json[0].recrutadorNome == 'Recrutador Fictício'
        json[0].recrutadorEmail == 'recrutador@exemplo.com'
        !corpo.contains('529.982.247-25')
        !corpo.contains('1990-01-01')
        !corpo.contains('Descrição interna')
    }

    def "curtidas do candidato retornam somente dados anonimos da vaga"() {
        given:
        def candidatoRepository = Mock(CandidatoRepository)
        def competenciaRepository = Mock(CompetenciaRepository)
        def service = new CandidatoService(candidatoRepository, competenciaRepository)
        def controller = new CandidatoController(service)
        def handler = new CandidatoHandler(controller)
        def resposta = criarExchange('GET', '/candidatos/7/curtidas')
        1 * candidatoRepository.listarCurtidasDoCandidato(7) >> [[
                id: 42,
                nome: 'Desenvolvedor Groovy',
                descricao: 'Vaga fictícia',
                competencias: ['Groovy'],
                empresaNome: 'Empresa privada',
                local: 'Endereço privado'
        ]]

        when:
        handler.handle(resposta.exchange)
        String corpo = resposta.body.toString('UTF-8')
        def json = new JsonSlurper().parseText(corpo)

        then:
        json[0].keySet().toSet() == ['id', 'nome', 'descricao', 'competencias'].toSet()
        json[0].id == 42
        json[0].nome == 'Desenvolvedor Groovy'
        json[0].descricao == 'Vaga fictícia'
        json[0].competencias == ['Groovy']
        !corpo.contains('Empresa privada')
        !corpo.contains('Endereço privado')
    }

    def "endpoint curtir vaga retorna o resultado do match"() {
        given:
        def candidatoRepository = Mock(CandidatoRepository)
        def competenciaRepository = Mock(CompetenciaRepository)
        def service = new CandidatoService(candidatoRepository, competenciaRepository)
        def controller = new CandidatoController(service)
        def handler = new CandidatoHandler(controller)
        def resposta = criarExchange('POST', '/candidatos/7/curtir-vaga', '{"vagaId":42}')
        1 * candidatoRepository.curtirVaga(7, 42)
        1 * candidatoRepository.existeCurtidaDaEmpresaParaVaga(7, 42) >> true

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        json == [match: true]
    }

    def "atualiza somente o perfil correspondente ao ID da sessao"() {
        given:
        def controller = Mock(CandidatoController)
        def handler = new CandidatoHandler(controller)
        String token = SessaoHttp.criar([id: 7, nome: 'Ana Souza', tipo: 'CANDIDATO']).token
        def resposta = criarExchange('PUT', '/candidatos/7', '''
            {"nome":"Ana Souza","email":"ana@example.test",
             "cpf":"529.982.247-25","idade":30,"estado":"SP","cep":"01000-000",
             "descricao":"Descrição fictícia","senha":"","competencias":["Groovy"]}
        ''', token)
        1 * controller.atualizarCandidatoController({
            it.id == 7 && it.nome == 'Ana Souza' &&
                    it.competencias == ['Groovy'] && it.senha == ''
        })

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        1 * resposta.exchange.sendResponseHeaders(200, _)
        json.mensagem == 'Perfil atualizado.'
    }

    def "nega leitura do perfil quando o ID diverge da sessao"() {
        given:
        def controller = Mock(CandidatoController)
        def handler = new CandidatoHandler(controller)
        String token = SessaoHttp.criar([id: 7, nome: 'Ana Souza', tipo: 'CANDIDATO']).token
        def resposta = criarExchange('GET', '/candidatos/8', '', token)

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        1 * resposta.exchange.sendResponseHeaders(403, _)
        json.erro == 'Acesso não autorizado.'
        0 * controller._
    }

    private Map criarExchange(String metodo, String caminho, String corpo = '', String token = null) {
        def exchange = Mock(HttpExchange)
        def output = new ByteArrayOutputStream()
        def headers = new Headers()
        if (token) {
            headers.set('Authorization', "Bearer ${token}")
        }
        exchange.requestMethod >> metodo
        exchange.requestURI >> URI.create(caminho)
        exchange.requestBody >> new ByteArrayInputStream(corpo.getBytes('UTF-8'))
        exchange.requestHeaders >> headers
        exchange.responseHeaders >> new Headers()
        exchange.responseBody >> output
        [exchange: exchange, body: output]
    }
}
