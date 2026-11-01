import type { VagaAnonimaInterface, VagaInterface } from '../types/models'
import * as vagaRepository from '../repository/vagaRepository'
import { request } from './api'

type DadosVagaInterface = {
    empresaId: number | string
    nome?: string
    titulo?: string
    descricao: string
    competencias: string[]
    recrutadorId?: number
}

export function listarTodasVagasService(): VagaInterface[] {
    return vagaRepository.listarTodasVagasRepository()
}

export function listarVagasAnonimas(): Promise<VagaAnonimaInterface[]> {
    return request<VagaAnonimaInterface[]>('/vagas/anonimas')
}

export function listarVagasPorEmpresaService(empresaId: number | string): VagaInterface[] {
    return vagaRepository.listarVagasPorEmpresaRepository(empresaId)
}

export function listarVagasVisiveisParaRecrutadorService(
    empresaId: number,
    recrutadorId: number
): Promise<VagaInterface[]> {
    return request<VagaInterface[]>(
        `/vagas?empresaId=${empresaId}&recrutadorId=${recrutadorId}`
    )
}

export async function cadastrarVagaPorRecrutadorService(dados: {
    empresaId: number
    recrutadorId: number
    nome: string
    descricao: string
    competencias: string[]
}): Promise<number> {
    if (!dados.nome.trim() || !dados.descricao.trim() || dados.competencias.length === 0) {
        throw new Error('Preencha título, descrição e ao menos uma competência.')
    }

    const resposta = await request<{ id: number }>('/vagas', {
        method: 'POST',
        body: JSON.stringify({ ...dados, local: '' })
    })
    return resposta.id
}

export function cadastrarVagaService(dados: DadosVagaInterface): VagaInterface {
    const nome = dados.nome ?? dados.titulo
    if (!nome || !dados.descricao || dados.competencias.length === 0) {
        throw new Error('Preencha título, descrição e ao menos uma competência.')
    }

    const novaVagaService: VagaInterface = {
        id: crypto.randomUUID(),
        ...dados,
        nome,
        titulo: nome
    }

    vagaRepository.adicionarVagaRepository(novaVagaService)
    return novaVagaService
}

export function removerVagaService(id: number | string): void {
    vagaRepository.removerVagaRepository(id)
}