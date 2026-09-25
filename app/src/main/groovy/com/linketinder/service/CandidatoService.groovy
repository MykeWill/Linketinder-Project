package com.linketinder.service

import com.linketinder.dao.CandidatoDao
import com.linketinder.dao.CompetenciaDao
import com.linketinder.model.Candidato

class CandidatoService {

    private CandidatoDao candidatoDao = new CandidatoDao()
    private CompetenciaDao competenciaDao = new CompetenciaDao()

    Integer cadastrarCandidato(Candidato c) {
        Integer candidatoId = candidatoDao.inserirCandidato(c)

        c.competencias.each { nomeCompetencia ->
            Integer competenciaId = competenciaDao.buscarOuCriar(nomeCompetencia)
            candidatoDao.vincularCompetenciaAoCandidato(candidatoId, competenciaId)
        }

        return candidatoId
    }

    List<Map> listarCandidatosAnonimosService() {
        return candidatoDao.listarCandidatosAnonimos()
    }

    List<Candidato> listarTodosCandidatosService() {
        return candidatoDao.listarTodosCandidatos()
    }

    void atualizarCandidatoService(Candidato c) {
        candidatoDao.atualizarCandidato(c)
    }

    void removerCandidatoService(Integer id) {
        candidatoDao.removerCandidato(id)
    }
}