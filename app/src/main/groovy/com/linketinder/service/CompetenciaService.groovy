package com.linketinder.service

import com.linketinder.dao.CompetenciaDao
import com.linketinder.model.Competencia

class CompetenciaService {

    private CompetenciaDao competenciaDao = new CompetenciaDao()

    Integer buscarOuCriarCompetenciaService(String nome) {
        return competenciaDao.buscarOuCriar(nome)
    }

    List<Competencia> listarTodasCompetenciasService() {
        return competenciaDao.listarTodas()
    }
}