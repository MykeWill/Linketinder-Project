package com.linketinder.service

import com.linketinder.dao.EmpresaDao
import com.linketinder.exception.ErroBancoException
import com.linketinder.exception.MensagensErro
import com.linketinder.exception.RegistroDuplicadoException
import com.linketinder.model.Empresa
import java.sql.SQLException

class EmpresaService {

    private EmpresaDao empresaDao = new EmpresaDao()

    Integer cadastrarEmpresa(Empresa e) {
        try {
            return empresaDao.inserirEmpresa(e)
        } catch (SQLException ex) {
            throw traduzirErro(ex)
        }
    }

    List<Map> listarEmpresasAnonimasService() {
        try {
            return empresaDao.listarEmpresasAnonimas()
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }

    List<Empresa> listarTodasEmpresasService() {
        try {
            return empresaDao.listarTodasEmpresas()
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }

    void atualizarEmpresaService(Empresa e) {
        try {
            empresaDao.atualizarEmpresa(e)
        } catch (SQLException ex) {
            throw traduzirErro(ex)
        }
    }

    void removerEmpresaService(Integer id) {
        try {
            empresaDao.removerEmpresa(id)
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }

    private RuntimeException traduzirErro(SQLException ex) {
        String msg = ex.message?.toLowerCase() ?: ''

        if (msg.contains('empresa_email_key')) {
            return new RegistroDuplicadoException(MensagensErro.EMAIL_DUPLICADO)
        }
        if (msg.contains('empresa_cnpj_key')) {
            return new RegistroDuplicadoException(MensagensErro.CNPJ_DUPLICADO)
        }

        return new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
    }
}