package com.linketinder.controller

import com.linketinder.model.Empresa
import com.linketinder.service.EmpresaService

class EmpresaController {

    private final EmpresaService empresaService

    EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService
    }

    Integer cadastrarEmpresaController(Empresa empresa) {
        return empresaService.cadastrarEmpresa(empresa)
    }

    List<Empresa> listarTodasEmpresasController() {
        return empresaService.listarTodasEmpresasService()
    }

    List<Map> listarEmpresasAnonimasController() {
        return empresaService.listarEmpresasAnonimasService()
    }

    void atualizarEmpresaController(Empresa empresa) {
        empresaService.atualizarEmpresaService(empresa)
    }

    void removerEmpresaController(Integer id) {
        empresaService.removerEmpresaService(id)
    }
}