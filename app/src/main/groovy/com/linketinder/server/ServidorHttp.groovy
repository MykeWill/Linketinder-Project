package com.linketinder.server

import com.linketinder.controller.CandidatoController
import com.linketinder.controller.EmpresaController
import com.linketinder.controller.RecrutadorController
import com.linketinder.controller.VagaController
import com.linketinder.server.handler.CandidatoHandler
import com.linketinder.server.handler.EmpresaHandler
import com.linketinder.server.handler.RecrutadorHandler
import com.linketinder.server.handler.VagaHandler
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

        servidor.createContext("/candidatos", new CandidatoHandler(candidatoController))
        servidor.createContext("/empresas", new EmpresaHandler(empresaController))
        servidor.createContext("/vagas", new VagaHandler(vagaController))
        servidor.createContext("/recrutadores", new RecrutadorHandler(recrutadorController))

        servidor.start()
        println "Servidor HTTP rodando na porta ${PORTA}"
    }
}