package com.linketinder.service

import com.linketinder.dao.CandidatoDao
import com.linketinder.dao.CompetenciaDao

class CandidatoService {

    private CandidatoDao candidatoDao = new CandidatoDao()
    private CompetenciaDao competenciaDao = new CompetenciaDao()

    Integer cadastrarCandidato(String nome, String sobrenome, String dataNasc, String email, String cpf,
                               String pais, String cep, String descricao, String senha, List<String> competencias) {

        Integer candidatoId = candidatoDao.inserirCandidato(nome, sobrenome, dataNasc, email, cpf, pais, cep, descricao, senha)

        competencias.each { nomeCompetencia ->
            Integer competenciaId = competenciaDao.buscarOuCriar(nomeCompetencia)
            candidatoDao.vincularCompetenciaAoCandidato(candidatoId, competenciaId)
        }

        return candidatoId
    }

    List<Map> listarTodosCandidatosService() {
        return candidatoDao.listarTodosCandidatos()
    }

    void atualizarCandidatoService(Integer id, String descricao) {
        candidatoDao.atualizarCandidato(id, descricao)
    }

    void removerCandidatoService(Integer id) {
        candidatoDao.removerCandidato(id)
    }
}