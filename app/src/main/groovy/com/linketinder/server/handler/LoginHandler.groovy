package com.linketinder.server.handler

import com.linketinder.controller.LoginController
import com.linketinder.exception.DadosInvalidosException
import com.linketinder.exception.ErroBancoException
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.json.JsonException
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class LoginHandler implements HttpHandler {

    private final LoginController loginController

    LoginHandler(LoginController loginController) {
        this.loginController = loginController
    }

    @Override
    void handle(HttpExchange exchange) {
        try {
            if (exchange.requestURI.path != '/login') {
                responder(exchange, 404, [erro: 'Rota não encontrada'])
            } else if (exchange.requestMethod != 'POST') {
                responder(exchange, 405, [erro: 'Método não permitido'])
            } else {
                tratarPost(exchange)
            }
        } catch (JsonException | IllegalArgumentException e) {
            responder(exchange, 400, [erro: 'Dados inválidos. Verifique a solicitação.'])
        } catch (DadosInvalidosException e) {
            responder(exchange, 401, [erro: e.message])
        } catch (ErroBancoException e) {
            responder(exchange, 500, [erro: e.message])
        } catch (Exception e) {
            responder(exchange, 500, [erro: 'Erro interno ao processar a solicitação.'])
        } finally {
            exchange.close()
        }
    }

    private void tratarPost(HttpExchange exchange) {
        def dados = new JsonSlurper().parseText(exchange.requestBody.text)
        if (!(dados instanceof Map)) {
            throw new IllegalArgumentException('Dados inválidos.')
        }

        Map sessao = loginController.autenticar(
                dados.email instanceof String ? dados.email : null,
                dados.senha instanceof String ? dados.senha : null,
                dados.tipo instanceof String ? dados.tipo : null
        )
        responder(exchange, 200, sessao)
    }

    private void responder(HttpExchange exchange, int status, Object corpo) {
        byte[] bytes = JsonOutput.toJson(corpo).getBytes('UTF-8')
        exchange.responseHeaders.set('Content-Type', 'application/json; charset=UTF-8')
        exchange.sendResponseHeaders(status, bytes.length)
        exchange.responseBody.withStream { it.write(bytes) }
    }
}
