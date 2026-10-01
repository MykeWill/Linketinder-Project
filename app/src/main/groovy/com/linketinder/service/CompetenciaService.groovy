package com.linketinder.service

import com.linketinder.dao.CompetenciaDao
import com.linketinder.exception.ErroBancoException
import com.linketinder.exception.MensagensErro
import com.linketinder.model.Competencia
import java.sql.SQLException

class CompetenciaService {

    private final CompetenciaDao competenciaDao

    CompetenciaService(CompetenciaDao competenciaDao) {
        this.competenciaDao = competenciaDao
    }

    Integer buscarOuCriarCompetenciaService(String nome) {
        try {
            return competenciaDao.buscarOuCriar(nome)
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }

    List<Competencia> listarTodasCompetenciasService() {
        try {
            return competenciaDao.listarTodas()
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }
}