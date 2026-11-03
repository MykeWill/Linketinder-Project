package com.linketinder.controller

import com.linketinder.model.Candidato
import com.linketinder.model.Match
import com.linketinder.model.Recrutador
import com.linketinder.service.RecrutadorService

class RecrutadorController {

    private final RecrutadorService recrutadorService

    RecrutadorController(RecrutadorService recrutadorService) {
        this.recrutadorService = recrutadorService
    }

    Integer cadastrarRecrutadorController(Recrutador recrutador, String senhaPura) {
        return recrutadorService.cadastrarRecrutador(recrutador, senhaPura)
    }

    void removerRecrutadorController(Integer id) {
        recrutadorService.removerRecrutador(id)
    }

    Recrutador buscarRecrutadorDaEmpresaController(Integer recrutadorId, Integer empresaId) {
        return recrutadorService.buscarRecrutadorDaEmpresa(recrutadorId, empresaId)
    }

    void atualizarRecrutadorController(Recrutador recrutador, String senhaPura, Integer empresaId) {
        recrutadorService.atualizarRecrutador(recrutador, senhaPura, empresaId)
    }

    List<Recrutador> listarRecrutadoresPorEmpresaController(Integer empresaId) {
        return recrutadorService.listarRecrutadoresPorEmpresa(empresaId)
    }

    List<Candidato> listarCandidatosDisponiveisController(Integer recrutadorId) {
        return recrutadorService.listarCandidatosDisponiveis(recrutadorId)
    }

    boolean curtirCandidatoController(Integer recrutadorId, Integer candidatoId, Integer vagaId) {
        return recrutadorService.curtirCandidato(recrutadorId, candidatoId, vagaId)
    }

    List<Match> listarMatchesDoRecrutadorController(Integer recrutadorId) {
        return recrutadorService.listarMatchesDoRecrutador(recrutadorId)
    }
}
