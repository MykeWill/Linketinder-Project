import type { VagaAnonimaInterface, VagaInterface } from '../types/models'
import { request } from './api'

type DadosVagaInterface = {
    empresaId: number | string
    nome?: string
    titulo?: string
    descricao: string
    competencias: string[]
    recrutadorId?: number
}

export function listarVagasAnonimas(candidatoId: number): Promise<VagaAnonimaInterface[]> {
    return request<VagaAnonimaInterface[]>(`/vagas/anonimas?candidatoId=${candidatoId}`)
}

export function listarVagasPorEmpresaService(empresaId: number | string): Promise<VagaInterface[]> {
    return request<VagaInterface[]>(`/vagas?empresaId=${empresaId}`)
}

export function listarVagasVisiveisParaRecrutadorService(
    empresaId: number
): Promise<VagaInterface[]> {
    return request<VagaInterface[]>(`/vagas?empresaId=${empresaId}`)
}

export async function cadastrarVagaPorRecrutadorService(dados: {
    empresaId: number
    recrutadorId: number
    nome: string
    descricao: string
    local: string
    competencias: string[]
}): Promise<number> {
    if (!dados.nome.trim() || !dados.descricao.trim() || dados.competencias.length === 0) {
        throw new Error('Preencha título, descrição e ao menos uma competência.')
    }

    const resposta = await request<{ id: number }>('/vagas', {
        method: 'POST',
        body: JSON.stringify({
            empresaId: dados.empresaId,
            recrutadorId: dados.recrutadorId,
            nome: dados.nome,
            descricao: dados.descricao,
            local: dados.local,
            competencias: dados.competencias
        })
    })
    return resposta.id
}

export async function cadastrarVagaService(dados: DadosVagaInterface): Promise<VagaInterface> {
    const nome = dados.nome ?? dados.titulo
    if (!nome || !dados.descricao || dados.competencias.length === 0) {
        throw new Error('Preencha título, descrição e ao menos uma competência.')
    }

    const resposta = await request<{ id: number }>('/vagas', {
        method: 'POST',
        body: JSON.stringify({
            empresaId: Number(dados.empresaId),
            nome,
            descricao: dados.descricao,
            local: '',
            competencias: dados.competencias
        })
    })

    return {
        id: resposta.id,
        empresaId: dados.empresaId,
        nome,
        titulo: nome,
        descricao: dados.descricao,
        competencias: dados.competencias
    }
}

export async function removerVagaService(id: number | string): Promise<void> {
    await request<void>(`/vagas/${encodeURIComponent(String(id))}`, { method: 'DELETE' })
}

export async function atualizarVagaService(dados: {
    id: number | string
    nome: string
    descricao: string
    local: string
    competencias: string[]
}): Promise<void> {
    if (!dados.nome.trim() || !dados.descricao.trim() || dados.competencias.length === 0) {
        throw new Error('Preencha título, descrição e ao menos uma competência.')
    }
    const { id, ...atualizacao } = dados
    await request<{ mensagem: string }>(`/vagas/${encodeURIComponent(String(id))}`, {
        method: 'PUT',
        body: JSON.stringify(atualizacao)
    })
}