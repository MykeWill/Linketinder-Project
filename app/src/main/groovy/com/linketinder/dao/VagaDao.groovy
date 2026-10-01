package com.linketinder.dao

import com.linketinder.model.Vaga
import java.sql.ResultSet

class VagaDao {

    Integer inserirVaga(Vaga v) {
        String sql = '''
            INSERT INTO vaga (empresa_id, nome, descricao, local)
            VALUES (?, ?, ?, ?)
            RETURNING id
        '''
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, v.empresaId)
            stmt.setString(2, v.nome)
            stmt.setString(3, v.descricao)
            stmt.setString(4, v.local)
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
            SELECT v.id, v.empresa_id, v.nome, v.descricao, v.local,
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

    List<Vaga> listarVagasPorEmpresa(Integer empresaId) {
        String sql = '''
            SELECT v.id, v.empresa_id, v.nome, v.descricao, v.local,
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

    void atualizarVaga(Vaga v) {
        String sql = 'UPDATE vaga SET empresa_id = ?, nome = ?, descricao = ?, local = ? WHERE id = ?'
        ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, v.empresaId)
            stmt.setString(2, v.nome)
            stmt.setString(3, v.descricao)
            stmt.setString(4, v.local)
            stmt.setInt(5, v.id)
            stmt.executeUpdate()
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
                (resultado.getArray('competencias')?.array as List)?.toList()?.collect { it.toString() } ?: []
        )
        v.id = resultado.getInt('id')
        return v
    }
}