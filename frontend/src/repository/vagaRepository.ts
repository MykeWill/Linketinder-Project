import type { VagaInterface } from '../types/models'

const vagas: VagaInterface[] = [
    {
        id: '1',
        empresaId: '1',
        titulo: 'Desenvolvedor Backend Java',
        descricao: 'Vaga para atuar com Spring Framework em sistemas de gestão.',
        competencias: ['Java', 'Spring Framework']
    },
    {
        id: '2',
        empresaId: '2',
        titulo: 'Desenvolvedor Frontend Angular',
        descricao: 'Vaga para atuar em sistemas internos de gestão de lazer.',
        competencias: ['Angular', 'JavaScript']
    },
    {
        id: '3',
        empresaId: '3',
        titulo: 'Engenheiro de Dados Python',
        descricao: 'Vaga para atuar com pipelines de dados em nuvem.',
        competencias: ['Python', 'SQL']
    },
    {
        id: '4',
        empresaId: '4',
        titulo: 'Analista de Testes Automatizados',
        descricao: 'Vaga para atuar com automação de testes end-to-end.',
        competencias: ['Python', 'Selenium']
    },
    {
        id: '5',
        empresaId: '5',
        titulo: 'Desenvolvedor Full Stack',
        descricao: 'Vaga para atuar com Java, Angular e SQL em projetos de clientes.',
        competencias: ['Java', 'Angular', 'SQL']
    }
]

export function listarTodasVagasRepository(): VagaInterface[] {
    return vagas
}

export function listarVagasPorEmpresaRepository(empresaId: string): VagaInterface[] {
    return vagas.filter(vaga => vaga.empresaId === empresaId)
}

export function adicionarVagaRepository(vaga: VagaInterface): void {
    vagas.push(vaga)
}

export function removerVagaRepository(id: string): void {
    const index = vagas.findIndex(v => v.id === id)
    if (index !== -1) {
        vagas.splice(index, 1)
    }
}