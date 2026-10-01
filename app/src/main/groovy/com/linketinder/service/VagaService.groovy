package com.linketinder.service

import com.linketinder.exception.ErroBancoException
import com.linketinder.exception.MensagensErro
import com.linketinder.model.Vaga
import com.linketinder.repository.CompetenciaRepository
import com.linketinder.repository.VagaRepository

import java.sql.SQLException

class VagaService {

    private final VagaRepository vagaDao
    private final CompetenciaRepository competenciaDao

    VagaService(VagaRepository vagaDao, CompetenciaRepository competenciaDao) {
        this.vagaDao = vagaDao
        this.competenciaDao = competenciaDao
    }

    Integer cadastrarVagaService(Vaga v) {
        try {
            Integer vagaId = vagaDao.inserirVaga(v)

            v.competencias.each { nomeCompetencia ->
                Integer competenciaId = competenciaDao.buscarOuCriar(nomeCompetencia)
                vagaDao.vincularCompetenciaAVaga(vagaId, competenciaId)
            }

            return vagaId
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }

    List<Vaga> listarTodasVagasService() {
        try {
            return vagaDao.listarTodasVagas()
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }

    List<Vaga> listarVagasPorEmpresaService(Integer empresaId) {
        try {
            return vagaDao.listarVagasPorEmpresa(empresaId)
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }

    void atualizarVagaService(Vaga v) {
        try {
            vagaDao.atualizarVaga(v)
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }

    void removerVagaService(Integer id) {
        try {
            vagaDao.removerVaga(id)
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }
}