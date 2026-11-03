package com.linketinder.server.handler

import com.linketinder.controller.EmpresaController
import com.linketinder.model.Empresa
import com.linketinder.server.SessaoHttp
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
            String path = exchange.requestURI.path
            String method = exchange.requestMethod
            if (path == '/empresas' && method == 'POST') {
                tratarPost(exchange)
            } else if (path == '/empresas' && method == 'GET') {
                tratarGet(exchange)
            } else {
                def perfil = path =~ /^\/empresas\/(\d+)$/
                if (!perfil.matches()) {
                    responder(exchange, 404, [erro: 'Rota não encontrada'])
                } else if (method == 'GET') {
                    tratarPerfil(exchange, inteiroPositivo(perfil[0][1]))
                } else if (method == 'PUT') {
                    tratarAtualizacao(exchange, inteiroPositivo(perfil[0][1]))
                } else {
                    responder(exchange, 405, [erro: 'Método não permitido'])
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

    private void tratarPerfil(HttpExchange exchange, Integer empresaId) {
        Map sessao = SessaoHttp.obter(exchange)
        if (sessao == null) {
            responder(exchange, 401, [erro: 'Sessão inválida ou expirada.'])
            return
        }
        if (sessao.id != empresaId || sessao.tipo != 'EMPRESA') {
            responder(exchange, 403, [erro: 'Acesso não autorizado.'])
            return
        }
        Empresa empresa = empresaController.buscarEmpresaPorIdController(empresaId)
        if (empresa == null) {
            responder(exchange, 404, [erro: 'Empresa não encontrada.'])
            return
        }
        responder(exchange, 200, [
                id: empresa.id,
                nome: empresa.nome,
                email: empresa.email,
                cnpj: empresa.cnpj,
                pais: empresa.pais,
                estado: empresa.estado,
                cep: empresa.cep,
                descricao: empresa.descricao
        ])
    }

    private void tratarAtualizacao(HttpExchange exchange, Integer empresaId) {
        Map sessao = SessaoHttp.obter(exchange)
        if (sessao == null) {
            responder(exchange, 401, [erro: 'Sessão inválida ou expirada.'])
            return
        }
        if (sessao.id != empresaId || sessao.tipo != 'EMPRESA') {
            responder(exchange, 403, [erro: 'Acesso não autorizado.'])
            return
        }
        def json = new JsonSlurper().parseText(exchange.requestBody.text)
        Empresa empresa = new Empresa(
                json.nome as String,
                json.email as String,
                json.cnpj as String,
                json.pais as String,
                json.estado as String,
                json.cep as String,
                json.descricao as String,
                json.senha as String
        )
        empresa.id = empresaId
        empresaController.atualizarEmpresaController(empresa)
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