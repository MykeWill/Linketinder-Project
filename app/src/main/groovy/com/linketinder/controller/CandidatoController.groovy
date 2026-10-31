package com.linketinder.controller

import com.linketinder.model.Candidato
import com.linketinder.service.CandidatoService

class CandidatoController {

    private final CandidatoService candidatoService

    CandidatoController(CandidatoService candidatoService) {
        this.candidatoService = candidatoService
    }

    Integer cadastrarCandidatoController(Candidato candidato) {
        return candidatoService.cadastrarCandidato(candidato)
    }

    List<Candidato> listarTodosCandidatosController() {
        return candidatoService.listarTodosCandidatosService()
    }

    List<Map> listarCandidatosAnonimosController() {
        return candidatoService.listarCandidatosAnonimosService()
    }

    List<Map> listarMatchesDoCandidatoController(Integer candidatoId) {
        return candidatoService.listarMatchesDoCandidatoService(candidatoId)
    }

    void atualizarCandidatoController(Candidato candidato) {
        candidatoService.atualizarCandidatoService(candidato)
    }

    void removerCandidatoController(Integer id) {
        candidatoService.removerCandidatoService(id)
    }
}