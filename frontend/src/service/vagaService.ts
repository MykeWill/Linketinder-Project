import type { VagaInterface } from '../types/models'
import * as vagaRepository from '../repository/vagaRepository'

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

export function listarVagasPorEmpresaService(empresaId: number | string): VagaInterface[] {
    return vagaRepository.listarVagasPorEmpresaRepository(empresaId)
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