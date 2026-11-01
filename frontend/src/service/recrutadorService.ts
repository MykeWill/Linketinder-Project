import type {
    CadastroRecrutadorInterface,
    CandidatoDisponivelInterface,
    MatchInterface,
    RecrutadorInterface
} from '../types/models'
import { request } from './api'

export async function cadastrarRecrutador(dados: CadastroRecrutadorInterface): Promise<number> {
    const resposta = await request<{ id: number }>('/recrutadores', {
        method: 'POST',
        body: JSON.stringify(dados)
    })
    return resposta.id
}

export function listarPorEmpresa(empresaId: number): Promise<RecrutadorInterface[]> {
    return request<RecrutadorInterface[]>(`/recrutadores?empresaId=${empresaId}`)
}

export async function removerRecrutador(id: number | string): Promise<void> {
    await request<void>(`/recrutadores/${id}`, { method: 'DELETE' })
}

export function listarCandidatos(recrutadorId: number): Promise<CandidatoDisponivelInterface[]> {
    return request<CandidatoDisponivelInterface[]>(`/recrutadores/${recrutadorId}/candidatos`)
}

export async function curtirCandidato(recrutadorId: number, candidatoId: number): Promise<void> {
    await request<{ sucesso: boolean }>(`/recrutadores/${recrutadorId}/curtidas`, {
        method: 'POST',
        body: JSON.stringify({ candidatoId })
    })
}

export function listarMatches(recrutadorId: number): Promise<MatchInterface[]> {
    return request<MatchInterface[]>(`/recrutadores/${recrutadorId}/matches`)
}
