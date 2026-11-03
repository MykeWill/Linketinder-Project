import type {
    CandidatoDisponivelInterface,
    CandidatoInterface,
    MatchCandidatoInterface,
    PerfilCandidatoInterface,
    VagaAnonimaInterface
} from '../types/models'
import { request } from './api'
import {
    validarNome, validarEmail, validarCPF, validarIdade,
    validarEstado, validarCEP, validarDescricao, validarSenha
} from '../utils/validacoes'

export function listarTodosCandidatosService(): Promise<CandidatoDisponivelInterface[]> {
    return request<CandidatoDisponivelInterface[]>('/candidatos')
}

export function contarCandidatosPorCompetenciaService(): Promise<Record<string, number>> {
    return request<Record<string, number>>('/candidatos/competencias')
}

export function listarMatchesCandidatoService(candidatoId: number): Promise<MatchCandidatoInterface[]> {
    return request<MatchCandidatoInterface[]>(`/candidatos/${candidatoId}/matches`)
}

export function listarCurtidasCandidato(candidatoId: number): Promise<VagaAnonimaInterface[]> {
    return request<VagaAnonimaInterface[]>(`/candidatos/${candidatoId}/curtidas`)
}

export function curtirVaga(candidatoId: number, vagaId: number): Promise<{ match: boolean }> {
    return request<{ match: boolean }>(`/candidatos/${candidatoId}/curtir-vaga`, {
        method: 'POST',
        body: JSON.stringify({ vagaId })
    })
}

export function buscarPerfilCandidato(id: number): Promise<PerfilCandidatoInterface> {
    return request<PerfilCandidatoInterface>(`/candidatos/${id}`)
}

export async function atualizarPerfilCandidato(
    id: number,
    dados: Omit<CandidatoInterface, 'id' | 'senha'> & { senha?: string }
): Promise<void> {
    const cpf = dados.cpf.replace(/\D/g, '')
    const erros = [
        validarNome(dados.nome),
        validarEmail(dados.email),
        validarCPF(cpf),
        validarIdade(dados.idade),
        validarEstado(dados.estado),
        validarCEP(dados.cep),
        validarDescricao(dados.descricao),
        dados.senha ? validarSenha(dados.senha) : null
    ].filter(e => e !== null)
    if (erros.length > 0) {
        throw new Error(erros[0] as string)
    }

    await request<{ mensagem: string }>(`/candidatos/${id}`, {
        method: 'PUT',
        body: JSON.stringify({ ...dados, cpf })
    })
}

export async function cadastrarCandidatoService(dados: Omit<CandidatoInterface, 'id'>): Promise<number> {
    const cpf = dados.cpf.replace(/\D/g, '')
    const erros = [
        validarNome(dados.nome),
        validarEmail(dados.email),
        validarCPF(cpf),
        validarIdade(dados.idade),
        validarEstado(dados.estado),
        validarCEP(dados.cep),
        validarDescricao(dados.descricao),
        validarSenha(dados.senha)
    ].filter(e => e !== null)

    if (erros.length > 0) {
        throw new Error(erros[0] as string)
    }

    const resposta = await request<{ id: number }>('/candidatos', {
        method: 'POST',
        body: JSON.stringify({ ...dados, cpf })
    })
    return resposta.id
}
