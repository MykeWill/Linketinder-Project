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
        boolean existeCNPJ = empresaRepository.listarTodasEmpresasRepository().any {it.cnpj == empresa.cnpj }
        if (existeCNPJ) {
            throw new IllegalArgumentException("CNPJ já cadastrado")
        }
        empresaRepository.adicionarEmpresaRepository(empresa)
    }
 }