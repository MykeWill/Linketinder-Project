import type { EmpresaInterface } from '../types/models'
import * as empresaRepository from '../repository/empresaRepository'
import {
    validarNome, validarEmail, validarCNPJ,
    validarEstado, validarCEP, validarDescricao, validarSenha
} from '../utils/validacoes'

export function listarTodasEmpresasService(): EmpresaInterface[] {
    return empresaRepository.listarTodasEmpresasRepository()
}

export function cadastrarEmpresaService(dados: Omit<EmpresaInterface, 'id'>): EmpresaInterface {
    const erros = [
        validarNome(dados.nome),
        validarEmail(dados.email),
        validarCNPJ(dados.cnpj),
        validarEstado(dados.estado),
        validarCEP(dados.cep),
        validarDescricao(dados.descricao),
        validarSenha(dados.senha)
    ].filter(e => e !== null)

    if (erros.length > 0) {
        throw new Error(erros[0] as string)
    }

    const novaEmpresaService: EmpresaInterface = {
        id: crypto.randomUUID(),
        ...dados
    }

    empresaRepository.adicionarEmpresaRepository(novaEmpresaService)
    return novaEmpresaService
}

export function removerEmpresaService(id: string): void {
    empresaRepository.removerEmpresaRepository(id)
}

export function buscarEmpresaPorEmailService(email: string): EmpresaInterface | undefined {
    return empresaRepository.buscarEmpresaPorEmailRepository(email)
}