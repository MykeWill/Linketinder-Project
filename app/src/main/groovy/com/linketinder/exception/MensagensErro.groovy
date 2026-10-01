package com.linketinder.exception

class MensagensErro {

    private MensagensErro() {}

    static final String DADOS_INVALIDOS = 'Dados inválidos. Verifique o formulário.'
    static final String EMAIL_DUPLICADO = 'Este e-mail já está cadastrado.'
    static final String CPF_DUPLICADO = 'Este CPF já está cadastrado.'
    static final String CNPJ_DUPLICADO = 'Este CNPJ já está cadastrado.'
    static final String CANDIDATO_NAO_ENCONTRADO = 'Candidato não encontrado.'
    static final String EMPRESA_NAO_ENCONTRADA = 'Empresa não encontrada.'
    static final String VAGA_NAO_ENCONTRADA = 'Vaga não encontrada.'
    static final String ERRO_BANCO = 'Erro ao acessar o banco de dados. Tente novamente.'
}