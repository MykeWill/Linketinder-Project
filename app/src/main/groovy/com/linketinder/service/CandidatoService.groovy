package com.linketinder.service

import com.linketinder.dao.CandidatoDao
import com.linketinder.dao.CompetenciaDao
import com.linketinder.exception.ErroBancoException
import com.linketinder.exception.MensagensErro
import com.linketinder.exception.RegistroDuplicadoException
import com.linketinder.model.Candidato
import java.sql.SQLException

class CandidatoService {

    private final CandidatoDao candidatoDao
    private final CompetenciaDao competenciaDao

    CandidatoService(CandidatoDao candidatoDao, CompetenciaDao competenciaDao) {
        this.candidatoDao = candidatoDao
        this.competenciaDao = competenciaDao
    }

    Integer cadastrarCandidato(Candidato c) {
        try {
            Integer candidatoId = candidatoDao.inserirCandidato(c)

            c.competencias.each { nomeCompetencia ->
                Integer competenciaId = competenciaDao.buscarOuCriar(nomeCompetencia)
                candidatoDao.vincularCompetenciaAoCandidato(candidatoId, competenciaId)
            }

            return candidatoId
        } catch (SQLException e) {
            throw traduzirErro(e)
        }
    }

    List<Map> listarCandidatosAnonimosService() {
        try {
            return candidatoDao.listarCandidatosAnonimos()
        } catch (SQLException e) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, e)
        }
    }

    List<Candidato> listarTodosCandidatosService() {
        try {
            return candidatoDao.listarTodosCandidatos()
        } catch (SQLException e) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, e)
        }
    }

    void atualizarCandidatoService(Candidato c) {
        try {
            candidatoDao.atualizarCandidato(c)
        } catch (SQLException e) {
            throw traduzirErro(e)
        }
    }

    void removerCandidatoService(Integer id) {
        try {
            candidatoDao.removerCandidato(id)
        } catch (SQLException e) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, e)
        }
    }

    private RuntimeException traduzirErro(SQLException e) {
        String msg = e.message?.toLowerCase() ?: ''

        if (msg.contains('candidato_email_key')) {
            return new RegistroDuplicadoException(MensagensErro.EMAIL_DUPLICADO)
        }
        if (msg.contains('candidato_cpf_key')) {
            return new RegistroDuplicadoException(MensagensErro.CPF_DUPLICADO)
        }

        return new ErroBancoException(MensagensErro.ERRO_BANCO, e)
    }
}