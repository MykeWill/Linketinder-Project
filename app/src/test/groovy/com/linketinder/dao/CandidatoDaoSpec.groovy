package com.linketinder.dao

import java.sql.Array
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import spock.lang.Specification

class CandidatoDaoSpec extends Specification {

    def "lista curtidas do candidato com dados anonimos e competencias"() {
        given:
        GroovyMock(ConexaoDB, global: true)
        def conexao = Mock(Connection)
        def statement = Mock(PreparedStatement)
        def resultado = Mock(ResultSet)
        def competencias = Mock(Array)
        ConexaoDB.executar(_ as Closure) >> { Closure consulta -> consulta.call(conexao) }
        conexao.prepareStatement(_ as String) >> statement
        statement.executeQuery() >> resultado
        resultado.next() >>> [true, true, false]
        resultado.getInt('id') >>> [42, 43]
        resultado.getString('nome') >>> ['Desenvolvedor Groovy', 'Analista']
        resultado.getString('descricao') >>> ['Descrição fictícia', 'Outra descrição']
        competencias.getArray() >> (['Groovy'] as Object[])
        resultado.getArray('competencias') >>> [competencias, null]
        def dao = new CandidatoDao()

        when:
        def curtidas = dao.listarCurtidasDoCandidato(7)

        then:
        1 * conexao.prepareStatement({ String sql ->
            sql.contains('JOIN vaga v ON v.id = ccv.vaga_id') &&
                    sql.contains('WHERE ccv.candidato_id = ?') &&
                    sql.contains('LEFT JOIN vaga_competencia') &&
                    sql.contains('LEFT JOIN competencia')
        }) >> statement
        1 * statement.setInt(1, 7)
        curtidas == [
                [id: 42, nome: 'Desenvolvedor Groovy', descricao: 'Descrição fictícia',
                 competencias: ['Groovy']],
                [id: 43, nome: 'Analista', descricao: 'Outra descrição', competencias: []]
        ]
    }
}
