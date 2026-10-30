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

    List<Candidato> listarCandidatosDisponiveisController(Integer recrutadorId) {
        return recrutadorService.listarCandidatosDisponiveis(recrutadorId)
    }

    void curtirCandidatoController(Integer recrutadorId, Integer candidatoId) {
        recrutadorService.curtirCandidato(recrutadorId, candidatoId)
    }

    List<Match> listarMatchesDoRecrutadorController(Integer recrutadorId) {
        return recrutadorService.listarMatchesDoRecrutador(recrutadorId)
    }
}
