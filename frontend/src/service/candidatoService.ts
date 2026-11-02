import type {
    CandidatoDisponivelInterface,
    CandidatoInterface,
    MatchCandidatoInterface,
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

export function contarCandidatosPorCompetenciaService(
    candidatos: CandidatoDisponivelInterface[]
): Record<string, number> {
    const contagem: Record<string, number> = {}

    candidatos.forEach(candidato => {
        candidato.competencias.forEach(competencia => {
            contagem[competencia] = (contagem[competencia] ?? 0) + 1
        })
    })

    return contagem
}