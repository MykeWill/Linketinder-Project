package com.linketinder

// Autor: Myke William Silva

import com.linketinder.menu.MenuPrincipal
import com.linketinder.service.CandidatoService
import com.linketinder.service.EmpresaService
import com.linketinder.service.VagaService

class Main {
    static void main(String[] args) {
        def candidatoService = new CandidatoService()
        def empresaService = new EmpresaService()
        def vagaService = new VagaService()

        def menu = new MenuPrincipal(candidatoService, empresaService, vagaService)
        menu.iniciar()
    }
}