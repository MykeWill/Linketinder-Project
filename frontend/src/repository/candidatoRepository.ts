import type {CandidatoInterface} from '../types/models'

const candidatos: CandidatoInterface[] = []

export function listarTodosRepository(): CandidatoInterface[] {
    return candidatos
}

export function adicionarRepository(candidato: CandidatoInterface): void {
    candidatos.push(candidato)
}

export function removerRepository(id: string): void {
    const index = candidatos.findIndex(c => c.id === id)
    if (index !== -1) {
        candidatos.splice(index, 1)
    }
}