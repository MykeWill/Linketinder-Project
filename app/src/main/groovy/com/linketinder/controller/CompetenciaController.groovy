package com.linketinder.controller

import com.linketinder.model.Competencia
import com.linketinder.service.CompetenciaService

class CompetenciaController {

    private final CompetenciaService competenciaService

    CompetenciaController(CompetenciaService competenciaService) {
        this.competenciaService = competenciaService
    }

    Integer buscarOuCriarCompetenciaController(String nome) {
        return competenciaService.buscarOuCriarCompetenciaService(nome)
    }

    List<Competencia> listarTodasCompetenciasController() {
        return competenciaService.listarTodasCompetenciasService()
    }
}