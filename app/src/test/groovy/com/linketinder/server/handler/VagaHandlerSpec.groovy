package com.linketinder.server.handler

import com.linketinder.controller.VagaController
import com.linketinder.exception.ErroBancoException
import com.linketinder.model.Vaga
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import groovy.json.JsonSlurper
import spock.lang.Specification

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URI
import java.sql.SQLException

class VagaHandlerSpec extends Specification {

    def "erro de banco retorna mensagem generica sem expor causa SQL"() {
        given:
        def controller = Mock(VagaController)
        def handler = new VagaHandler(controller)
        def exchange = Mock(HttpExchange)
        def output = new ByteArrayOutputStream()
        exchange.requestMethod >> 'POST'
        exchange.requestURI >> URI.create('/vagas')
        exchange.requestBody >> new ByteArrayInputStream(
                '{"empresaId":1,"recrutadorId":1,"nome":"Vaga","descricao":"Descrição","local":"","competencias":["Java"]}'
                        .getBytes('UTF-8')
        )
        exchange.responseHeaders >> new Headers()
        exchange.responseBody >> output
        1 * controller.cadastrarVagaController(_ as Vaga) >> {
            throw new ErroBancoException(
                    'Erro ao acessar o banco de dados. Tente novamente.',
                    new SQLException('DETAIL: internal database diagnostic')
            )
        }

        when:
        handler.handle(exchange)
        String corpo = output.toString('UTF-8')
        def json = new JsonSlurper().parseText(corpo)

        then:
        1 * exchange.sendResponseHeaders(500, _)
        json.erro == 'Erro ao acessar o banco de dados. Tente novamente.'
        !corpo.contains('internal database diagnostic')
    }

    def "vagas anonimas retornam campos permitidos e id tecnico sem dados da empresa"() {
        given:
        def controller = Mock(VagaController)
        def handler = new VagaHandler(controller)
        def vaga = new Vaga(3, 'Desenvolvedor Groovy', 'Descrição fictícia', 'Local privado', ['Groovy'])
        vaga.id = 21
        def resposta = criarExchange('GET', '/vagas/anonimas?candidatoId=1')
        1 * controller.listarVagasNaoCurtidasPorCandidatoController(1) >> [vaga]

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

    def "vagas anonimas exigem um candidatoId positivo"() {
        given:
        def controller = Mock(VagaController)
        def handler = new VagaHandler(controller)
        def resposta = criarExchange('GET', '/vagas/anonimas')

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        json.erro == 'candidatoId inválido.'
        0 * controller._
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

    def "listagem por empresa inclui vagas sem recrutador criador"() {
        given:
        def controller = Mock(VagaController)
        def handler = new VagaHandler(controller)
        def vagaDaEmpresa = new Vaga(3, 'Criada pela empresa', 'Descrição fictícia',
                'Remoto', ['Java'])
        vagaDaEmpresa.id = 31
        def vagaDoRecrutador = new Vaga(3, 'Criada pelo recrutador', 'Outra descrição',
                'Remoto', ['SQL'], 1)
        vagaDoRecrutador.id = 32
        def resposta = criarExchange('GET', '/vagas?empresaId=3')
        1 * controller.listarVagasVisiveisParaRecrutadorController(3) >>
                [vagaDaEmpresa, vagaDoRecrutador]

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        json*.id == [31, 32]
        json*.empresaId == [3, 3]
        json*.nome == ['Criada pela empresa', 'Criada pelo recrutador']
    }

    def "remove vaga persistida pelo id"() {
        given:
        def controller = Mock(VagaController)
        def handler = new VagaHandler(controller)
        def resposta = criarExchange('DELETE', '/vagas/31')
        1 * controller.removerVagaController(31)

        when:
        handler.handle(resposta.exchange)
        def json = new JsonSlurper().parseText(resposta.body.toString('UTF-8'))

        then:
        1 * resposta.exchange.sendResponseHeaders(200, _)
        json.mensagem == 'Vaga removida.'
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
