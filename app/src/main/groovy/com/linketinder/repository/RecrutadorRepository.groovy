package com.linketinder.repository

import com.linketinder.model.Recrutador

import java.sql.SQLException

interface RecrutadorRepository {
    Integer inserirRecrutador(Recrutador recrutador) throws SQLException
    Recrutador buscarRecrutadorPorId(Integer id) throws SQLException
    List<Recrutador> listarRecrutadoresPorEmpresa(Integer empresaId) throws SQLException
    boolean existeEmail(String email) throws SQLException
    boolean existeCpf(String cpf) throws SQLException
    void removerRecrutador(Integer id) throws SQLException
}
