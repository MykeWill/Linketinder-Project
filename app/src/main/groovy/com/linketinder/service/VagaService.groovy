package com.linketinder.service

import com.linketinder.dao.VagaDao
import com.linketinder.dao.CompetenciaDao
import com.linketinder.model.Vaga

class VagaService {

    private VagaDao vagaDao = new VagaDao()
    private CompetenciaDao competenciaDao = new CompetenciaDao()

    Integer cadastrarVaga(Vaga v) {
        Integer vagaId = vagaDao.inserirVaga(v)

        v.competencias.each { nomeCompetencia ->
            Integer competenciaId = competenciaDao.buscarOuCriar(nomeCompetencia)
            vagaDao.vincularCompetenciaAVaga(vagaId, competenciaId)
        }

        return vagaId
    }

    List<Vaga> listarTodasVagasService() {
        return vagaDao.listarTodasVagas()
    }

    List<Vaga> listarVagasPorEmpresaService(Integer empresaId) {
        return vagaDao.listarVagasPorEmpresa(empresaId)
    }

    void atualizarVagaService(Vaga v) {
        vagaDao.atualizarVaga(v)
    }

    void removerVagaService(Integer id) {
        vagaDao.removerVaga(id)
    }
}