package com.linketinder.service

import com.linketinder.dao.EmpresaDao
import com.linketinder.model.Empresa

class EmpresaService {

    private EmpresaDao empresaDao = new EmpresaDao()

    Integer cadastrarEmpresa(Empresa e) {
        return empresaDao.inserirEmpresa(e)
    }

    List<Map> listarEmpresasAnonimasService() {
        return empresaDao.listarEmpresasAnonimas()
    }

    List<Empresa> listarTodasEmpresasService() {
        return empresaDao.listarTodasEmpresas()
    }

    void atualizarEmpresaService(Empresa e) {
        empresaDao.atualizarEmpresa(e)
    }

    void removerEmpresaService(Integer id) {
        empresaDao.removerEmpresa(id)
    }
}