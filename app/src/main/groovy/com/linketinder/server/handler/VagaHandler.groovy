package com.linketinder.server.handler

import com.linketinder.controller.VagaController
import com.linketinder.model.Vaga
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.json.JsonSlurper

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class VagaHandler implements HttpHandler {

    private final VagaController vagaController

    VagaHandler(VagaController vagaController) {
        this.vagaController = vagaController
    }

    @Override
    void handle(HttpExchange exchange) {
        try {
            if (exchange.requestURI.path == '/vagas/anonimas' && exchange.requestMethod != 'GET') {
                responder(exchange, 405, [erro: "Método não permitido"])
                return
            }
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

        Vaga vaga = new Vaga(
                json.empresaId as Integer,
                json.nome,
                json.descricao,
                json.local,
                json.competencias ?: [],
                json.recrutadorId == null ? null : json.recrutadorId as Integer
        )

        Integer id = vagaController.cadastrarVagaController(vaga)

        responder(exchange, 201, [id: id])
    }

    private void tratarGet(HttpExchange exchange) {
        String path = exchange.requestURI.path
        if (path == '/vagas/anonimas') {
            responder(exchange, 200, vagaController.listarTodasVagasController()
                    .collect { Vaga vaga -> mapearVagaAnonima(vaga) })
            return
        }

        List<String> parametros = exchange.requestURI.rawQuery
                ? exchange.requestURI.rawQuery.split('&').collect { parametro ->
                    String[] partes = parametro.split('=', 2)
                    URLDecoder.decode(partes[0], StandardCharsets.UTF_8)
                }
                : []
        def vagas
        if (parametros.contains('empresaId') || parametros.contains('recrutadorId')) {
            Integer empresaId = inteiroPositivo(parametroUnico(exchange, 'empresaId'), 'empresaId')
            Integer recrutadorId = inteiroPositivo(parametroUnico(exchange, 'recrutadorId'), 'recrutadorId')
            vagas = vagaController.listarVagasVisiveisParaRecrutadorController(empresaId, recrutadorId)
        } else {
            vagas = vagaController.listarTodasVagasController()
        }

        def resposta = vagas.collect { vaga ->
            if (!parametros.contains('empresaId') && !parametros.contains('recrutadorId')) {
                return mapearVagaAnonima(vaga)
            }
            [
                    id          : vaga.id,
                    empresaId   : vaga.empresaId,
                    nome        : vaga.nome,
                    descricao   : vaga.descricao,
                    local       : vaga.local,
                    competencias: vaga.competencias
            ]
        }

        responder(exchange, 200, resposta)
    }

    private static Map mapearVagaAnonima(Vaga vaga) {
        [
                id          : vaga.id,
                nome        : vaga.nome,
                descricao   : vaga.descricao,
                competencias: vaga.competencias
        ]
    }

    private static String parametroUnico(HttpExchange exchange, String nome) {
        List<String> valores = exchange.requestURI.rawQuery
                .split('&')
                .findAll { parametro ->
                    String[] partes = parametro.split('=', 2)
                    URLDecoder.decode(partes[0], StandardCharsets.UTF_8) == nome
                }
                .collect { parametro ->
                    String[] partes = parametro.split('=', 2)
                    URLDecoder.decode(partes.length == 2 ? partes[1] : '', StandardCharsets.UTF_8)
                }

        if (valores.size() != 1) {
            throw new IllegalArgumentException("${nome} inválido.")
        }
        return valores[0]
    }

    private static Integer inteiroPositivo(String valor, String campo) {
        int inteiro = new BigDecimal(valor).intValueExact()
        if (inteiro <= 0) {
            throw new IllegalArgumentException("${campo} inválido.")
        }
        return inteiro
    }

    private void responder(HttpExchange exchange, int status, Object corpo) {
        String json = new groovy.json.JsonOutput().toJson(corpo)
        byte[] bytes = json.getBytes("UTF-8")

        exchange.responseHeaders.set("Content-Type", "application/json; charset=UTF-8")
        exchange.sendResponseHeaders(status, bytes.length)

        exchange.responseBody.withStream { it.write(bytes) }
    }
}