import type {CandidatoInterface} from '../types/models'

const candidatos: CandidatoInterface[] = [
    {
        id: '1',
        nome: 'Ana Souza',
        email: 'ana.souza@email.com',
        cpf: '111.111.111-11',
        idade: 28,
        estado: 'SP',
        cep: '01000-000',
        descricao: 'Desenvolvedora backend apaixonada por dados.',
        competencias: ['Java', 'Spring Framework', 'SQL'],
        formacao: 'Ciência da Computação'
    },
    {
        id: '2',
        nome: 'Bruno Lima',
        email: 'bruno.lima@email.com',
        cpf: '222.222.222-22',
        idade: 24,
        estado: 'RJ',
        cep: '20000-000',
        descricao: 'Front-end entusiasta de acessibilidade web.',
        competencias: ['Angular', 'JavaScript', 'CSS'],
        formacao: 'Sistemas de Informação'
    },
    {
        id: '3',
        nome: 'Carla Dias',
        email: 'carla.dias@email.com',
        cpf: '333.333.333-33',
        idade: 31,
        estado: 'MG',
        cep: '30000-000',
        descricao: 'Especialista em automação de testes.',
        competencias: ['Python', 'Selenium', 'Java'],
        formacao: 'Engenharia de Software'
    },
    {
        id: '4',
        nome: 'Diego Alves',
        email: 'diego.alves@email.com',
        cpf: '444.444.444-44',
        idade: 26,
        estado: 'PR',
        cep: '80000-000',
        descricao: 'Full stack com foco em APIs REST.',
        competencias: ['Java', 'Spring Framework', 'Angular'],
        formacao: 'Análise e Desenvolvimento de Sistemas'
    },
    {
        id: '5',
        nome: 'Elisa Prado',
        email: 'elisa.prado@email.com',
        cpf: '555.555.555-55',
        idade: 22,
        estado: 'BA',
        cep: '40000-000',
        descricao: 'Recém-formada, buscando primeira oportunidade.',
        competencias: ['Python', 'SQL'],
        formacao: 'Ciência da Computação'
    }
]


export function listarTodosCandidatosRepository(): CandidatoInterface[] {
    return candidatos
}

export function adicionarCandidatoRepository(candidato: CandidatoInterface): void {
    candidatos.push(candidato)
}

export function removerCandidatoRepository(id: string): void {
    const index = candidatos.findIndex(c => c.id === id)
    if (index !== -1) {
        candidatos.splice(index, 1)
    }
}