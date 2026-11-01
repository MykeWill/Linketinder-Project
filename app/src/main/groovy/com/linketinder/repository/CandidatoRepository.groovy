package com.linketinder.repository

import com.linketinder.model.Candidato
import java.sql.SQLException

interface CandidatoRepository {
    Integer inserirCandidato(Candidato c) throws SQLException
    void vincularCompetenciaAoCandidato(Integer candidatoId, Integer competenciaId) throws SQLException
    List<Map> listarCandidatosAnonimos() throws SQLException
    List<Map> listarMatchesDoCandidato(Integer candidatoId) throws SQLException
    void curtirVaga(Integer candidatoId, Integer vagaId) throws SQLException
    boolean existeCurtidaDaEmpresaParaVaga(Integer candidatoId, Integer vagaId) throws SQLException
    List<Candidato> listarTodosCandidatos() throws SQLException
    void atualizarCandidato(Candidato c) throws SQLException
    void removerCandidato(Integer id) throws SQLException
}