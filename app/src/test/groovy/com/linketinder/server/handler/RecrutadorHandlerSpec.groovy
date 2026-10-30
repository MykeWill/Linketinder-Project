package com.linketinder.server.handler

import com.linketinder.controller.RecrutadorController
import com.linketinder.model.Candidato
import com.linketinder.model.Match
import com.linketinder.service.RecrutadorService
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import groovy.json.JsonSlurper
import spock.lang.Specification

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URI

class RecrutadorHandlerSpec extends Specification {

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
        json[0].id == 7
        json[0].descricao == 'Descrição profissional'
        json[0].competencias == ['Groovy']
        !resposta.body.toString('UTF-8').contains('Nome Real')
        !resposta.body.toString('UTF-8').contains('candidato@exemplo.com')
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

    private Map criarExchange(String metodo, String caminho, String corpo = '') {
        def exchange = Mock(HttpExchange)
        def output = new ByteArrayOutputStream()
        exchange.requestMethod >> metodo
        exchange.requestURI >> URI.create(caminho)
        exchange.requestBody >> new ByteArrayInputStream(corpo.getBytes('UTF-8'))
        exchange.responseHeaders >> new Headers()
        exchange.responseBody >> output
        [exchange: exchange, body: output]
    }
}
