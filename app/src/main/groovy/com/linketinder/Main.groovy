package com.linketinder

// Autor: Myke William Silva

import com.linketinder.controller.CandidatoController
import com.linketinder.controller.EmpresaController
import com.linketinder.controller.RecrutadorController
import com.linketinder.controller.VagaController
import com.linketinder.dao.CandidatoDao
import com.linketinder.dao.CompetenciaDao
import com.linketinder.dao.EmpresaDao
import com.linketinder.dao.RecrutadorDao
import com.linketinder.dao.VagaDao
import com.linketinder.menu.CandidatoView
import com.linketinder.menu.EmpresaView
import com.linketinder.menu.MenuPrincipal
import com.linketinder.server.ServidorHttp
import com.linketinder.service.CandidatoService
import com.linketinder.service.EmpresaService
import com.linketinder.service.RecrutadorService
import com.linketinder.service.VagaService

class Main {
    static void main(String[] args) {


        def candidatoDao = new CandidatoDao()
        def empresaDao = new EmpresaDao()
        def recrutadorDao = new RecrutadorDao()
        def vagaDao = new VagaDao()
        def competenciaDao = new CompetenciaDao()


        def candidatoService = new CandidatoService(candidatoDao, competenciaDao)
        def empresaService = new EmpresaService(empresaDao)
        def recrutadorService = new RecrutadorService(recrutadorDao)
        def vagaService = new VagaService(vagaDao, competenciaDao)


        def candidatoController = new CandidatoController(candidatoService)
        def empresaController = new EmpresaController(empresaService)
        def recrutadorController = new RecrutadorController(recrutadorService)
        def vagaController = new VagaController(vagaService)

        def servidor = new ServidorHttp(candidatoController, empresaController, vagaController, recrutadorController)
        def threadServidor = new Thread({
            try {
                servidor.iniciar()
            } catch (Exception e) {
                println "Erro no servidor HTTP: ${e.message}"
                e.printStackTrace()
            }
        })
        threadServidor.start()

        def scanner = new Scanner(System.in)
        def candidatoView = new CandidatoView(candidatoController, scanner)
        def empresaView = new EmpresaView(empresaController, scanner)
        def menu = new MenuPrincipal(candidatoView, empresaView)
        menu.iniciar()
    }
}