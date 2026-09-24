package com.linketinder.service

import com.linketinder.dao.VagaDao
import com.linketinder.dao.CompetenciaDao

class VagaService {

    private VagaDao vagaDao = new VagaDao()
    private CompetenciaDao competenciaDao = new CompetenciaDao()

    Integer cadastrarVaga(Integer empresaId, String nome, String descricao, String local, List<String> competencias) {
        Integer vagaId = vagaDao.inserirVaga(empresaId, nome, descricao, local)

        competencias.each { nomeCompetencia ->
            Integer competenciaId = competenciaDao.buscarOuCriar(nomeCompetencia)
            vagaDao.vincularCompetenciaAVaga(vagaId, competenciaId)
        }

        return vagaId
    }

    List<Map> listarTodasVagasService() {
        return vagaDao.listarTodasVagas()
    }

    List<Map> listarVagasPorEmpresaService(Integer empresaId) {
        return vagaDao.listarVagasPorEmpresa(empresaId)
    }

    void atualizarVagaService(Integer id, String descricao) {
        vagaDao.atualizarVaga(id, descricao)
    }

    void removerVagaService(Integer id) {
        vagaDao.removerVaga(id)
    }
}