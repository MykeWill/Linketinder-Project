package com.linketinder.dao

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
