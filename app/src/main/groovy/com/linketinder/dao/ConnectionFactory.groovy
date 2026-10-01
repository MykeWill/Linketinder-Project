package com.linketinder.dao

import java.sql.Connection
import java.sql.SQLException

interface ConnectionFactory {
    Connection criarConexao() throws SQLException
}