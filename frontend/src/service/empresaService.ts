import type { EmpresaInterface } from '../types/models'
import * as empresaRepository from '../repository/empresaRepository'

export function listarTodasEmpresasService(): EmpresaInterface[] {
    return empresaRepository.listarTodasEmpresasRepository()
}

export function cadastrarEmpresaService(dados: Omit<EmpresaInterface, 'id'>): EmpresaInterface {
    if (!dados.nome || !dados.emailCorporativo || !dados.cnpj) {
        throw new Error('Preencha nome, e-mail corporativo e CNPJ.')
    }
    const novaEmpresaService: EmpresaInterface = {
        id: crypto.randomUUID(),
        ...dados
    }
    empresaRepository.adicionarEmpresaRepository(novaEmpresaService)
    return novaEmpresaService
}

export function removerEmpresaService(id: string): void {
    empresaRepository.removerEmpresaRepository(id)
}

export function buscarEmpresaPorEmailService(email: string): EmpresaInterface | undefined {
    return empresaRepository.buscarEmpresaPorEmailRepository(email)
}