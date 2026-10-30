const TOKEN_KEY = 'istock.accessToken'
export const getAccessToken = () => sessionStorage.getItem(TOKEN_KEY)
export const setAccessToken = (token: string) => sessionStorage.setItem(TOKEN_KEY, token)
export const clearAccessToken = () => sessionStorage.removeItem(TOKEN_KEY)

export class ApiError extends Error {
  status: number
  constructor(status: number, message: string) { super(message); this.status = status }
}

export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers)
  const token = getAccessToken()
  const isLogin = path === '/public/auth/login'
  if (token && !isLogin) headers.set('Authorization', `Bearer ${token}`)
  if (options.body) headers.set('Content-Type', 'application/json')
  let response: Response
  try {
    response = await fetch(`/api${path}`, { ...options, headers, credentials: 'omit' })
  } catch {
    throw new Error('Não foi possível conectar ao servidor. Verifique se o backend está disponível.')
  }
  if ([502, 503, 504].includes(response.status)) {
    throw new ApiError(response.status, 'Backend indisponível. Confira se ele iniciou e se o banco de dados está acessível.')
  }
  if (!response.ok) {
    if (response.status === 401 && !isLogin && token === getAccessToken()) {
      clearAccessToken()
      window.dispatchEvent(new Event('session-expired'))
    }
    const body = await response.json().catch(() => ({})) as { message?: string }
    throw new ApiError(response.status, body.message || (response.status === 401 ? 'E-mail ou senha inválidos, ou sessão expirada.' : response.status === 403 ? 'Você não tem permissão para esta operação.' : 'Não foi possível concluir a operação.'))
  }
  const text = await response.text()
  return (text ? JSON.parse(text) : undefined) as T
}
