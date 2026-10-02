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
            switch (exchange.requestMethod) {
                case "POST":
                    tratarPost(exchange)
                    break
                case "GET":
                    tratarGet(exchange)
                    break
                default:
                    responder(exchange, 405, [erro: "Método não permitido"])
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
        def candidatos = candidatoController.listarTodosCandidatosController()

        def resposta = candidatos.collect { candidato ->
            [
                    id          : candidato.id,
                    nome        : candidato.nome,
                    email       : candidato.email,
                    cpf         : candidato.cpf,
                    idade       : candidato.idade,
                    estado      : candidato.estado,
                    cep         : candidato.cep,
                    descricao   : candidato.descricao,
                    competencias: candidato.competencias
            ]
        }

        responder(exchange, 200, resposta)
    }

    private void responder(HttpExchange exchange, int status, Object corpo) {
        String json = new groovy.json.JsonOutput().toJson(corpo)
        byte[] bytes = json.getBytes("UTF-8")

        exchange.responseHeaders.set("Content-Type", "application/json; charset=UTF-8")
        exchange.sendResponseHeaders(status, bytes.length)

        exchange.responseBody.withStream { it.write(bytes) }
    }
}