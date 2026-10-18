export class ApiError extends Error {
  status: number
  constructor(status: number, message: string) { super(message); this.status = status }
}

export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers)
  try {
    if (options.method && options.method !== 'GET') {
      const csrf = await fetch('/api/auth/csrf', { credentials: 'same-origin' })
      if (!csrf.ok) throw new ApiError(csrf.status, 'Não foi possível validar a sessão. Tente novamente.')
      const token = await csrf.json() as { headerName: string; token: string }
      headers.set(token.headerName, token.token)
    }
    if (options.body && !(options.body instanceof URLSearchParams)) headers.set('Content-Type', 'application/json')
    const response = await fetch(`/api${path}`, { ...options, headers, credentials: 'same-origin' })
    if (!response.ok) {
      if (response.status === 401 && path !== '/auth/login' && path !== '/auth/me') window.dispatchEvent(new Event('session-expired'))
      const body = await response.json().catch(() => ({})) as { message?: string }
      throw new ApiError(response.status, body.message || (response.status === 401 ? 'E-mail ou senha inválidos, ou conta inativa.' : response.status === 403 ? 'Acesso não autorizado. Atualize a página e tente novamente.' : 'Não foi possível concluir a operação.'))
    }
    const text = await response.text()
    return (text ? JSON.parse(text) : undefined) as T
  } catch (error) {
    if (error instanceof ApiError) throw error
    throw new Error('Não foi possível conectar ao servidor. Verifique se o backend está disponível.')
  }
}
