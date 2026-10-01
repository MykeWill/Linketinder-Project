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

    List<Vaga> listarVagasPorEmpresaController(Integer empresaId) {
        return vagaService.listarVagasPorEmpresaService(empresaId)
    }

    void atualizarVagaController(Vaga vaga) {
        vagaService.atualizarVagaService(vaga)
    }

    void removerVagaController(Integer id) {
        vagaService.removerVagaService(id)
    }
}