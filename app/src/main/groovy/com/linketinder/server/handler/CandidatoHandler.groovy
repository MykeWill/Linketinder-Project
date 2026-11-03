package com.linketinder.server.handler

import com.linketinder.controller.CandidatoController
import com.linketinder.model.Candidato
import com.linketinder.server.SessaoHttp
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
            } else if (path == '/candidatos/competencias' && method == 'GET') {
                responder(exchange, 200, candidatoController.contarCandidatosPorCompetenciaController())
            } else {
                def perfil = path =~ /^\/candidatos\/(\d+)$/
                def curtida = path =~ /^\/candidatos\/(\d+)\/curtir-vaga$/
                def curtidas = path =~ /^\/candidatos\/(\d+)\/curtidas$/
                def matches = path =~ /^\/candidatos\/(\d+)\/matches$/
                if (perfil.matches()) {
                    Integer id = inteiroPositivo(perfil[0][1])
                    if (method == 'GET') {
                        tratarPerfil(exchange, id)
                    } else if (method == 'PUT') {
                        tratarAtualizacao(exchange, id)
                    } else {
                        responder(exchange, 405, [erro: 'Método não permitido'])
                    }
                } else if (curtida.matches()) {
                    if (method != 'POST') {
                        responder(exchange, 405, [erro: 'Método não permitido'])
                    } else {
                        tratarCurtidaVaga(exchange, inteiroPositivo(curtida[0][1]))
                    }
                } else if (curtidas.matches()) {
                    if (method != 'GET') {
                        responder(exchange, 405, [erro: 'Método não permitido'])
                    } else {
                        tratarCurtidas(exchange, inteiroPositivo(curtidas[0][1]))
                    }
                } else if (!matches.matches()) {
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
                    vagaId                    : match.vagaId,
                    vagaNome                  : match.vagaNome,
                    vagaDescricao             : match.vagaDescricao,
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

    private void tratarCurtidas(HttpExchange exchange, Integer candidatoId) {
        List<Map> curtidas = candidatoController.listarCurtidasDoCandidatoController(candidatoId)
        responder(exchange, 200, curtidas.collect { Map curtida ->
            [
                    id          : curtida.id,
                    nome        : curtida.nome,
                    descricao   : curtida.descricao,
                    competencias: curtida.competencias
            ]
        })
    }

    private void tratarCurtidaVaga(HttpExchange exchange, Integer candidatoId) {
        def json = new JsonSlurper().parseText(exchange.requestBody.text)
        Integer vagaId = inteiroPositivo(json.vagaId?.toString())
        boolean match = candidatoController.curtirVagaController(candidatoId, vagaId)
        responder(exchange, 200, [match: match])
    }

    private void tratarPerfil(HttpExchange exchange, Integer candidatoId) {
        Map sessao = SessaoHttp.obter(exchange)
        if (sessao == null) {
            responder(exchange, 401, [erro: 'Sessão inválida ou expirada.'])
            return
        }
        if (sessao.id != candidatoId || sessao.tipo != 'CANDIDATO') {
            responder(exchange, 403, [erro: 'Acesso não autorizado.'])
            return
        }

        Candidato candidato = candidatoController.buscarCandidatoPorIdController(candidatoId)
        if (candidato == null) {
            responder(exchange, 404, [erro: 'Candidato não encontrado.'])
            return
        }
        responder(exchange, 200, [
                id: candidato.id,
                nome: candidato.nome,
                email: candidato.email,
                cpf: candidato.cpf,
                idade: candidato.idade,
                estado: candidato.estado,
                cep: candidato.cep,
                descricao: candidato.descricao,
                competencias: candidato.competencias
        ])
    }

    private void tratarAtualizacao(HttpExchange exchange, Integer candidatoId) {
        Map sessao = SessaoHttp.obter(exchange)
        if (sessao == null) {
            responder(exchange, 401, [erro: 'Sessão inválida ou expirada.'])
            return
        }
        if (sessao.id != candidatoId || sessao.tipo != 'CANDIDATO') {
            responder(exchange, 403, [erro: 'Acesso não autorizado.'])
            return
        }
        def json = new JsonSlurper().parseText(exchange.requestBody.text)
        Candidato candidato = new Candidato(
                json.nome as String,
                json.email as String,
                json.cpf as String,
                json.idade as Integer,
                json.estado as String,
                json.cep as String,
                json.descricao as String,
                json.senha as String,
                json.competencias instanceof List ? json.competencias.collect { it as String } : []
        )
        candidato.id = candidatoId
        candidatoController.atualizarCandidatoController(candidato)
        responder(exchange, 200, [mensagem: 'Perfil atualizado.'])
    }

    private static Integer inteiroPositivo(String valor) {
        int id = new BigDecimal(valor).intValueExact()
        if (id <= 0) {
            throw new IllegalArgumentException('ID inválido.')
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