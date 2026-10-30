package com.linketinder.server.handler

import com.linketinder.controller.RecrutadorController
import com.linketinder.exception.DadosInvalidosException
import com.linketinder.exception.ErroBancoException
import com.linketinder.exception.RegistroDuplicadoException
import com.linketinder.exception.RegistroNaoEncontradoException
import com.linketinder.model.Candidato
import com.linketinder.model.Match
import com.linketinder.model.Recrutador
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.json.JsonException
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.time.DateTimeException
import java.time.LocalDate

class RecrutadorHandler implements HttpHandler {

    private final RecrutadorController recrutadorController

    RecrutadorHandler(RecrutadorController recrutadorController) {
        this.recrutadorController = recrutadorController
    }

    @Override
    void handle(HttpExchange exchange) {
        try {
            despachar(exchange)
        } catch (JsonException | IllegalArgumentException | DateTimeException e) {
            responder(exchange, 400, [erro: 'Dados inválidos. Verifique a solicitação.'])
        } catch (DadosInvalidosException e) {
            responder(exchange, 400, [erro: e.message])
        } catch (RegistroDuplicadoException e) {
            responder(exchange, 409, [erro: e.message])
        } catch (RegistroNaoEncontradoException e) {
            responder(exchange, 404, [erro: e.message])
        } catch (ErroBancoException e) {
            responder(exchange, 500, [erro: e.message])
        } catch (Exception e) {
            responder(exchange, 500, [erro: 'Erro interno ao processar a solicitação.'])
        } finally {
            exchange.close()
        }
    }

    private void despachar(HttpExchange exchange) {
        String path = exchange.requestURI.path
        String method = exchange.requestMethod

        if (path == '/recrutadores') {
            if (method != 'POST') {
                responder(exchange, 405, [erro: 'Método não permitido'])
                return
            }
            tratarPost(exchange)
            return
        }

        def candidatos = path =~ /^\/recrutadores\/(\d+)\/candidatos$/
        if (candidatos.matches()) {
            if (method != 'GET') {
                responder(exchange, 405, [erro: 'Método não permitido'])
                return
            }
            tratarCandidatos(exchange, idDaRota(candidatos[0][1]))
            return
        }

        def curtidas = path =~ /^\/recrutadores\/(\d+)\/curtidas$/
        if (curtidas.matches()) {
            if (method != 'POST') {
                responder(exchange, 405, [erro: 'Método não permitido'])
                return
            }
            tratarCurtida(exchange, idDaRota(curtidas[0][1]))
            return
        }

        def matches = path =~ /^\/recrutadores\/(\d+)\/matches$/
        if (matches.matches()) {
            if (method != 'GET') {
                responder(exchange, 405, [erro: 'Método não permitido'])
                return
            }
            tratarMatches(exchange, idDaRota(matches[0][1]))
            return
        }

        def recrutador = path =~ /^\/recrutadores\/(\d+)$/
        if (recrutador.matches()) {
            if (method != 'DELETE') {
                responder(exchange, 405, [erro: 'Método não permitido'])
                return
            }
            recrutadorController.removerRecrutadorController(idDaRota(recrutador[0][1]))
            responder(exchange, 204, null)
            return
        }

        responder(exchange, 404, [erro: 'Rota não encontrada'])
    }

    private void tratarPost(HttpExchange exchange) {
        Map json = parsearObjeto(exchange)
        if (!(json.dataNascimento instanceof String)) {
            throw new IllegalArgumentException('Data de nascimento inválida.')
        }

        Recrutador recrutador = new Recrutador(
                inteiroPositivo(json.empresaId, 'empresaId'),
                json.nome as String,
                json.areaRecrutamento as String,
                json.descricao as String,
                json.email as String,
                json.cpf as String,
                LocalDate.parse(json.dataNascimento as String),
                null
        )

        Integer id = recrutadorController.cadastrarRecrutadorController(recrutador, json.senha as String)
        responder(exchange, 201, [id: id])
    }

    private void tratarCandidatos(HttpExchange exchange, Integer recrutadorId) {
        List<Candidato> candidatos =
                recrutadorController.listarCandidatosDisponiveisController(recrutadorId)
        responder(exchange, 200, candidatos.collect { Candidato candidato ->
            [
                    id          : candidato.id,
                    descricao   : candidato.descricao,
                    competencias: candidato.competencias
            ]
        })
    }

    private void tratarCurtida(HttpExchange exchange, Integer recrutadorId) {
        Map json = parsearObjeto(exchange)
        recrutadorController.curtirCandidatoController(
                recrutadorId,
                inteiroPositivo(json.candidatoId, 'candidatoId')
        )
        responder(exchange, 201, [sucesso: true])
    }

    private void tratarMatches(HttpExchange exchange, Integer recrutadorId) {
        List<Match> matches = recrutadorController.listarMatchesDoRecrutadorController(recrutadorId)
        responder(exchange, 200, matches.collect { Match match ->
            [
                    empresaNome                 : match.empresaNome,
                    empresaDescricao            : match.empresaDescricao,
                    empresaEmail                 : match.empresaEmail,
                    recrutadorNome               : match.recrutadorNome,
                    recrutadorAreaRecrutamento   : match.recrutadorAreaRecrutamento,
                    recrutadorEmail              : match.recrutadorEmail,
                    candidatoNome                : match.candidatoNome,
                    candidatoIdade               : match.candidatoIdade,
                    candidatoEmail               : match.candidatoEmail,
                    candidatoCompetencias        : match.candidatoCompetencias,
                    candidatoDescricao           : match.candidatoDescricao,
                    candidatoEstado              : match.candidatoEstado
            ]
        })
    }

    private static Map parsearObjeto(HttpExchange exchange) {
        def json = new JsonSlurper().parseText(exchange.requestBody.getText('UTF-8'))
        if (!(json instanceof Map)) {
            throw new IllegalArgumentException('O corpo deve ser um objeto JSON.')
        }
        return json as Map
    }

    private static Integer inteiroPositivo(Object valor, String campo) {
        if (!(valor instanceof Number)) {
            throw new IllegalArgumentException("${campo} inválido.")
        }
        int inteiro = new BigDecimal(valor.toString()).intValueExact()
        if (inteiro <= 0) {
            throw new IllegalArgumentException("${campo} inválido.")
        }
        return inteiro
    }

    private static Integer idDaRota(String valor) {
        return inteiroPositivo(new BigDecimal(valor), 'id')
    }

    private static void responder(HttpExchange exchange, int status, Object corpo) {
        if (status == 204) {
            exchange.sendResponseHeaders(status, -1)
            return
        }

        byte[] bytes = JsonOutput.toJson(corpo).getBytes('UTF-8')
        exchange.responseHeaders.set('Content-Type', 'application/json; charset=UTF-8')
        exchange.sendResponseHeaders(status, bytes.length)
        exchange.responseBody.withStream { it.write(bytes) }
    }
}
