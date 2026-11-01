package com.linketinder.service

import com.linketinder.exception.ErroBancoException
import com.linketinder.exception.MensagensErro
import com.linketinder.exception.RegistroDuplicadoException
import com.linketinder.model.Candidato
import com.linketinder.repository.CandidatoRepository
import com.linketinder.repository.CompetenciaRepository

import java.sql.SQLException

class CandidatoService {

    private final CandidatoRepository candidatoDao
    private final CompetenciaRepository competenciaDao

    CandidatoService(CandidatoRepository candidatoDao, CompetenciaRepository competenciaDao) {
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

    List<Map> listarMatchesDoCandidatoService(Integer candidatoId) {
        try {
            return candidatoDao.listarMatchesDoCandidato(candidatoId)
        } catch (SQLException e) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, e)
        }
    }

    boolean curtirVaga(Integer candidatoId, Integer vagaId) {
        try {
            candidatoDao.curtirVaga(candidatoId, vagaId)
            return candidatoDao.existeCurtidaDaEmpresaParaVaga(candidatoId, vagaId)
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