package com.linketinder.server.handler

import com.linketinder.controller.LoginController
import com.linketinder.exception.DadosInvalidosException
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import groovy.json.JsonSlurper
import spock.lang.Specification

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URI

class LoginHandlerSpec extends Specification {

    def "login retorna a sessão para credenciais válidas"() {
        given:
        def controller = Mock(LoginController)
        def handler = new LoginHandler(controller)
        def resposta = criarExchange('POST', '/login',
                '{"email":"ana@example.com","senha":"senha-segura","tipo":"CANDIDATO"}')
        1 * controller.autenticar('ana@example.com', 'senha-segura', 'CANDIDATO') >>
                [id: 42, nome: 'Ana Souza', tipo: 'CANDIDATO']

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        1 * resposta.exchange.sendResponseHeaders(200, _)
        json.id == 42
        json.nome == 'Ana Souza'
        json.tipo == 'CANDIDATO'
        json.token ==~ /^[0-9a-f-]{36}$/
    }

    def "login retorna 401 para credenciais inválidas"() {
        given:
        def controller = Mock(LoginController)
        def handler = new LoginHandler(controller)
        def resposta = criarExchange('POST', '/login',
                '{"email":"ana@example.com","senha":"incorreta","tipo":"CANDIDATO"}')
        1 * controller.autenticar('ana@example.com', 'incorreta', 'CANDIDATO') >>
                { throw new DadosInvalidosException('Credenciais inválidas.') }

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        1 * resposta.exchange.sendResponseHeaders(401, _)
        json.erro == 'Credenciais inválidas.'
    }

    private Map criarExchange(String metodo, String caminho, String corpo) {
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
