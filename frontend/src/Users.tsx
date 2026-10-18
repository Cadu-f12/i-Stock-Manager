import { useEffect, useRef, useState } from 'react'
import type { Dispatch, FormEvent, SetStateAction } from 'react'
import Icon from './Icon'
import { api } from './api'
import type { User } from './usersData'

const normalize = (text: string) => text.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('pt-BR')

export default function Users({ hidden, users, setUsers, refreshSession }: { hidden: boolean; users: User[]; setUsers: Dispatch<SetStateAction<User[]>>; refreshSession: () => Promise<void> }) {
  const [query, setQuery] = useState('')
  const [status, setStatus] = useState('all')
  const [sort, setSort] = useState<'name' | 'email'>('name')
  const [descending, setDescending] = useState(false)
  const [page, setPage] = useState(1)
  const [size, setSize] = useState(50)
  const [notice, setNotice] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const [modal, setModal] = useState<{ mode: 'new' | 'edit' | 'view' | 'delete'; id?: number } | null>(null)
  const dialog = useRef<HTMLDialogElement>(null)
  const selected = users.find(user => user.id === modal?.id)
  useEffect(() => { setError(''); if (modal) dialog.current?.showModal() }, [modal])
  useEffect(() => { setPage(1) }, [query, status, sort, descending, size])
  useEffect(() => { if (hidden) setModal(null) }, [hidden])
  const filtered = users.filter(user => normalize(`${user.name} ${user.email} ${user.id}`).includes(normalize(query.trim())) && (status === 'all' || user.active === (status === 'active')))
    .sort((a, b) => a[sort].localeCompare(b[sort], 'pt-BR') * (descending ? -1 : 1))
  const pages = Math.max(1, Math.ceil(filtered.length / size))
  const currentPage = Math.min(page, pages)
  const start = (currentPage - 1) * size
  const hasFilters = Boolean(query || status !== 'all')
  function clearFilters() { setQuery(''); setStatus('all') }
  async function save(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = event.currentTarget
    const data = new FormData(form)
    const name = String(data.get('name')).trim()
    const email = String(data.get('email')).trim().toLowerCase()
    const emailInput = form.elements.namedItem('email') as HTMLInputElement
    if (users.some(user => user.email.toLowerCase() === email && user.id !== selected?.id)) {
      emailInput.setCustomValidity('Este e-mail já está cadastrado.')
      emailInput.reportValidity()
      return
    }
    if (!name) return
    if (busy) return
    setBusy(true); setError('')
    try {
      const user = await api<User>(selected ? `/admin/users/${selected.id}` : '/admin/users', {
        method: selected ? 'PUT' : 'POST',
        body: JSON.stringify({ name, email, active: data.get('status') === 'active', role: data.get('role'), password: data.get('password') }),
      })
      setUsers(current => selected ? current.map(item => item.id === user.id ? user : item) : [...current, user])
      setNotice(`${name}: ${selected ? 'alterações salvas' : 'usuário cadastrado'}.`)
      setModal(null)
      await refreshSession()
    } catch (error) { setError((error as Error).message) }
    finally { setBusy(false) }
  }
  async function remove() {
    if (!selected || busy) return
    setBusy(true); setError('')
    try {
      await api(`/admin/users/${selected.id}`, { method: 'DELETE' })
      setUsers(current => current.filter(user => user.id !== selected.id))
      setNotice(`${selected.name}: usuário excluído.`)
      setModal(null)
    } catch (error) { setError((error as Error).message) }
    finally { setBusy(false) }
  }
  return <main id="users-main" hidden={hidden}>
    <header className="page-header"><div><div className="eyebrow">GERENCIAMENTO <span>/</span> USUÁRIOS</div><h1>Gestão de usuários<span>.</span></h1><p>Organize os usuários da sua plataforma.</p></div><button className="primary-button" onClick={() => setModal({ mode: 'new' })}><Icon name="plus" />Novo usuário</button></header>
    <div className="inventory-summary"><span><span className="status-dot" />Visão geral dos usuários</span><span><strong>{users.length}</strong> cadastrados <span className="summary-divider">/</span> <strong>{users.filter(user => user.active).length}</strong> ativos</span></div>
    <section className="filter-panel" aria-label="Filtros de usuários">
      <div className="filter-topline"><span><Icon name="filter" />Filtrar usuários</span><button className="clear-button" disabled={!hasFilters} onClick={clearFilters}>Limpar filtros<Icon name="close" /></button></div>
      <div className="filter-toolbar"><label className="search-field"><Icon name="search" /><input type="search" aria-label="Pesquisar usuários" placeholder="Pesquisar por nome, e-mail ou ID..." value={query} onChange={event => setQuery(event.target.value)} /></label><label className="toolbar-field"><span>Status</span><select value={status} onChange={event => setStatus(event.target.value)}><option value="all">Todos</option><option value="active">Ativos</option><option value="inactive">Inativos</option></select></label></div>
    </section>
    <section className="products-section" aria-labelledby="users-title">
      <div className="table-toolbar"><div><h2 id="users-title">Usuários da plataforma</h2><span className="result-count" role="status">{filtered.length} resultados</span></div><div className="sort-controls"><label htmlFor="users-sort">Ordenar por</label><select id="users-sort" value={sort} onChange={event => setSort(event.target.value as 'name' | 'email')}><option value="name">Nome</option><option value="email">E-mail</option></select><button className="sort-direction" aria-label={descending ? 'Ordenar usuários em ordem crescente' : 'Ordenar usuários em ordem decrescente'} onClick={() => setDescending(!descending)}>{descending ? '↓' : '↑'}</button></div></div>
      <div className="table-scroll"><table><thead><tr><th scope="col">ID</th><th scope="col">Nome</th><th scope="col">E-mail</th><th scope="col">Papel</th><th scope="col">Status</th><th scope="col" className="actions-column">Operações</th></tr></thead><tbody>{filtered.slice(start, start + size).map(user => <tr key={user.id}>
        <td className="product-id">#{String(user.id).padStart(3, '0')}</td><td className="product-name">{user.name}</td><td>{user.email}</td><td><span className="category-tag">{user.role}</span></td><td><span className={`user-status ${user.active ? 'is-active' : ''}`}>{user.active ? 'Ativo' : 'Inativo'}</span></td>
        <td><div className="row-actions"><button aria-label={`Visualizar ${user.name}`} title="Visualizar usuário" onClick={() => setModal({ mode: 'view', id: user.id })}><Icon name="eye" /></button><button aria-label={`Editar ${user.name}`} title="Editar usuário" onClick={() => setModal({ mode: 'edit', id: user.id })}><Icon name="edit" /></button><button aria-label={`Excluir ${user.name}`} title="Excluir usuário" onClick={() => setModal({ mode: 'delete', id: user.id })}><Icon name="trash" /></button></div></td>
      </tr>)}</tbody></table>{!filtered.length && <div className="empty-state"><Icon name="users" /><h3>Nenhum usuário encontrado</h3><p>Tente outro termo ou remova os filtros.</p><button className="secondary-button" onClick={clearFilters}>Limpar filtros</button></div>}</div>
      <footer className="table-footer"><span role="status">Exibindo {filtered.length ? start + 1 : 0}–{Math.min(start + size, filtered.length)} de {filtered.length} usuários</span><nav className="pagination" aria-label="Paginação dos usuários"><label htmlFor="users-page-size">Por página</label><select id="users-page-size" value={size} onChange={event => setSize(Number(event.target.value))}>{[50, 100, 200].map(value => <option key={value}>{value}</option>)}</select><button className="secondary-button" disabled={currentPage === 1} onClick={() => setPage(currentPage - 1)}>Anterior</button><span>Página {currentPage} de {pages}</span><button className="secondary-button" disabled={currentPage === pages} onClick={() => setPage(currentPage + 1)}>Próxima</button></nav></footer>
    </section>
    <p className="save-notice" role="status">{notice}</p>
    <p className="users-demo-note">Usuários cadastrados podem acessar a plataforma com e-mail e senha enquanto estiverem ativos.</p>
    <footer className="page-footer"><span>iStock <span>/</span> Organização que faz a diferença.</span><span>GESTÃO DE USUÁRIOS</span></footer>
    {modal && <dialog ref={dialog} onCancel={() => setModal(null)} onClose={() => setModal(null)} aria-labelledby="user-dialog-title"><div className="dialog-heading"><h2 id="user-dialog-title">{modal.mode === 'new' ? 'Novo usuário' : modal.mode === 'edit' ? 'Editar usuário' : modal.mode === 'delete' ? 'Excluir usuário' : 'Detalhes do usuário'}</h2><button className="icon-button" aria-label="Fechar" onClick={() => setModal(null)}><Icon name="close" /></button></div>
      {modal.mode === 'delete' && selected ? <><p>Excluir o usuário <strong>{selected.name}</strong>?</p><div className="dialog-actions"><button className="secondary-button" autoFocus onClick={() => setModal(null)}>Cancelar</button><button className="danger-button" disabled={busy} onClick={() => void remove()}>Excluir usuário</button></div></> : modal.mode === 'view' && selected ? <><dl className="product-details"><dt>Nome</dt><dd>{selected.name}</dd><dt>E-mail</dt><dd>{selected.email}</dd><dt>Papel</dt><dd>{selected.role}</dd><dt>Status</dt><dd>{selected.active ? 'Ativo' : 'Inativo'}</dd></dl><button className="primary-button" onClick={() => setModal({ mode: 'edit', id: selected.id })}><Icon name="edit" />Editar usuário</button></> : <form key={`${modal.mode}-${modal.id}`} onSubmit={save}>
        <label>Nome<input name="name" required maxLength={120} pattern=".*\S.*" title="Informe um nome para o usuário" defaultValue={selected?.name} autoFocus /></label>
        <label>E-mail<input name="email" type="email" required maxLength={60} defaultValue={selected?.email} onChange={event => event.currentTarget.setCustomValidity('')} /></label>
        <label>{selected ? 'Nova senha (opcional)' : 'Senha'}<input name="password" type="password" autoComplete="new-password" required={!selected} minLength={8} maxLength={72} /></label>
        <label>Papel<select name="role" defaultValue={selected?.role ?? 'Operator'}><option value="Admin">Admin</option><option value="Operator">Operator</option></select></label>
        <label>Status<select name="status" defaultValue={selected?.active === false ? 'inactive' : 'active'}><option value="active">Ativo</option><option value="inactive">Inativo</option></select></label>
        <p className="form-hint">Use ao menos 8 caracteres na senha. Na edição, deixe em branco para manter a senha atual.</p>
        <div className="dialog-actions"><button type="button" className="secondary-button" onClick={() => setModal(null)}>Cancelar</button><button type="submit" className="primary-button" disabled={busy}>{modal.mode === 'new' ? 'Cadastrar usuário' : 'Salvar alterações'}</button></div>
      </form>}
      {error && <p role="alert" className="form-error">{error}</p>}
    </dialog>}
  </main>
}
