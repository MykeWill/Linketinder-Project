package com.linketinder

// Autor: Myke William Silva

import com.linketinder.dao.CandidatoDao
import com.linketinder.dao.CompetenciaDao
import com.linketinder.dao.EmpresaDao
import com.linketinder.dao.VagaDao
import com.linketinder.menu.MenuPrincipal
import com.linketinder.service.CandidatoService
import com.linketinder.service.EmpresaService
import com.linketinder.service.VagaService

class Main {
    static void main(String[] args) {
        def candidatoService = new CandidatoService(new CandidatoDao(), new CompetenciaDao())
        def empresaService = new EmpresaService(new EmpresaDao())
        def vagaService = new VagaService(new VagaDao(), new CompetenciaDao())

        def menu = new MenuPrincipal(candidatoService, empresaService, vagaService)
        menu.iniciar()
    }
}