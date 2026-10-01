package com.linketinder.service

import com.linketinder.dao.VagaDao
import com.linketinder.dao.CompetenciaDao
import com.linketinder.exception.ErroBancoException
import com.linketinder.exception.MensagensErro
import com.linketinder.model.Vaga
import java.sql.SQLException

class VagaService {

    private final VagaDao vagaDao
    private final CompetenciaDao competenciaDao

    VagaService(VagaDao vagaDao, CompetenciaDao competenciaDao) {
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