import type { EmpresaInterface, PerfilEmpresaInterface } from '../types/models'
import { request } from './api'
import {
    validarNome, validarEmail, validarCNPJ,
    validarEstado, validarCEP, validarDescricao, validarSenha
} from '../utils/validacoes'

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

export function buscarPerfilEmpresa(id: number): Promise<PerfilEmpresaInterface> {
    return request<PerfilEmpresaInterface>(`/empresas/${id}`)
}

export async function atualizarPerfilEmpresa(
    id: number,
    dados: Omit<EmpresaInterface, 'id' | 'senha'> & { senha?: string }
): Promise<void> {
    const cnpj = dados.cnpj.replace(/\D/g, '')
    const erros = [
        validarNome(dados.nome),
        validarEmail(dados.email),
        validarCNPJ(cnpj),
        validarEstado(dados.estado),
        validarCEP(dados.cep),
        validarDescricao(dados.descricao),
        dados.senha ? validarSenha(dados.senha) : null
    ].filter(e => e !== null)
    if (erros.length > 0) {
        throw new Error(erros[0] as string)
    }

    await request<{ mensagem: string }>(`/empresas/${id}`, {
        method: 'PUT',
        body: JSON.stringify({ ...dados, cnpj })
    })
}