import type { VagaInterface } from '../types/models'

const CHAVE_STORAGE = 'linketinder:vagas'

const vagasIniciais: VagaInterface[] = [
    {
        id: '1',
        empresaId: '1',
        nome: 'Desenvolvedor Backend Java',
        titulo: 'Desenvolvedor Backend Java',
        descricao: 'Vaga para atuar com Spring Framework em sistemas de gestão.',
        competencias: ['Java', 'Spring Framework']
    },
    {
        id: '2',
        empresaId: '2',
        nome: 'Desenvolvedor Frontend Angular',
        titulo: 'Desenvolvedor Frontend Angular',
        descricao: 'Vaga para atuar em sistemas internos de gestão de lazer.',
        competencias: ['Angular', 'JavaScript']
    },
    {
        id: '3',
        empresaId: '3',
        nome: 'Engenheiro de Dados Python',
        titulo: 'Engenheiro de Dados Python',
        descricao: 'Vaga para atuar com pipelines de dados em nuvem.',
        competencias: ['Python', 'SQL']
    },
    {
        id: '4',
        empresaId: '4',
        nome: 'Analista de Testes Automatizados',
        titulo: 'Analista de Testes Automatizados',
        descricao: 'Vaga para atuar com automação de testes end-to-end.',
        competencias: ['Python', 'Selenium']
    },
    {
        id: '5',
        empresaId: '5',
        nome: 'Desenvolvedor Full Stack',
        titulo: 'Desenvolvedor Full Stack',
        descricao: 'Vaga para atuar com Java, Angular e SQL em projetos de clientes.',
        competencias: ['Java', 'Angular', 'SQL']
    }
]

function carregarVagas(): VagaInterface[] {
    const dados = localStorage.getItem(CHAVE_STORAGE)
    return dados
        ? (JSON.parse(dados) as VagaInterface[]).map(vaga => ({
            ...vaga,
            nome: vaga.nome ?? vaga.titulo ?? ''
        }))
        : vagasIniciais
}

function salvarVagas(vagas: VagaInterface[]): void {
    localStorage.setItem(CHAVE_STORAGE, JSON.stringify(vagas))
}

let vagas: VagaInterface[] = carregarVagas()

export function listarTodasVagasRepository(): VagaInterface[] {
    return vagas
}

export function listarVagasPorEmpresaRepository(empresaId: number | string): VagaInterface[] {
    return vagas.filter(vaga => String(vaga.empresaId) === String(empresaId))
}

export function adicionarVagaRepository(vaga: VagaInterface): void {
    vagas.push(vaga)
    salvarVagas(vagas)
}

export function removerVagaRepository(id: number | string): void {
    const index = vagas.findIndex(v => String(v.id) === String(id))
    if (index !== -1) {
        vagas.splice(index, 1)
        salvarVagas(vagas)
    }
}