package com.linketinder.server.handler

import com.linketinder.controller.EmpresaController
import com.linketinder.model.Empresa
import com.linketinder.server.SessaoHttp
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import groovy.json.JsonSlurper
import spock.lang.Specification

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URI

class EmpresaHandlerSpec extends Specification {

    def "retorna perfil proprio sem expor o hash da senha"() {
        given:
        def controller = Mock(EmpresaController)
        def handler = new EmpresaHandler(controller)
        def empresa = new Empresa('Empresa Exemplo', 'rh@example.test', '11111111000111',
                'Brasil', 'SP', '01000-000', 'Descrição fictícia', 'hash-sigiloso')
        empresa.id = 7
        String token = SessaoHttp.criar([id: 7, nome: 'Empresa Exemplo', tipo: 'EMPRESA']).token
        def resposta = criarExchange('GET', '/empresas/7', '', token)
        1 * controller.buscarEmpresaPorIdController(7) >> empresa

        when:
        handler.handle(resposta.exchange)
        String corpo = resposta.body.toString('UTF-8')
        def json = new JsonSlurper().parseText(corpo)

        then:
        json.id == 7
        json.nome == 'Empresa Exemplo'
        !corpo.contains('hash-sigiloso')
        1 * resposta.exchange.sendResponseHeaders(200, _)
    }

    def "atualiza perfil apenas com o mesmo ID da sessao"() {
        given:
        def controller = Mock(EmpresaController)
        def handler = new EmpresaHandler(controller)
        String token = SessaoHttp.criar([id: 7, nome: 'Empresa Exemplo', tipo: 'EMPRESA']).token
        def resposta = criarExchange('PUT', '/empresas/7', '''
            {"nome":"Empresa Atualizada","email":"rh@example.test",
             "cnpj":"11.111.111/0001-11","pais":"Brasil","estado":"SP",
             "cep":"01000-000","descricao":"Descrição fictícia","senha":""}
        ''', token)
        1 * controller.atualizarEmpresaController({
            it.id == 7 && it.nome == 'Empresa Atualizada' && it.senha == ''
        })

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        1 * resposta.exchange.sendResponseHeaders(200, _)
        json.mensagem == 'Perfil atualizado.'
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
