package com.linketinder.repository

import com.linketinder.model.Empresa
import java.sql.SQLException

interface EmpresaRepository {
    Integer inserirEmpresa(Empresa e) throws SQLException
    Empresa buscarPorId(Integer id) throws SQLException
    Empresa buscarPorEmail(String email) throws SQLException
    List<Empresa> listarTodasEmpresas() throws SQLException
    List<Map> listarEmpresasAnonimas() throws SQLException
    boolean atualizarEmpresa(Empresa e) throws SQLException
    void removerEmpresa(Integer id) throws SQLException
}