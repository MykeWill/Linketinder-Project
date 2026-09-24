package com.linketinder.service

import com.linketinder.dao.EmpresaDao

class EmpresaService {

    private EmpresaDao empresaDao = new EmpresaDao()

    Integer cadastrarEmpresa(String nome, String cnpj, String email, String descricao, String pais, String cep, String senha) {
        return empresaDao.inserirEmpresa(nome, cnpj, email, descricao, pais, cep, senha)
    }

    List<Map> listarTodasEmpresasService() {
        return empresaDao.listarTodasEmpresas()
    }

    void atualizarEmpresaService(Integer id, String descricao) {
        empresaDao.atualizarEmpresa(id, descricao)
    }

    void removerEmpresaService(Integer id) {
        empresaDao.removerEmpresa(id)
    }
}