package com.linketinder.dao

class DatabaseConfig {

    private static final Map<String, String> DOT_ENV = loadDotEnv()
    private static final String URL = getConfigValue('DB_URL', 'jdbc:postgresql://localhost:5432/linketinder')
    private static final String USUARIO = getConfigValue('DB_USER', 'postgres')
    private static final String SENHA = getConfigValue('DB_PASSWORD', '')

    static String getUrl() { URL }
    static String getUsuario() { USUARIO }
    static String getSenha() { SENHA }

    private static String getConfigValue(String key, String defaultValue) {
        System.getenv(key) ?: DOT_ENV[key] ?: defaultValue
    }

    private static Map<String, String> loadDotEnv() {
        File currentDirectory = new File(System.getProperty('user.dir')).canonicalFile
        File parentDirectory = currentDirectory.parentFile
        List<File> candidates = [
                new File(currentDirectory, '.env'),
                parentDirectory ? new File(parentDirectory, '.env') : null
        ].findAll { it != null }
        File envFile = candidates.find { it.isFile() }

        if (envFile == null) {
            return [:]
        }

        Map<String, String> values = [:]
        envFile.readLines('UTF-8').each { String line ->
            String content = line.trim()
            if (!content || content.startsWith('#')) {
                return
            }

            int separator = content.indexOf('=')
            if (separator <= 0) {
                return
            }

            String key = content.substring(0, separator).trim()
            if (key) {
                values[key] = content.substring(separator + 1).trim()
            }
        }

        values
    }
}