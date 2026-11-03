package com.linketinder.repository

import com.linketinder.model.Candidato
import com.linketinder.model.Match
import com.linketinder.model.Recrutador

import java.sql.SQLException

interface RecrutadorRepository {
    Integer inserirRecrutador(Recrutador recrutador) throws SQLException
    Recrutador buscarPorEmail(String email) throws SQLException
    Recrutador buscarRecrutadorPorId(Integer id) throws SQLException
    List<Recrutador> listarRecrutadoresPorEmpresa(Integer empresaId) throws SQLException
    boolean existeEmail(String email) throws SQLException
    boolean existeCpf(String cpf) throws SQLException
    List<Candidato> listarCandidatosDisponiveis(Integer recrutadorId) throws SQLException
    boolean empresaJaCurtiuCandidatoNaVaga(Integer empresaId, Integer candidatoId, Integer vagaId) throws SQLException
    boolean vagaPertenceEmpresa(Integer vagaId, Integer empresaId) throws SQLException
    boolean candidatoCurtiuVaga(Integer candidatoId, Integer vagaId) throws SQLException
    void curtirCandidato(Integer empresaId, Integer candidatoId, Integer recrutadorId, Integer vagaId) throws SQLException
    List<Match> listarMatchesDoRecrutador(Integer recrutadorId) throws SQLException
    void removerRecrutador(Integer id) throws SQLException
}
