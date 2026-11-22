import { useEffect, useState } from 'react'
import './App.css'
import './Select.css'
import Icon from './Icon'
import Users from './Users'
import Pastilles from './Pastilles'
import PastilleReport from './PastilleReport'
import Reports from './Reports'
import type { User } from './usersData'
import { api } from './api'
import type { Inventory } from './inventoryData'

const sections = [
  { hash: '#produtos', screen: 'pastilles', label: 'Pastilhas' },
  { hash: '#fabricantes', screen: 'manufacturer', label: 'Fabricantes' },
  { hash: '#fornecedores', screen: 'supplier', label: 'Fornecedores' },
] as const
function currentScreen() {
  switch (window.location.hash) {
    case '#usuarios': return 'users'
    case '#relatorios': return 'reports'
    case '#relatorios-produtos': return 'legacyReports'
    default: return sections.find(section => section.hash === window.location.hash)?.screen ?? 'pastilles'
  }
}
function App({ currentUser, onLogout, refreshSession }: {
  currentUser: User; onLogout: () => Promise<void>; refreshSession: () => Promise<void>
}) {
  const [screen, setScreen] = useState(currentScreen)
  useEffect(() => {
    function navigate() { setScreen(currentScreen()); window.scrollTo(0, 0) }
    window.addEventListener('hashchange', navigate)
    return () => window.removeEventListener('hashchange', navigate)
  }, [])
  const [users, setUsers] = useState<User[]>([])
  const [accountError, setAccountError] = useState('')
  const isAdmin = currentUser.role === 'Admin'
  useEffect(() => {
    if (isAdmin) api<User[]>('/admin/users').then(setUsers).catch(error => setAccountError(error.message))
    else setUsers([])
  }, [isAdmin])
  async function logout() {
    try { await onLogout() } catch (error) { setAccountError((error as Error).message) }
  }
  const [archive, setArchive] = useState<Inventory | null>(null)
  const [inventoryError, setInventoryError] = useState('')
  const [reload, setReload] = useState(0)
  const inventoryReady = archive !== null
  useEffect(() => {
    if (!isAdmin || screen !== 'legacyReports') return
    const controller = new AbortController()
    setInventoryError('')
    setArchive(null)
    api<Inventory>('/inventory', { signal: controller.signal }).then(setArchive)
      .catch(e => { if (!controller.signal.aborted) setInventoryError(e.message) })
    return () => controller.abort()
  }, [screen, isAdmin, reload])
  const inventorySection = sections.find(section => section.screen === screen) ?? (!isAdmin ? sections[0] : undefined)
  const legacyStatus = <div>{inventoryError ? <><p role="alert" className="form-error">{inventoryError}</p><button className="primary-button" onClick={() => setReload(value => value + 1)}>Tentar novamente</button></> : <p role="status">Carregando estoque…</p>}</div>
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <a className="brand" href="#main" aria-label="iStock — início"><span className="brand-symbol"><Icon name="box" /></span><span>iStock<span className="brand-dot">.</span></span></a>
        <span className="nav-caption">GERENCIAMENTO</span>
        <nav aria-label="Menu principal">
          <a className={`nav-link ${inventorySection ? "active" : ""}`} href="#produtos" aria-current={inventorySection ? "page" : undefined}><Icon name="box" /><span>Produtos</span></a>
          {isAdmin && <a className={`nav-link ${screen === 'users' ? 'active' : ''}`} href="#usuarios" aria-current={screen === 'users' ? 'page' : undefined}><Icon name="users" /><span>Usuários</span></a>}
          {isAdmin && <a className={`nav-link ${screen === 'reports' ? 'active' : ''}`} href="#relatorios" aria-current={screen === 'reports' ? 'page' : undefined}><Icon name="chart" /><span>Relatórios</span></a>}
          {isAdmin && <a className={`nav-link ${screen === "legacyReports" ? "active" : ""}`} href="#relatorios-produtos"><Icon name="chart" /><span>Relatórios anteriores</span></a>}
        </nav>
        <div className="sidebar-footer account-footer"><div><strong>{currentUser.name}</strong><small>{currentUser.role}</small></div><button className="secondary-button" onClick={() => void logout()}>Sair</button>{accountError && <p role="alert" className="form-error">{accountError}</p>}</div>
      </aside>
      {inventorySection && <main id="main">
        <header className="page-header"><h1>Gestão de produtos<span>.</span></h1></header>
        <nav className="catalog-sections" aria-label="Seções de produtos">{sections.map(section =>
          <a key={section.hash} href={section.hash} aria-current={inventorySection === section ? 'page' : undefined}>
            {section.label}
          </a>
        )}</nav>
        <Pastilles key={inventorySection.screen} tab={inventorySection.screen} />
      </main>}
      {isAdmin && <Users hidden={screen !== 'users'} users={users} setUsers={setUsers} refreshSession={refreshSession} />}
      {isAdmin && screen === 'reports' && <PastilleReport />}
      {isAdmin && screen === 'legacyReports' && (inventoryReady ? <Reports products={archive.products} categories={archive.categories} /> : <main>{legacyStatus}</main>)}
    </div>
  )
}
export default App
