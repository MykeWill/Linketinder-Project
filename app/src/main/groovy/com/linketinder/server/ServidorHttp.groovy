package com.linketinder.server

import com.linketinder.controller.CandidatoController
import com.linketinder.controller.EmpresaController
import com.linketinder.controller.VagaController
import com.linketinder.server.handler.CandidatoHandler
import com.linketinder.server.handler.EmpresaHandler
import com.linketinder.server.handler.VagaHandler
import com.sun.net.httpserver.HttpServer

class ServidorHttp {

    private static final int PORTA = 8080

    private final CandidatoController candidatoController
    private final EmpresaController empresaController
    private final VagaController vagaController

    ServidorHttp(CandidatoController candidatoController, EmpresaController empresaController, VagaController vagaController) {
        this.candidatoController = candidatoController
        this.empresaController = empresaController
        this.vagaController = vagaController
    }

    void iniciar() {
        def servidor = HttpServer.create(new InetSocketAddress(PORTA), 0)

        servidor.createContext("/candidatos", new CandidatoHandler(candidatoController))
        servidor.createContext("/empresas", new EmpresaHandler(empresaController))
        servidor.createContext("/vagas", new VagaHandler(vagaController))

        servidor.start()
        println "Servidor HTTP rodando na porta ${PORTA}"
    }
}