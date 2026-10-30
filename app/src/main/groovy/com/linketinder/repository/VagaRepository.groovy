package com.linketinder.repository

import com.linketinder.model.Vaga
import java.sql.SQLException

interface VagaRepository {
    Integer inserirVaga(Vaga v) throws SQLException
    void vincularCompetenciaAVaga(Integer vagaId, Integer competenciaId) throws SQLException
    List<Vaga> listarTodasVagas() throws SQLException
    List<Vaga> listarVagasPorEmpresa(Integer empresaId) throws SQLException
    List<Vaga> listarVagasVisiveisParaRecrutador(Integer empresaId, Integer recrutadorId) throws SQLException
    void atualizarVaga(Vaga v) throws SQLException
    void removerVaga(Integer id) throws SQLException
}