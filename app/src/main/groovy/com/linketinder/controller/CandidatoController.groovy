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

    Map<String, Integer> contarCandidatosPorCompetenciaController() {
        return candidatoService.contarCandidatosPorCompetenciaService()
    }

    List<Map> listarMatchesDoCandidatoController(Integer candidatoId) {
        return candidatoService.listarMatchesDoCandidatoService(candidatoId)
    }

    List<Map> listarCurtidasDoCandidatoController(Integer candidatoId) {
        return candidatoService.listarCurtidasDoCandidatoService(candidatoId)
    }

    boolean curtirVagaController(Integer candidatoId, Integer vagaId) {
        return candidatoService.curtirVaga(candidatoId, vagaId)
    }

    void atualizarCandidatoController(Candidato candidato) {
        candidatoService.atualizarCandidatoService(candidato)
    }

    Candidato buscarCandidatoPorIdController(Integer id) {
        return candidatoService.buscarCandidatoPorIdService(id)
    }

    void removerCandidatoController(Integer id) {
        candidatoService.removerCandidatoService(id)
    }
}