import type { CandidatoInterface } from '../types/models'
import * as candidatoRepository from '../repository/candidatoRepository'
import {
    validarNome, validarEmail, validarCPF, validarIdade,
    validarEstado, validarCEP, validarDescricao, validarSenha
} from '../utils/validacoes'

export function listarTodosCandidatosService(): CandidatoInterface[] {
    return candidatoRepository.listarTodosCandidatosRepository()
}

export function cadastrarCandidatoService(dados: Omit<CandidatoInterface, 'id'>): CandidatoInterface {
    const erros = [
        validarNome(dados.nome),
        validarEmail(dados.email),
        validarCPF(dados.cpf),
        validarIdade(dados.idade),
        validarEstado(dados.estado),
        validarCEP(dados.cep),
        validarDescricao(dados.descricao),
        validarSenha(dados.senha)
    ].filter(e => e !== null)

    if (erros.length > 0) {
        throw new Error(erros[0] as string)
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

export function buscarCandidatoPorEmailService(email: string): CandidatoInterface | undefined {
    return candidatoRepository.buscarCandidatoPorEmailRepository(email)
}