package com.linketinder.repository

import com.linketinder.model.Competencia
import java.sql.SQLException

interface CompetenciaRepository {
    Integer buscarIdPorNome(String nome) throws SQLException
    Integer inserir(String nome) throws SQLException
    Integer buscarOuCriar(String nome) throws SQLException
    List<Competencia> listarTodas() throws SQLException
}