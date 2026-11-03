package com.linketinder.server.handler

import com.linketinder.controller.VagaController
import com.linketinder.exception.ErroBancoException
import com.linketinder.model.Vaga
import com.linketinder.server.SessaoHttp
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
            if (exchange.requestMethod == 'PUT') {
                tratarPut(exchange)
                return
            }
            switch (exchange.requestMethod) {
                case "POST":
                    tratarPost(exchange)
                    break
                case "GET":
                    tratarGet(exchange)
                    break
                case "DELETE":
                    tratarDelete(exchange)
                    break
                default:
                    responder(exchange, 405, [erro: "Método não permitido"])
            }
        } catch (ErroBancoException e) {
            responder(exchange, 500, [erro: e.message])
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
            Integer candidatoId = inteiroPositivo(parametroUnico(exchange, 'candidatoId'), 'candidatoId')
            responder(exchange, 200, vagaController.listarVagasNaoCurtidasPorCandidatoController(candidatoId)
                    .collect { Vaga vaga -> mapearVagaAnonima(vaga, true) })
            return
        }

        List<String> parametros = exchange.requestURI.rawQuery
                ? exchange.requestURI.rawQuery.split('&').collect { parametro ->
                    String[] partes = parametro.split('=', 2)
                    URLDecoder.decode(partes[0], StandardCharsets.UTF_8)
                }
                : []
        def vagas
        if (parametros.contains('empresaId')) {
            Integer empresaId = inteiroPositivo(parametroUnico(exchange, 'empresaId'), 'empresaId')
            Map sessao = SessaoHttp.obter(exchange)
            if (sessao == null) {
                responder(exchange, 401, [erro: 'Sessão inválida ou expirada.'])
                return
            }
            boolean acessoPermitido = sessao.tipo == 'EMPRESA'
                    ? sessao.id == empresaId
                    : sessao.tipo == 'RECRUTADOR' && sessao.empresaId == empresaId
            if (!acessoPermitido) {
                responder(exchange, 403, [erro: 'Acesso não autorizado.'])
                return
            }
            vagas = vagaController.listarVagasVisiveisParaRecrutadorController(empresaId)
        } else {
            vagas = vagaController.listarTodasVagasController()
        }

        def resposta = vagas.collect { vaga ->
            if (!parametros.contains('empresaId')) {
                return mapearVagaAnonima(vaga)
            }
            [
                    id          : vaga.id,
                    empresaId   : vaga.empresaId,
                    recrutadorId: vaga.recrutadorId,
                    nome        : vaga.nome,
                    descricao   : vaga.descricao,
                    local       : vaga.local,
                    competencias: vaga.competencias
            ]
        }

        responder(exchange, 200, resposta)
    }

    private void tratarDelete(HttpExchange exchange) {
        def match = exchange.requestURI.path =~ /^\/vagas\/(\d+)$/
        if (!match.matches()) {
            responder(exchange, 404, [erro: 'Vaga não encontrada.'])
            return
        }

        Integer id = inteiroPositivo(match.group(1), 'id')
        vagaController.removerVagaController(id)
        responder(exchange, 200, [mensagem: 'Vaga removida.'])
    }

    private void tratarPut(HttpExchange exchange) {
        def match = exchange.requestURI.path =~ /^\/vagas\/(\d+)$/
        if (!match.matches()) {
            responder(exchange, 404, [erro: 'Vaga não encontrada.'])
            return
        }

        Integer id = inteiroPositivo(match.group(1), 'id')
        Map sessao = SessaoHttp.obter(exchange)
        if (sessao == null) {
            responder(exchange, 401, [erro: 'Sessão inválida ou expirada.'])
            return
        }
        def json = new JsonSlurper().parseText(exchange.requestBody.text)
        Vaga vaga = new Vaga(
                null,
                json.nome as String,
                json.descricao as String,
                json.local as String ?: '',
                json.competencias instanceof List ? json.competencias.collect { it as String } : []
        )
        vaga.id = id
        if (!(sessao.tipo in ['EMPRESA', 'RECRUTADOR']) ||
                !vagaController.atualizarVagaController(vaga, sessao.tipo as String, sessao.id as Integer)) {
            responder(exchange, 403, [erro: 'Você não tem permissão para editar esta vaga.'])
            return
        }
        responder(exchange, 200, [mensagem: 'Vaga atualizada.'])
    }

    private static Map mapearVagaAnonima(Vaga vaga, boolean incluirAfinidade = false) {
        Map resposta = [
                id          : vaga.id,
                nome        : vaga.nome,
                descricao   : vaga.descricao,
                competencias: vaga.competencias
        ]
        if (incluirAfinidade) {
            resposta.afinidade = vaga.afinidade
        }
        return resposta
    }

    private static String parametroUnico(HttpExchange exchange, String nome) {
        String query = exchange.requestURI.rawQuery
        List<String> valores = query
                ? query
                .split('&')
                .findAll { parametro ->
                    String[] partes = parametro.split('=', 2)
                    URLDecoder.decode(partes[0], StandardCharsets.UTF_8) == nome
                }
                .collect { parametro ->
                    String[] partes = parametro.split('=', 2)
                    URLDecoder.decode(partes.length == 2 ? partes[1] : '', StandardCharsets.UTF_8)
                }
                : []

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