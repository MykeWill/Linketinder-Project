package com.linketinder

// Autor: Myke William Silva

import com.linketinder.controller.CandidatoController
import com.linketinder.controller.EmpresaController
import com.linketinder.dao.CandidatoDao
import com.linketinder.dao.CompetenciaDao
import com.linketinder.dao.EmpresaDao
import com.linketinder.menu.CandidatoView
import com.linketinder.menu.EmpresaView
import com.linketinder.menu.MenuPrincipal
import com.linketinder.service.CandidatoService
import com.linketinder.service.EmpresaService

class Main {
    static void main(String[] args) {

        def candidatoDao = new CandidatoDao()
        def empresaDao = new EmpresaDao()
        def competenciaDao = new CompetenciaDao()

        def candidatoService = new CandidatoService(candidatoDao, competenciaDao)
        def empresaService = new EmpresaService(empresaDao)

        def candidatoController = new CandidatoController(candidatoService)
        def empresaController = new EmpresaController(empresaService)

        def scanner = new Scanner(System.in)
        def candidatoView = new CandidatoView(candidatoController, scanner)
        def empresaView = new EmpresaView(empresaController, scanner)

        def menu = new MenuPrincipal(candidatoView, empresaView)
        menu.iniciar()
    }
}