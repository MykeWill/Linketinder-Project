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
        boolean existeCPF = candidatoRepository.listarTodosCandidatosRepository().any{ it.cpf == candidato.cpf }
        if (existeCPF) {
            throw new IllegalArgumentException("CPF já cadastrado")
        }
        candidatoRepository.adicionarCandidatoRepository(candidato)
    }
}