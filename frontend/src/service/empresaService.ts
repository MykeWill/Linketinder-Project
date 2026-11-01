import type { EmpresaInterface } from '../types/models'
import * as empresaRepository from '../repository/empresaRepository'
import { request } from './api'
import {
    validarNome, validarEmail, validarCNPJ,
    validarEstado, validarCEP, validarDescricao, validarSenha
} from '../utils/validacoes'

export function listarTodasEmpresasService(): EmpresaInterface[] {
    return empresaRepository.listarTodasEmpresasRepository()
}

export async function cadastrarEmpresaService(dados: Omit<EmpresaInterface, 'id'>): Promise<number> {
    const cnpj = dados.cnpj.replace(/\D/g, '')
    const erros = [
        validarNome(dados.nome),
        validarEmail(dados.email),
        validarCNPJ(cnpj),
        validarEstado(dados.estado),
        validarCEP(dados.cep),
        validarDescricao(dados.descricao),
        validarSenha(dados.senha)
    ].filter(e => e !== null)

    if (erros.length > 0) {
        throw new Error(erros[0] as string)
    }

    const resposta = await request<{ id: number }>('/empresas', {
        method: 'POST',
        body: JSON.stringify({ ...dados, cnpj })
    })
    return resposta.id
}

export function removerEmpresaService(id: string): void {
    empresaRepository.removerEmpresaRepository(id)
}

export function buscarEmpresaPorEmailService(email: string): EmpresaInterface | undefined {
    return empresaRepository.buscarEmpresaPorEmailRepository(email)
}