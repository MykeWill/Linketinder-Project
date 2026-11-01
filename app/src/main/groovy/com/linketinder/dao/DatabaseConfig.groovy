package com.linketinder.dao

class DatabaseConfig {

    private static final String URL = System.getenv('DB_URL') ?: 'jdbc:postgresql://localhost:5432/linketinder'
    private static final String USUARIO = System.getenv('DB_USER') ?: 'postgres'
    private static final String SENHA = System.getenv('DB_PASSWORD') ?: ''

    static String getUrl() { URL }
    static String getUsuario() { USUARIO }
    static String getSenha() { SENHA }
}