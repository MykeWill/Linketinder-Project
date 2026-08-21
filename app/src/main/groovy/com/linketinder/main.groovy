package com.linketinder

// Autor: Myke William Silva

import com.linketinder.menu.MenuPrincipal
import com.linketinder.repository.CandidatoRepository
import com.linketinder.repository.EmpresaRepository
import com.linketinder.service.CandidatoService
import com.linketinder.service.EmpresaService

 class Main {
    static void main(String[] args) {
        def candidatoRepository = new CandidatoRepository()
        def empresaRepository = new EmpresaRepository()

        def candidatoService = new CandidatoService(candidatoRepository)
        def empresaService = new EmpresaService(empresaRepository)

        def menu = new MenuPrincipal(candidatoService, empresaService)
        menu.iniciar()
    }
 }
