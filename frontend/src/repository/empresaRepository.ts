import type { EmpresaInterface } from '../types/models'

const CHAVE_STORAGE = 'linketinder:empresas'

const empresasIniciais: EmpresaInterface[] = [
    {
        id: '1',
        nome: 'Arroz-Gostoso',
        emailCorporativo: 'contato@arrozgostoso.com',
        cnpj: '11.111.111/0001-11',
        pais: 'Brasil',
        estado: 'SP',
        cep: '01000-000',
        descricao: 'Empresa do ramo alimentício.'
    },
    {
        id: '2',
        nome: 'Império do Boliche',
        emailCorporativo: 'rh@imperiodoboliche.com',
        cnpj: '22.222.222/0001-22',
        pais: 'Brasil',
        estado: 'RJ',
        cep: '20000-000',
        descricao: 'Rede de entretenimento e lazer.'
    },
    {
        id: '3',
        nome: 'TechNova',
        emailCorporativo: 'vagas@technova.com',
        cnpj: '33.333.333/0001-33',
        pais: 'Brasil',
        estado: 'MG',
        cep: '30000-000',
        descricao: 'Startup de soluções em nuvem.'
    },
    {
        id: '4',
        nome: 'QualiTest Corp',
        emailCorporativo: 'recrutamento@qualitest.com',
        cnpj: '44.444.444/0001-44',
        pais: 'Brasil',
        estado: 'PR',
        cep: '80000-000',
        descricao: 'Empresa especializada em qualidade de software.'
    },
    {
        id: '5',
        nome: 'DevBridge',
        emailCorporativo: 'contato@devbridge.com',
        cnpj: '55.555.555/0001-55',
        pais: 'Brasil',
        estado: 'BA',
        cep: '40000-000',
        descricao: 'Consultoria em desenvolvimento full stack.'
    }
]

function carregarEmpresas(): EmpresaInterface[] {
    const dados = localStorage.getItem(CHAVE_STORAGE)
    return dados ? JSON.parse(dados) : empresasIniciais
}

function salvarEmpresas(empresas: EmpresaInterface[]): void {
    localStorage.setItem(CHAVE_STORAGE, JSON.stringify(empresas))
}

let empresas: EmpresaInterface[] = carregarEmpresas()

export function listarTodasEmpresasRepository(): EmpresaInterface[] {
    return empresas
}

export function adicionarEmpresaRepository(empresa: EmpresaInterface): void {
    empresas.push(empresa)
    salvarEmpresas(empresas)
}

export function removerEmpresaRepository(id: string): void {
    const index = empresas.findIndex(e => e.id === id)
    if (index !== -1) {
        empresas.splice(index, 1)
        salvarEmpresas(empresas)
    }
}

export function buscarEmpresaPorEmailRepository(email: string): EmpresaInterface | undefined {
    return empresas.find(e => e.emailCorporativo === email)
}