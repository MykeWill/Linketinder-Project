package com.linketinder.server.handler

import com.linketinder.controller.EmpresaController
import com.linketinder.model.Empresa
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.json.JsonSlurper

class EmpresaHandler implements HttpHandler {

    private final EmpresaController empresaController

    EmpresaHandler(EmpresaController empresaController) {
        this.empresaController = empresaController
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

        Empresa empresa = new Empresa(
                json.nome,
                json.email,
                json.cnpj,
                json.pais,
                json.estado,
                json.cep,
                json.descricao,
                json.senha
        )

        Integer id = empresaController.cadastrarEmpresaController(empresa)

        responder(exchange, 201, [id: id])
    }

    private void tratarGet(HttpExchange exchange) {
        def empresas = empresaController.listarTodasEmpresasController()

        def resposta = empresas.collect { empresa ->
            [
                    id       : empresa.id,
                    nome     : empresa.nome,
                    email    : empresa.email,
                    cnpj     : empresa.cnpj,
                    pais     : empresa.pais,
                    estado   : empresa.estado,
                    cep      : empresa.cep,
                    descricao: empresa.descricao
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