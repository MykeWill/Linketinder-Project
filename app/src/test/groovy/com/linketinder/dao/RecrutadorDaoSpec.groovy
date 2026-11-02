package com.linketinder.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import spock.lang.Specification

class RecrutadorDaoSpec extends Specification {

    def "verifica se o candidato curtiu vaga da empresa"() {
        given:
        GroovyMock(ConexaoDB, global: true)
        def conexao = Mock(Connection)
        def statement = Mock(PreparedStatement)
        def resultado = Mock(ResultSet)
        ConexaoDB.executar(_ as Closure) >> { Closure consulta -> consulta.call(conexao) }
        conexao.prepareStatement(_ as String) >> statement
        statement.executeQuery() >> resultado
        resultado.next() >> true
        resultado.getBoolean(1) >> true
        def dao = new RecrutadorDao()

        when:
        boolean match = dao.candidatoCurtiuVagaDaEmpresa(7, 1)

        then:
        1 * conexao.prepareStatement({ String sql ->
            sql.contains('FROM curtida_candidato_vaga ccv') &&
                    sql.contains('JOIN vaga v ON v.id = ccv.vaga_id') &&
                    sql.contains('ccv.candidato_id = ?') &&
                    sql.contains('v.empresa_id = ?')
        }) >> statement
        1 * statement.setInt(1, 7)
        1 * statement.setInt(2, 1)
        match
    }
}
