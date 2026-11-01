export const API_BASE_URL = 'http://localhost:8080'

export async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
    const headers = new Headers(options.headers)
    if (!headers.has('Accept')) {
        headers.set('Accept', 'application/json')
    }
    if (options.body !== undefined && !headers.has('Content-Type')) {
        headers.set('Content-Type', 'application/json')
    }

    const response = await fetch(`${API_BASE_URL}${path}`, {
        ...options,
        headers
    })
    const responseText = await response.text()
    let responseBody: unknown

    if (responseText) {
        try {
            responseBody = JSON.parse(responseText) as unknown
        } catch {
            if (response.ok) {
                throw new Error(`Resposta inválida do servidor (HTTP ${response.status}).`)
            }
        }
    }

    if (!response.ok) {
        const errorBody = responseBody as { erro?: unknown; message?: unknown } | undefined
        const errorMessage = typeof errorBody?.erro === 'string'
            ? errorBody.erro
            : typeof errorBody?.message === 'string'
                ? errorBody.message
                : `Falha na solicitação HTTP ${response.status}.`
        throw new Error(errorMessage)
    }

    if (response.status === 204 || !responseText) {
        return undefined as T
    }

    return responseBody as T
}
