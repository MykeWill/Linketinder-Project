package com.linketinder.server

import com.linketinder.controller.CandidatoController
import com.linketinder.controller.EmpresaController
import com.linketinder.controller.RecrutadorController
import com.linketinder.controller.VagaController
import com.linketinder.server.handler.CandidatoHandler
import com.linketinder.server.handler.EmpresaHandler
import com.linketinder.server.handler.RecrutadorHandler
import com.linketinder.server.handler.VagaHandler
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer

class ServidorHttp {

    private static final int PORTA = 8080

    private final CandidatoController candidatoController
    private final EmpresaController empresaController
    private final VagaController vagaController
    private final RecrutadorController recrutadorController

    ServidorHttp(CandidatoController candidatoController, EmpresaController empresaController, VagaController vagaController,
                 RecrutadorController recrutadorController) {
        this.candidatoController = candidatoController
        this.empresaController = empresaController
        this.vagaController = vagaController
        this.recrutadorController = recrutadorController
    }

    void iniciar() {
        def servidor = HttpServer.create(new InetSocketAddress(PORTA), 0)

        registrarContexto(servidor, "/candidatos", new CandidatoHandler(candidatoController))
        registrarContexto(servidor, "/empresas", new EmpresaHandler(empresaController))
        registrarContexto(servidor, "/vagas", new VagaHandler(vagaController))
        registrarContexto(servidor, "/recrutadores", new RecrutadorHandler(recrutadorController))

        servidor.start()
        println "Servidor HTTP rodando na porta ${PORTA}"
    }

    private static void registrarContexto(HttpServer servidor, String caminho, HttpHandler handler) {
        servidor.createContext(caminho, handler).filters.add(new CorsFilter())
    }
}