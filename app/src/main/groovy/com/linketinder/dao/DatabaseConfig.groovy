package com.linketinder.dao

class DatabaseConfig {

    private static final Properties PROPRIEDADES = carregarPropriedades()

    private static Properties carregarPropriedades() {
        def props = new Properties()
        def stream = DatabaseConfig.classLoader.getResourceAsStream('database.properties')
        if (stream == null) {
            throw new IllegalStateException('Arquivo database.properties não encontrado.')
        }
        props.load(stream)
        return props
    }

    static String getUrl() { PROPRIEDADES.getProperty('db.url') }
    static String getUsuario() { PROPRIEDADES.getProperty('db.usuario') }
    static String getSenha() { PROPRIEDADES.getProperty('db.senha') }
}