package com.linketinder.dao

import com.linketinder.model.Vaga
import com.linketinder.repository.VagaRepository

import java.sql.ResultSet
import java.sql.Types

class VagaDao implements VagaRepository {

    Integer inserirVaga(Vaga v) {
        String sql = '''
            INSERT INTO vaga (empresa_id, recrutador_id, nome, descricao, local)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
        '''
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, v.empresaId)
            stmt.setObject(2, v.recrutadorId, Types.INTEGER)
            stmt.setString(3, v.nome)
            stmt.setString(4, v.descricao)
            stmt.setString(5, v.local)
            def resultado = stmt.executeQuery()
            resultado.next()
            return resultado.getInt('id')
        }
    }

    void vincularCompetenciaAVaga(Integer vagaId, Integer competenciaId) {
        String sql = 'INSERT INTO vaga_competencia (vaga_id, competencia_id) VALUES (?, ?)'
        ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, vagaId)
            stmt.setInt(2, competenciaId)
            stmt.executeUpdate()
        }
    }

    List<Vaga> listarTodasVagas() {
        String sql = '''
            SELECT v.id, v.empresa_id, v.recrutador_id, v.nome, v.descricao, v.local,
                   array_agg(comp.nome) AS competencias
            FROM vaga v
            LEFT JOIN vaga_competencia vc ON v.id = vc.vaga_id
            LEFT JOIN competencia comp ON vc.competencia_id = comp.id
            GROUP BY v.id
            ORDER BY v.id
        '''
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            def resultado = stmt.executeQuery()
            List<Vaga> vagas = []
            while (resultado.next()) {
                vagas << mapearVaga(resultado)
            }
            return vagas
        }
    }

    List<Vaga> listarVagasNaoCurtidasPorCandidato(Integer candidatoId) {
        String sql = '''
            SELECT v.id, v.empresa_id, v.recrutador_id, v.nome, v.descricao, v.local,
                   array_agg(comp.nome) AS competencias
            FROM vaga v
            LEFT JOIN vaga_competencia vc ON v.id = vc.vaga_id
            LEFT JOIN competencia comp ON vc.competencia_id = comp.id
            WHERE NOT EXISTS (
                SELECT 1
                FROM curtida_candidato_vaga ccv
                WHERE ccv.candidato_id = ?
                  AND ccv.vaga_id = v.id
            )
            GROUP BY v.id
            ORDER BY v.id
        '''
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, candidatoId)
            def resultado = stmt.executeQuery()
            List<Vaga> vagas = []
            while (resultado.next()) {
                vagas << mapearVaga(resultado)
            }
            return vagas
        }
    }

    List<Vaga> listarVagasPorEmpresa(Integer empresaId) {
        String sql = '''
            SELECT v.id, v.empresa_id, v.recrutador_id, v.nome, v.descricao, v.local,
                   array_agg(comp.nome) AS competencias
            FROM vaga v
            LEFT JOIN vaga_competencia vc ON v.id = vc.vaga_id
            LEFT JOIN competencia comp ON vc.competencia_id = comp.id
            WHERE v.empresa_id = ?
            GROUP BY v.id
            ORDER BY v.id
        '''
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, empresaId)
            def resultado = stmt.executeQuery()
            List<Vaga> vagas = []
            while (resultado.next()) {
                vagas << mapearVaga(resultado)
            }
            return vagas
        }
    }

    List<Vaga> listarVagasVisiveisParaRecrutador(Integer empresaId) {
        String sql = '''
            SELECT v.id, v.empresa_id, v.recrutador_id, v.nome, v.descricao, v.local,
                   array_agg(comp.nome) AS competencias
            FROM vaga v
            LEFT JOIN vaga_competencia vc ON v.id = vc.vaga_id
            LEFT JOIN competencia comp ON vc.competencia_id = comp.id
            WHERE v.empresa_id = ?
            GROUP BY v.id
            ORDER BY v.id
        '''
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, empresaId)
            def resultado = stmt.executeQuery()
            List<Vaga> vagas = []
            while (resultado.next()) {
                vagas << mapearVaga(resultado)
            }
            return vagas
        }
    }

    boolean vagaPertenceRecrutador(Integer vagaId, Integer recrutadorId) {
        String sql = 'SELECT EXISTS (SELECT 1 FROM vaga WHERE id = ? AND recrutador_id = ?)'
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, vagaId)
            stmt.setInt(2, recrutadorId)
            def resultado = stmt.executeQuery()
            resultado.next()
            return resultado.getBoolean(1)
        }
    }

    boolean vagaPertenceEmpresa(Integer vagaId, Integer empresaId) {
        String sql = 'SELECT EXISTS (SELECT 1 FROM vaga WHERE id = ? AND empresa_id = ?)'
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, vagaId)
            stmt.setInt(2, empresaId)
            def resultado = stmt.executeQuery()
            resultado.next()
            return resultado.getBoolean(1)
        }
    }

    boolean atualizarVaga(Vaga v, String tipoEditor, Integer editorId, List<Integer> competenciaIds) {
        String sql = '''
            UPDATE vaga
            SET nome = ?, descricao = ?, local = ?
            WHERE id = ?
              AND ((? = 'EMPRESA' AND empresa_id = ?)
                OR (? = 'RECRUTADOR' AND recrutador_id = ?))
        '''
        return ConexaoDB.executar { conexao ->
            conexao.autoCommit = false
            try {
                def stmt = conexao.prepareStatement(sql)
                stmt.setString(1, v.nome)
                stmt.setString(2, v.descricao)
                stmt.setString(3, v.local)
                stmt.setInt(4, v.id)
                stmt.setString(5, tipoEditor)
                stmt.setInt(6, editorId)
                stmt.setString(7, tipoEditor)
                stmt.setInt(8, editorId)
                if (stmt.executeUpdate() == 0) {
                    conexao.rollback()
                    return false
                }

                def removerCompetencias = conexao.prepareStatement(
                        'DELETE FROM vaga_competencia WHERE vaga_id = ?')
                removerCompetencias.setInt(1, v.id)
                removerCompetencias.executeUpdate()

                def inserirCompetencia = conexao.prepareStatement(
                        'INSERT INTO vaga_competencia (vaga_id, competencia_id) VALUES (?, ?)')
                competenciaIds.each { Integer competenciaId ->
                    inserirCompetencia.setInt(1, v.id)
                    inserirCompetencia.setInt(2, competenciaId)
                    inserirCompetencia.addBatch()
                }
                inserirCompetencia.executeBatch()
                conexao.commit()
                return true
            } catch (Exception e) {
                conexao.rollback()
                throw e
            }
        }
    }

    void removerVaga(Integer id) {
        String sql = 'DELETE FROM vaga WHERE id = ?'
        ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            stmt.executeUpdate()
        }
    }

    private Vaga mapearVaga(ResultSet resultado) {
        Vaga v = new Vaga(
                resultado.getInt('empresa_id'),
                resultado.getString('nome'),
                resultado.getString('descricao'),
                resultado.getString('local'),
                (resultado.getArray('competencias')?.array as List)?.toList()?.collect { it.toString() } ?: [],
                resultado.getObject('recrutador_id') as Integer
        )
        v.id = resultado.getInt('id')
        return v
    }
}