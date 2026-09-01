import type { CandidatoInterface } from '../types/models'
import * as candidatoRepository from '../repository/candidatoRepository'

export function listarTodosService(): CandidatoInterface[] {
    return candidatoRepository.listarTodosRepository()
}

export function cadastrarService(dados: Omit<CandidatoInterface, 'id'>): CandidatoInterface {
    if (!dados.nome || !dados.email || !dados.cpf) {
        throw new Error('Preencha nome, e-mail e CPF.')
    }

    const novoCandidatoService: CandidatoInterface = {
        id: crypto.randomUUID(),
        ...dados
    }

    candidatoRepository.adicionarRepository(novoCandidatoService)
    return novoCandidatoService
}

export function removerService(id: string): void {
    candidatoRepository.removerRepositpry(id)
}