package com.linketinder.service

import com.linketinder.model.Candidato
import com.linketinder.repository.CandidatoRepository

class CandidatoService {

    private CandidatoRepository candidatoRepository

    CandidatoService(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository
    }

    List<Candidato> listarTodosCandidatosService() {
        candidatoRepository.listarTodosCandidatosRepository()
    }

    void cadastrarCandidatoService(Candidato candidato) {
        candidatoRepository.adicionarCandidatoRepository(candidato)
    }
}