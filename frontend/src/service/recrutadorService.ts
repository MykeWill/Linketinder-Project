import type {
    CadastroRecrutadorInterface,
    CandidatoDisponivelInterface,
    MatchInterface,
    PerfilRecrutadorInterface,
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

export function buscarRecrutador(id: number | string): Promise<PerfilRecrutadorInterface> {
    return request<PerfilRecrutadorInterface>(`/recrutadores/${encodeURIComponent(String(id))}`)
}

export async function atualizarRecrutador(
    id: number | string,
    dados: Omit<PerfilRecrutadorInterface, 'id'> & { senha?: string }
): Promise<void> {
    await request<{ mensagem: string }>(`/recrutadores/${encodeURIComponent(String(id))}`, {
        method: 'PUT',
        body: JSON.stringify(dados)
    })
}

export function listarCandidatos(recrutadorId: number): Promise<CandidatoDisponivelInterface[]> {
    return request<CandidatoDisponivelInterface[]>(`/recrutadores/${recrutadorId}/candidatos`)
}

export function curtirCandidato(
    recrutadorId: number,
    candidatoId: number,
    vagaId: number
): Promise<{ match: boolean }> {
    return request<{ match: boolean }>(`/recrutadores/${recrutadorId}/curtidas`, {
        method: 'POST',
        body: JSON.stringify({ candidatoId, vagaId })
    })
}

export function listarMatches(recrutadorId: number): Promise<MatchInterface[]> {
    return request<MatchInterface[]>(`/recrutadores/${recrutadorId}/matches`)
}
