package com.linketinder.service

import com.linketinder.exception.ErroBancoException
import com.linketinder.exception.DadosInvalidosException
import com.linketinder.exception.MensagensErro
import com.linketinder.exception.RegistroDuplicadoException
import com.linketinder.model.Empresa
import com.linketinder.repository.EmpresaRepository
import org.mindrot.jbcrypt.BCrypt

import java.sql.SQLException

class EmpresaService {

    private final EmpresaRepository empresaDao

    EmpresaService(EmpresaRepository empresaDao) {
        this.empresaDao = empresaDao
    }

    Integer cadastrarEmpresa(Empresa e) {
        String cnpj = e?.cnpj?.replaceAll('\\D', '')
        if (!(cnpj ==~ /\d{14}/)) {
            throw new DadosInvalidosException(MensagensErro.DADOS_INVALIDOS)
        }
        e.cnpj = cnpj
        e.senha = BCrypt.hashpw(e.senha, BCrypt.gensalt())

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
            String cnpj = e?.cnpj?.replaceAll('\\D', '')
            if (!(cnpj ==~ /\d{14}/) || e.id == null || e.id <= 0 ||
                    !e.nome?.trim() || !e.email?.trim() || !e.pais?.trim() ||
                    !e.estado?.trim() || !e.cep?.trim() || !e.descricao?.trim()) {
                throw new DadosInvalidosException(MensagensErro.DADOS_INVALIDOS)
            }
            e.cnpj = cnpj
            if (e.senha) {
                e.senha = BCrypt.hashpw(e.senha, BCrypt.gensalt())
            }
            if (!empresaDao.atualizarEmpresa(e)) {
                throw new IllegalArgumentException('Empresa não encontrada.')
            }
        } catch (SQLException ex) {
            throw traduzirErro(ex)
        }
    }

    Empresa buscarEmpresaPorIdService(Integer id) {
        try {
            return empresaDao.buscarPorId(id)
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
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