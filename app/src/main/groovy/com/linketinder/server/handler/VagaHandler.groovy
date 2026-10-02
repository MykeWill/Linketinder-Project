package com.linketinder.server.handler

import com.linketinder.controller.VagaController
import com.linketinder.model.Vaga
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.json.JsonSlurper

class VagaHandler implements HttpHandler {

    private final VagaController vagaController

    VagaHandler(VagaController vagaController) {
        this.vagaController = vagaController
    }

    @Override
    void handle(HttpExchange exchange) {
        try {
            switch (exchange.requestMethod) {
                case "POST":
                    tratarPost(exchange)
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

        Vaga vaga = new Vaga(
                json.empresaId as Integer,
                json.nome,
                json.descricao,
                json.local,
                json.competencias ?: []
        )

        Integer id = vagaController.cadastrarVagaController(vaga)

        responder(exchange, 201, [id: id])
    }

    private void responder(HttpExchange exchange, int status, Map corpo) {
        String json = new groovy.json.JsonOutput().toJson(corpo)
        byte[] bytes = json.getBytes("UTF-8")

        exchange.responseHeaders.set("Content-Type", "application/json; charset=UTF-8")
        exchange.sendResponseHeaders(status, bytes.length)

        exchange.responseBody.withStream { it.write(bytes) }
    }
}