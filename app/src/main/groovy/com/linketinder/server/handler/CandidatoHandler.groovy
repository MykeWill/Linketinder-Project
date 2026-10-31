package com.linketinder.server.handler

import com.linketinder.controller.CandidatoController
import com.linketinder.model.Candidato
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.json.JsonSlurper

class CandidatoHandler implements HttpHandler {

    private final CandidatoController candidatoController

    CandidatoHandler(CandidatoController candidatoController) {
        this.candidatoController = candidatoController
    }

    @Override
    void handle(HttpExchange exchange) {
        try {
            String path = exchange.requestURI.path
            String method = exchange.requestMethod

            if (path == '/candidatos' && method == 'POST') {
                tratarPost(exchange)
            } else if (path == '/candidatos' && method == 'GET') {
                tratarGet(exchange)
            } else {
                def matches = path =~ /^\/candidatos\/(\d+)\/matches$/
                if (!matches.matches()) {
                    responder(exchange, 404, [erro: 'Rota não encontrada'])
                } else if (method != 'GET') {
                    responder(exchange, 405, [erro: 'Método não permitido'])
                } else {
                    tratarMatches(exchange, inteiroPositivo(matches[0][1]))
                }
            }
        } catch (Exception e) {
            responder(exchange, 400, [erro: e.message])
        } finally {
            exchange.close()
        }
    }

    private void tratarPost(HttpExchange exchange) {
        String corpo = exchange.requestBody.text
        def json = new JsonSlurper().parseText(corpo)

        Candidato candidato = new Candidato(
                json.nome,
                json.email,
                json.cpf,
                json.idade as Integer,
                json.estado,
                json.cep,
                json.descricao,
                json.senha,
                json.competencias ?: []
        )

        Integer id = candidatoController.cadastrarCandidatoController(candidato)

        responder(exchange, 201, [id: id])
    }

    private void tratarGet(HttpExchange exchange) {
        List<Map> candidatos = candidatoController.listarCandidatosAnonimosController()
        def resposta = candidatos.collect { candidato ->
            [
                    id          : candidato.id,
                    descricao   : candidato.descricao,
                    competencias: candidato.competencias
            ]
        }

        responder(exchange, 200, resposta)
    }

    private void tratarMatches(HttpExchange exchange, Integer candidatoId) {
        List<Map> matches = candidatoController.listarMatchesDoCandidatoController(candidatoId)
        responder(exchange, 200, matches.collect { Map match ->
            [
                    empresaNome                : match.empresaNome,
                    empresaDescricao           : match.empresaDescricao,
                    empresaEmail               : match.empresaEmail,
                    recrutadorId               : match.recrutadorId,
                    recrutadorNome             : match.recrutadorNome,
                    recrutadorAreaRecrutamento : match.recrutadorAreaRecrutamento,
                    recrutadorEmail            : match.recrutadorEmail
            ]
        })
    }

    private static Integer inteiroPositivo(String valor) {
        int id = new BigDecimal(valor).intValueExact()
        if (id <= 0) {
            throw new IllegalArgumentException('ID do candidato inválido.')
        }
        return id
    }

    private void responder(HttpExchange exchange, int status, Object corpo) {
        String json = new groovy.json.JsonOutput().toJson(corpo)
        byte[] bytes = json.getBytes("UTF-8")

        exchange.responseHeaders.set("Content-Type", "application/json; charset=UTF-8")
        exchange.sendResponseHeaders(status, bytes.length)

        exchange.responseBody.withStream { it.write(bytes) }
    }
}