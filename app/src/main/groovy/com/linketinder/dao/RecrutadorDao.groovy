package com.linketinder.dao

import com.linketinder.model.Candidato
import com.linketinder.model.Recrutador
import com.linketinder.repository.RecrutadorRepository

import java.sql.Date
import java.sql.ResultSet

class RecrutadorDao implements RecrutadorRepository {

    Integer inserirRecrutador(Recrutador recrutador) {
        String sql = '''
            INSERT INTO recrutador
                (empresa_id, nome, area_recrutamento, descricao, email, cpf, data_nascimento, senha_hash)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        '''
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, recrutador.empresaId)
            stmt.setString(2, recrutador.nome)
            stmt.setString(3, recrutador.areaRecrutamento)
            stmt.setString(4, recrutador.descricao)
            stmt.setString(5, recrutador.email)
            stmt.setString(6, recrutador.cpf)
            stmt.setDate(7, Date.valueOf(recrutador.dataNascimento))
            stmt.setString(8, recrutador.senhaHash)
            def resultado = stmt.executeQuery()
            resultado.next()
            return resultado.getInt('id')
        }
    }

    Recrutador buscarRecrutadorPorId(Integer id) {
        String sql = '''
            SELECT id, empresa_id, nome, area_recrutamento, descricao, email, cpf, data_nascimento, senha_hash
            FROM recrutador
            WHERE id = ?
        '''
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            def resultado = stmt.executeQuery()
            return resultado.next() ? mapearRecrutador(resultado) : null
        }
    }

    List<Recrutador> listarRecrutadoresPorEmpresa(Integer empresaId) {
        String sql = '''
            SELECT id, empresa_id, nome, area_recrutamento, descricao, email, cpf, data_nascimento, senha_hash
            FROM recrutador
            WHERE empresa_id = ?
            ORDER BY id
        '''
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, empresaId)
            def resultado = stmt.executeQuery()
            List<Recrutador> recrutadores = []
            while (resultado.next()) {
                recrutadores << mapearRecrutador(resultado)
            }
            return recrutadores
        }
    }

    boolean existeEmail(String email) {
        String sql = 'SELECT EXISTS (SELECT 1 FROM recrutador WHERE email = ?)'
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setString(1, email)
            def resultado = stmt.executeQuery()
            resultado.next()
            return resultado.getBoolean(1)
        }
    }

    boolean existeCpf(String cpf) {
        String sql = 'SELECT EXISTS (SELECT 1 FROM recrutador WHERE cpf = ?)'
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setString(1, cpf)
            def resultado = stmt.executeQuery()
            resultado.next()
            return resultado.getBoolean(1)
        }
    }

    List<Candidato> listarCandidatosDisponiveis(Integer recrutadorId) {
        String sql = '''
            SELECT c.id, c.descricao, array_agg(comp.nome) AS competencias
            FROM candidato c
            JOIN candidato_competencia cc ON c.id = cc.candidato_id
            JOIN competencia comp ON cc.competencia_id = comp.id
            WHERE NOT EXISTS (
                SELECT 1
                FROM recrutador r
                JOIN curtida_empresa_candidato cec ON cec.empresa_id = r.empresa_id
                WHERE r.id = ? AND cec.candidato_id = c.id
            )
            GROUP BY c.id
            ORDER BY c.id
        '''
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, recrutadorId)
            def resultado = stmt.executeQuery()
            List<Candidato> candidatos = []
            while (resultado.next()) {
                Candidato candidato = new Candidato(
                        null,
                        null,
                        null,
                        0,
                        null,
                        null,
                        resultado.getString('descricao'),
                        null,
                        (resultado.getArray('competencias')?.array as List)?.toList()?.collect { it.toString() } ?: []
                )
                candidato.id = resultado.getInt('id')
                candidatos << candidato
            }
            return candidatos
        }
    }

    boolean empresaJaCurtiuCandidato(Integer empresaId, Integer candidatoId) {
        String sql = '''
            SELECT EXISTS (
                SELECT 1 FROM curtida_empresa_candidato
                WHERE empresa_id = ? AND candidato_id = ?
            )
        '''
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, empresaId)
            stmt.setInt(2, candidatoId)
            def resultado = stmt.executeQuery()
            resultado.next()
            return resultado.getBoolean(1)
        }
    }

    void curtirCandidato(Integer empresaId, Integer candidatoId, Integer recrutadorId) {
        String sql = '''
            INSERT INTO curtida_empresa_candidato (empresa_id, candidato_id, recrutador_id)
            VALUES (?, ?, ?)
        '''
        ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, empresaId)
            stmt.setInt(2, candidatoId)
            stmt.setInt(3, recrutadorId)
            stmt.executeUpdate()
        }
    }

    void removerRecrutador(Integer id) {
        String sql = 'DELETE FROM recrutador WHERE id = ?'
        ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            stmt.executeUpdate()
        }
    }

    private Recrutador mapearRecrutador(ResultSet resultado) {
        Recrutador recrutador = new Recrutador(
                resultado.getInt('empresa_id'),
                resultado.getString('nome'),
                resultado.getString('area_recrutamento'),
                resultado.getString('descricao'),
                resultado.getString('email'),
                resultado.getString('cpf'),
                resultado.getDate('data_nascimento').toLocalDate(),
                resultado.getString('senha_hash')
        )
        recrutador.id = resultado.getInt('id')
        return recrutador
    }
}
