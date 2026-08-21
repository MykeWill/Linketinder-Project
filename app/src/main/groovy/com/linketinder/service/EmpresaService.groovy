package com.linketinder.service

import com.linketinder.model.Empresa
import com.linketinder.repository.EmpresaRepository

 class EmpresaService {

    private EmpresaRepository empresaRepository

    EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository
    }

    List<Empresa> listarTodasEmpresasService() {
        empresaRepository.listarTodasEmpresasRepository()
    }

    void cadastrarEmpresaService(Empresa empresa) {
        empresaRepository.adicionarEmpresaRepository(empresa)
    }
 }