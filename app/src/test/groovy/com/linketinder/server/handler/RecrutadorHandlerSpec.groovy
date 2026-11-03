package com.linketinder.server.handler

import com.linketinder.controller.RecrutadorController
import com.linketinder.model.Candidato
import com.linketinder.model.Match
import com.linketinder.model.Recrutador
import com.linketinder.server.SessaoHttp
import com.linketinder.service.RecrutadorService
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import groovy.json.JsonSlurper
import spock.lang.Specification

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URI
import java.time.LocalDate

class RecrutadorHandlerSpec extends Specification {

    def "recrutadores da empresa retornam somente campos permitidos"() {
        given:
        def service = Mock(RecrutadorService)
        def controller = new RecrutadorController(service)
        def handler = new RecrutadorHandler(controller)
        def recrutador = new Recrutador(
                1, 'Recrutador Fictício', 'Tecnologia', 'Descrição interna',
                'recrutador@exemplo.com', '529.982.247-25',
                LocalDate.parse('1990-01-01'), 'hash-ficticio'
        )
        recrutador.id = 12
        def resposta = criarExchange('GET', '/recrutadores?empresaId=1')
        1 * service.listarRecrutadoresPorEmpresa(1) >> [recrutador]

        when:
        handler.handle(resposta.exchange)
        String corpo = resposta.body.toString('UTF-8')
        def json = new JsonSlurper().parseText(corpo)

        then:
        json[0].keySet().toSet() == ['id', 'nome', 'areaRecrutamento', 'descricao', 'email'].toSet()
        json[0].id == 12
        json[0].nome == 'Recrutador Fictício'
        json[0].areaRecrutamento == 'Tecnologia'
        json[0].descricao == 'Descrição interna'
        json[0].email == 'recrutador@exemplo.com'
        !corpo.contains('529.982.247-25')
        !corpo.contains('1990-01-01')
        !corpo.contains('hash-ficticio')
    }

    def "candidatos disponíveis retornam somente campos anônimos permitidos"() {
        given:
        def service = Mock(RecrutadorService)
        def controller = new RecrutadorController(service)
        def handler = new RecrutadorHandler(controller)
        def candidato = new Candidato(
                'Nome Real',
                'candidato@exemplo.com',
                '529.982.247-25',
                30,
                'SP',
                '01000-000',
                'Descrição profissional',
                'senha-ficticia',
                ['Groovy']
        )
        candidato.id = 7
        def resposta = criarExchange('GET', '/recrutadores/42/candidatos')
        1 * service.listarCandidatosDisponiveis(42) >> [candidato]

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        json[0].keySet().toSet() == ['id', 'descricao', 'competencias'].toSet()
        !json[0].containsKey('nome')
        json[0].id == 7
        json[0].descricao == 'Descrição profissional'
        json[0].competencias == ['Groovy']
        !resposta.body.toString('UTF-8').contains('Nome Real')
        !resposta.body.toString('UTF-8').contains('candidato@exemplo.com')
    }

    def "curtida do recrutador retorna status do match"() {
        given:
        def service = Mock(RecrutadorService)
        def controller = new RecrutadorController(service)
        def handler = new RecrutadorHandler(controller)
        def resposta = criarExchange(
                'POST',
                '/recrutadores/42/curtidas',
                '{"candidatoId":7,"vagaId":31}'
        )
        1 * service.curtirCandidato(42, 7, 31) >> true

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        json == [match: true]
        1 * resposta.exchange.sendResponseHeaders(201, _)
    }

    def "matches não serializam campos sigilosos"() {
        given:
        def service = Mock(RecrutadorService)
        def controller = new RecrutadorController(service)
        def handler = new RecrutadorHandler(controller)
        def match = new Match(
                'Empresa',
                'Descrição',
                'empresa@exemplo.com',
                'Recrutador',
                'Tecnologia',
                'recrutador@exemplo.com',
                'Candidato',
                30,
                'candidato@exemplo.com',
                ['Groovy'],
                'Perfil profissional',
                'SP'
        )
        def resposta = criarExchange('GET', '/recrutadores/42/matches')
        1 * service.listarMatchesDoRecrutador(42) >> [match]

        when:
        handler.handle(resposta.exchange)
        String corpo = resposta.body.toString('UTF-8')
        def json = new JsonSlurper().parseText(corpo)

        then:
        json[0].candidatoNome == 'Candidato'
        json[0].candidatoCompetencias == ['Groovy']
        !corpo.toLowerCase().contains('cpf')
        !corpo.toLowerCase().contains('datanascimento')
    }

    def "cadastro responde somente com o id sem senha ou hash"() {
        given:
        def service = Mock(RecrutadorService)
        def controller = new RecrutadorController(service)
        def handler = new RecrutadorHandler(controller)
        def resposta = criarExchange('POST', '/recrutadores', '''{
            "empresaId": 2, "nome": "Recrutador Fictício", "areaRecrutamento": "Tecnologia",
            "descricao": "Perfil interno", "email": "recrutador@exemplo.com",
            "cpf": "529.982.247-25", "dataNascimento": "1990-01-01", "senha": "senha-ficticia"
        }''')
        1 * service.cadastrarRecrutador({ it.nome == 'Recrutador Fictício' }, 'senha-ficticia') >> 51

        when:
        handler.handle(resposta.exchange)
        String corpo = resposta.body.toString('UTF-8')
        def json = new JsonSlurper().parseText(corpo)

        then:
        json == [id: 51]
        !corpo.contains('senha')
        !corpo.contains('hash')
    }

    def "retorna detalhes de edicao sem CPF nem hash e exige empresa proprietaria"() {
        given:
        def service = Mock(RecrutadorService)
        def controller = new RecrutadorController(service)
        def handler = new RecrutadorHandler(controller)
        def recrutador = new Recrutador(
                1, 'Recrutador Fictício', 'Tecnologia', 'Descrição fictícia',
                'recrutador@exemplo.com', '52998224725',
                LocalDate.parse('1990-01-01'), 'hash-ficticio'
        )
        recrutador.id = 12
        String token = SessaoHttp.criar([id: 1, nome: 'Empresa', tipo: 'EMPRESA']).token
        def resposta = criarExchange('GET', '/recrutadores/12', '', token)
        1 * service.buscarRecrutadorDaEmpresa(12, 1) >> recrutador

        when:
        handler.handle(resposta.exchange)
        String corpo = resposta.body.toString('UTF-8')
        def json = new JsonSlurper().parseText(corpo)

        then:
        json.keySet().toSet() ==
                ['id', 'nome', 'areaRecrutamento', 'descricao', 'email', 'dataNascimento'].toSet()
        json.dataNascimento == '1990-01-01'
        !corpo.contains('cpf')
        !corpo.contains('52998224725')
        !corpo.contains('hash-ficticio')
    }

    def "atualiza recrutador com sessao da empresa e sem CPF no payload"() {
        given:
        def service = Mock(RecrutadorService)
        def controller = new RecrutadorController(service)
        def handler = new RecrutadorHandler(controller)
        String token = SessaoHttp.criar([id: 1, nome: 'Empresa', tipo: 'EMPRESA']).token
        def resposta = criarExchange('PUT', '/recrutadores/12', '''{
            "nome":"Recrutador Atualizado","areaRecrutamento":"Produto",
            "descricao":"Descrição atualizada","email":"novo@exemplo.com",
            "dataNascimento":"1990-01-01","senha":""
        }''', token)
        1 * service.atualizarRecrutador(
                { it.id == 12 && it.empresaId == 1 && it.cpf == null &&
                        it.nome == 'Recrutador Atualizado' },
                '', 1)

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        1 * resposta.exchange.sendResponseHeaders(200, _)
        json.mensagem == 'Recrutador atualizado.'
    }

    def "nega edicao de recrutador sem sessao de empresa"() {
        given:
        def service = Mock(RecrutadorService)
        def handler = new RecrutadorHandler(new RecrutadorController(service))
        def resposta = criarExchange('PUT', '/recrutadores/12', '{}')

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        1 * resposta.exchange.sendResponseHeaders(401, _)
        json.erro == 'Sessão inválida ou expirada.'
        0 * service._
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
