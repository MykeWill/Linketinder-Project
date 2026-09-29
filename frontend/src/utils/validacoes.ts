export function validarNome(nome: string): string | null {
    const regex = /^[A-Za-zÀ-ÿ]+(?:\s+[A-Za-zÀ-ÿ]+)+$/
    if (!regex.test(nome.trim())) return 'Nome inválido. Informe nome e sobrenome (apenas letras).'
    return null
}

export function validarEmail(email: string): string | null {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!regex.test(email.trim())) return 'E-mail inválido.'
    return null
}

export function validarCPF(cpf: string): string | null {
    const regex = /^\d{3}\.?\d{3}\.?\d{3}-?\d{2}$/
    if (!regex.test(cpf.trim())) return 'CPF inválido. Use o formato 000.000.000-00.'
    return null
}

export function validarIdade(idade: number): string | null {
    if (!Number.isInteger(idade) || idade < 16 || idade > 120) {
        return 'Idade inválida. Deve ser um número entre 16 e 120.'
    }
    return null
}

export function validarEstado(estado: string): string | null {
    const regex = /^[A-Za-zÀ-ÿ]{2,}$/
    if (!regex.test(estado.trim())) return 'Estado inválido. Informe apenas letras (ex: SP).'
    return null
}

export function validarCEP(cep: string): string | null {
    const regex = /^\d{5}-?\d{3}$/
    if (!regex.test(cep.trim())) return 'CEP inválido. Use o formato 00000-000.'
    return null
}

export function validarCNPJ(cnpj: string): string | null {
    const regex = /^\d{2}\.?\d{3}\.?\d{3}\/?\d{4}-?\d{2}$/
    if (!regex.test(cnpj.trim())) return 'CNPJ inválido. Use o formato 00.000.000/0000-00.'
    return null
}

export function validarDescricao(descricao: string): string | null {
    if (descricao.trim().length < 10) return 'Descrição muito curta. Mínimo 10 caracteres.'
    return null
}

export function validarSenha(senha: string): string | null {
    if (senha.length < 6) return 'Senha inválida. Mínimo 6 caracteres.'
    return null
}