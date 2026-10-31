export interface CandidatoInterface {
  id: number | string
  nome: string
  email: string
  cpf: string
  idade: number
  estado: string
  cep: string
  descricao: string
  competencias: string[]
  senha: string
}

export interface EmpresaInterface {
  id: number | string
  nome: string
  email: string
  cnpj: string
  pais: string
  estado: string
  cep: string
  descricao: string
  senha: string
}

export interface VagaInterface {
  id: number | string
  empresaId: number | string
  nome: string
  titulo?: string
  descricao: string
  competencias: string[]
  recrutadorId?: number
}

export interface RecrutadorInterface {
  id: number | string
  empresaId: number | string
  nome: string
  areaRecrutamento: string
  descricao: string
  email: string
  dataNascimento: string
}

export interface CadastroRecrutadorInterface {
  empresaId: number
  nome: string
  areaRecrutamento: string
  descricao: string
  email: string
  cpf: string
  dataNascimento: string
  senha: string
}

export interface CandidatoDisponivelInterface {
  id: number
  descricao: string
  competencias: string[]
}

export interface MatchInterface {
  empresaNome: string
  empresaDescricao: string
  empresaEmail: string
  recrutadorNome: string | null
  recrutadorAreaRecrutamento: string | null
  recrutadorEmail: string | null
  candidatoNome: string
  candidatoIdade: number
  candidatoEmail: string
  candidatoCompetencias: string[]
  candidatoDescricao: string
  candidatoEstado: string
}