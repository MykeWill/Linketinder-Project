import type { CandidatoInterface } from '../types/models'
import * as candidatoRepository from '../repository/candidatoRepository'

export function listarTodosCandidatosService(): CandidatoInterface[] {
    return candidatoRepository.listarTodosCandidatosRepository()
}

export function cadastrarCandidatoService(dados: Omit<CandidatoInterface, 'id'>): CandidatoInterface {
    if (!dados.nome || !dados.email || !dados.cpf) {
        throw new Error('Preencha nome, e-mail e CPF.')
    }

    const novoCandidatoService: CandidatoInterface = {
        id: crypto.randomUUID(),
        ...dados
    }

    candidatoRepository.adicionarCandidatoRepository(novoCandidatoService)
    return novoCandidatoService
}

export function removerCandidatoService(id: string): void {
    candidatoRepository.removerCandidatoRepository(id)
}

export function contarCandidatosPorCompetenciaService(): Record<string, number> {
    const candidatos = candidatoRepository.listarTodosCandidatosRepository()
    const contagem: Record<string, number> = {}

    candidatos.forEach(candidato => {
        candidato.competencias.forEach(competencia => {
            contagem[competencia] = (contagem[competencia] ?? 0) + 1
        })
    })

    return contagem
}