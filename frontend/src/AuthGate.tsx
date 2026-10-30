import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import App from './App'
import type { Product } from './inventoryData'
import Icon from './Icon'
import { api, ApiError, getAccessToken, setAccessToken, clearAccessToken } from './api'
import type { User } from './usersData'

export default function AuthGate() {
  const [products, setProducts] = useState<Product[]>([])
  const [categories, setCategories] = useState<string[]>([])
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [available, setAvailable] = useState(false)
  const refresh = useCallback(async () => {
    if (!getAccessToken()) { setUser(null); return }
    try { setUser(await api<User>('/auth/me')) }
    catch (error) { if (error instanceof ApiError && error.status === 401) setUser(null); else throw error }
  }, [])
  const initialize = useCallback(async () => {
    setLoading(true); setError('')
    try {
      await refresh()
      setAvailable(true)
    } catch (error) { setError((error as Error).message); setAvailable(false) }
    finally { setLoading(false) }
  }, [refresh])
  useEffect(() => { void initialize() }, [initialize])
  useEffect(() => {
    function expired() { setUser(null); setProducts([]); setCategories([]); setError('Sua sessão terminou. Entre novamente.') }
    function focus() { if (user) void refresh().catch(() => {}) }
    window.addEventListener('session-expired', expired)
    window.addEventListener('focus', focus)
    const timer = window.setInterval(focus, 60000)
    return () => { window.removeEventListener('session-expired', expired); window.removeEventListener('focus', focus); clearInterval(timer) }
  }, [user, refresh])
  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    const email = String(data.get('email')).trim().toLowerCase()
    const password = String(data.get('password'))
    setBusy(true); setError('')
    try {
      const login = await api<{ accessToken: string; tokenType: string }>('/public/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) })
      setAccessToken(login.accessToken)
      await refresh()
      window.location.hash = '#estoque'
    } catch (error) { setError((error as Error).message) }
    finally { setBusy(false) }
  }
  async function logout() {
    clearAccessToken()
    setProducts([]); setCategories([])
    setUser(null); setError(''); window.location.hash = '#login'
  }
  if (user) return <App currentUser={user} onLogout={logout} refreshSession={refresh} products={products} setProducts={setProducts} categories={categories} setCategories={setCategories} />
  return <div className="login-page"><section className="login-card" aria-labelledby="login-title">
    <div className="brand"><span className="brand-symbol"><Icon name="box" /></span><span>iStock<span className="brand-dot">.</span></span></div>
    <div className="eyebrow">GESTÃO DE ESTOQUE</div>
    <h1 id="login-title">Bem-vindo de volta<span>.</span></h1>
    <p className="login-description">Entre com o e-mail e a senha cadastrados na plataforma.</p>
    {loading ? <p role="status">Conectando à plataforma…</p> : available ? <form onSubmit={submit}>
      <label>E-mail<input name="email" type="email" required maxLength={60} autoComplete="username" autoFocus /></label>
      <label>Senha<input name="password" type="password" required maxLength={72} autoComplete="current-password" /></label>
      {error && <p className="form-error" role="alert">{error}</p>}
      <button className="primary-button" disabled={busy}>{busy ? 'Aguarde…' : 'Entrar'}</button>
    </form> : <><p className="form-error" role="alert">{error}</p><button className="secondary-button" onClick={() => void initialize()}>Tentar novamente</button></>}
  </section></div>
}
