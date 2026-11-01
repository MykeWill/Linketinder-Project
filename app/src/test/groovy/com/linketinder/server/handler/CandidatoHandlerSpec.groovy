package com.linketinder.server.handler

import com.linketinder.controller.CandidatoController
import com.linketinder.repository.CandidatoRepository
import com.linketinder.repository.CompetenciaRepository
import com.linketinder.service.CandidatoService
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import groovy.json.JsonSlurper
import spock.lang.Specification

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URI

class CandidatoHandlerSpec extends Specification {

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
