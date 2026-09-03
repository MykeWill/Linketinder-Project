import type { VagaInterface } from '../types/models'
import * as vagaRepository from '../repository/vagaRepository'

export function listarTodasVagasService(): VagaInterface[] {
    return vagaRepository.listarTodasVagasRepository()
}

export function listarVagasPorEmpresaService(empresaId: string): VagaInterface[] {
    return vagaRepository.listarVagasPorEmpresaRepository(empresaId)
}

export function cadastrarVagaService(dados: Omit<VagaInterface, 'id'>): VagaInterface {
    if (!dados.titulo || !dados.descricao || dados.competencias.length === 0) {
        throw new Error('Preencha título, descrição e ao menos uma competência.')
    }

    const novaVagaService: VagaInterface = {
        id: crypto.randomUUID(),
        ...dados
    }

    vagaRepository.adicionarVagaRepository(novaVagaService)
    return novaVagaService
}

export function removerVagaService(id: string): void {
    vagaRepository.removerVagaRepository(id)
}