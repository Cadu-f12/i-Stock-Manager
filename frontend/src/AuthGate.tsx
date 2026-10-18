import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import App from './App'
import { initialProducts, initialCategories } from './inventoryData'
import Icon from './Icon'
import { api, ApiError } from './api'
import type { User } from './usersData'

export default function AuthGate() {
  const [products, setProducts] = useState(initialProducts)
  const [categories, setCategories] = useState(initialCategories)
  const [user, setUser] = useState<User | null>(null)
  const [setup, setSetup] = useState(false)
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [available, setAvailable] = useState(false)
  const refresh = useCallback(async () => {
    try { setUser(await api<User>('/auth/me')) }
    catch (error) { if (error instanceof ApiError && error.status === 401) setUser(null); else throw error }
  }, [])
  const initialize = useCallback(async () => {
    setLoading(true); setError('')
    try {
      const status = await api<{ required: boolean }>('/auth/setup')
      setSetup(status.required)
      if (!status.required) await refresh()
      setAvailable(true)
    } catch (error) { setError((error as Error).message); setAvailable(false) }
    finally { setLoading(false) }
  }, [refresh])
  useEffect(() => { void initialize() }, [initialize])
  useEffect(() => {
    function expired() { setUser(null); setError('Sua sessão terminou. Entre novamente.') }
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
      if (setup) {
        await api('/auth/setup', { method: 'POST', body: JSON.stringify({ name: data.get('name'), email, password }) })
        setSetup(false)
      }
      await api('/auth/login', { method: 'POST', body: new URLSearchParams({ email, password }) })
      await refresh()
      window.location.hash = '#estoque'
    } catch (error) { setError((error as Error).message) }
    finally { setBusy(false) }
  }
  async function logout() {
    await api('/auth/logout', { method: 'POST' })
    setUser(null); setError(''); window.location.hash = '#login'
  }
  if (user) return <App currentUser={user} onLogout={logout} refreshSession={refresh} products={products} setProducts={setProducts} categories={categories} setCategories={setCategories} />
  return <div className="login-page"><section className="login-card" aria-labelledby="login-title">
    <div className="brand"><span className="brand-symbol"><Icon name="box" /></span><span>iStock<span className="brand-dot">.</span></span></div>
    <div className="eyebrow">GESTÃO DE ESTOQUE</div>
    <h1 id="login-title">{setup ? 'Configure seu acesso' : 'Bem-vindo de volta'}<span>.</span></h1>
    <p className="login-description">{setup ? 'Crie o primeiro administrador para começar. Os demais usuários serão cadastrados na gestão de usuários.' : 'Entre com o e-mail e a senha cadastrados na plataforma.'}</p>
    {loading ? <p role="status">Conectando à plataforma…</p> : available ? <form onSubmit={submit}>
      {setup && <label>Nome<input name="name" required maxLength={120} pattern=".*\S.*" autoComplete="name" autoFocus /></label>}
      <label>E-mail<input name="email" type="email" required maxLength={60} autoComplete="username" autoFocus={!setup} /></label>
      <label>Senha<input name="password" type="password" required minLength={setup ? 8 : undefined} maxLength={72} autoComplete={setup ? 'new-password' : 'current-password'} /></label>
      {setup && <p className="form-hint">Use uma senha com pelo menos 8 caracteres.</p>}
      {error && <p className="form-error" role="alert">{error}</p>}
      <button className="primary-button" disabled={busy}>{busy ? 'Aguarde…' : setup ? 'Criar administrador e entrar' : 'Entrar'}</button>
    </form> : <><p className="form-error" role="alert">{error}</p><button className="secondary-button" onClick={() => void initialize()}>Tentar novamente</button></>}
  </section></div>
}
