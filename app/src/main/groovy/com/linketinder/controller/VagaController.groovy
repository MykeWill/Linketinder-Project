package com.linketinder.controller

import com.linketinder.model.Vaga
import com.linketinder.service.VagaService

class VagaController {

    private final VagaService vagaService

    VagaController(VagaService vagaService) {
        this.vagaService = vagaService
    }

    Integer cadastrarVagaController(Vaga vaga) {
        return vagaService.cadastrarVagaService(vaga)
    }

    List<Vaga> listarTodasVagasController() {
        return vagaService.listarTodasVagasService()
    }

    List<Vaga> listarVagasNaoCurtidasPorCandidatoController(Integer candidatoId) {
        return vagaService.listarVagasNaoCurtidasPorCandidato(candidatoId)
    }

    List<Vaga> listarVagasPorEmpresaController(Integer empresaId) {
        return vagaService.listarVagasPorEmpresaService(empresaId)
    }

    List<Vaga> listarVagasVisiveisParaRecrutadorController(Integer empresaId) {
        return vagaService.listarVagasVisiveisParaRecrutador(empresaId)
    }

    boolean atualizarVagaController(Vaga vaga, String tipoEditor, Integer editorId) {
        return vagaService.atualizarVagaService(vaga, tipoEditor, editorId)
    }

    void removerVagaController(Integer id) {
        vagaService.removerVagaService(id)
    }
}