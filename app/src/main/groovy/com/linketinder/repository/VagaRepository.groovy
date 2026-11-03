package com.linketinder.repository

import com.linketinder.model.Vaga
import java.sql.SQLException

interface VagaRepository {
    Integer inserirVaga(Vaga v) throws SQLException
    void vincularCompetenciaAVaga(Integer vagaId, Integer competenciaId) throws SQLException
    List<Vaga> listarTodasVagas() throws SQLException
    List<Vaga> listarVagasNaoCurtidasPorCandidato(Integer candidatoId) throws SQLException
    List<Vaga> listarVagasPorEmpresa(Integer empresaId) throws SQLException
    List<Vaga> listarVagasVisiveisParaRecrutador(Integer empresaId) throws SQLException
    boolean vagaPertenceEmpresa(Integer vagaId, Integer empresaId) throws SQLException
    boolean vagaPertenceRecrutador(Integer vagaId, Integer recrutadorId) throws SQLException
    boolean atualizarVaga(Vaga v, String tipoEditor, Integer editorId,
                          List<Integer> competenciaIds) throws SQLException
    void removerVaga(Integer id) throws SQLException
}