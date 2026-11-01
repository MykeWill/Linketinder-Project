package com.linketinder.server.handler

import com.linketinder.controller.VagaController
import com.linketinder.model.Vaga
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import groovy.json.JsonSlurper
import spock.lang.Specification

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URI

class VagaHandlerSpec extends Specification {

    def "vagas anonimas retornam campos permitidos e id tecnico sem dados da empresa"() {
        given:
        def controller = Mock(VagaController)
        def handler = new VagaHandler(controller)
        def vaga = new Vaga(3, 'Desenvolvedor Groovy', 'Descrição fictícia', 'Local privado', ['Groovy'])
        vaga.id = 21
        def resposta = criarExchange('GET', '/vagas/anonimas')
        1 * controller.listarTodasVagasController() >> [vaga]

        when:
        handler.handle(resposta.exchange)
        String corpo = resposta.body.toString('UTF-8')
        def json = new JsonSlurper().parseText(corpo)

        then:
        json[0].keySet().toSet() == ['id', 'nome', 'descricao', 'competencias'].toSet()
        json[0].id == 21
        json[0].nome == 'Desenvolvedor Groovy'
        json[0].descricao == 'Descrição fictícia'
        json[0].competencias == ['Groovy']
        !corpo.contains('empresaId')
        !corpo.contains('Local privado')
    }

    def "listagem publica de vagas tambem permanece anonima"() {
        given:
        def controller = Mock(VagaController)
        def handler = new VagaHandler(controller)
        def vaga = new Vaga(3, 'Desenvolvedor Groovy', 'Descrição fictícia', 'Local privado', ['Groovy'])
        vaga.id = 21
        def resposta = criarExchange('GET', '/vagas')
        1 * controller.listarTodasVagasController() >> [vaga]

        when:
        handler.handle(resposta.exchange)
        String corpo = resposta.body.toString('UTF-8')
        def json = new JsonSlurper().parseText(corpo)

        then:
        json[0].keySet().toSet() == ['id', 'nome', 'descricao', 'competencias'].toSet()
        json[0].id == 21
        !corpo.contains('empresaId')
        !corpo.contains('Local privado')
    }

    private Map criarExchange(String metodo, String caminho) {
        def exchange = Mock(HttpExchange)
        def output = new ByteArrayOutputStream()
        exchange.requestMethod >> metodo
        exchange.requestURI >> URI.create(caminho)
        exchange.requestBody >> new ByteArrayInputStream(new byte[0])
        exchange.responseHeaders >> new Headers()
        exchange.responseBody >> output
        [exchange: exchange, body: output]
    }
}
