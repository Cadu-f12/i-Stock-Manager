import { useEffect, useRef, useState } from 'react'
import type { FormEvent } from 'react'
import { domainApi } from './api'
import Icon from './Icon'

type Item = { id: number; sku_code: string; description: string | null; minimum_stock_level: number; current_balance: number; manufacturer: string | null; suppliers: string[] }
type Partner = { id: number; name: string; contact: string; pastilles: Item[]; isActive?: boolean; is_active?: boolean }
type Reason = { id: number; description: string; type: 'IN' | 'OUT' }
type Movement = { id: number; date_time: string; quantity: number; reason: Reason; user: { name: string } }
type Detail = { manufacturer: Partner | null; suppliers: Partner[] }
type Catalog = 'manufacturer' | 'supplier'
type Action = { kind: 'new' } | { kind: 'edit' | 'history'; item: Item } | { kind: 'move'; item: Item; direction: Reason['type'] } | { kind: 'partner'; catalog: Catalog; partner?: Partner }

function PastilleDialog({ action, close, saved }: { action: Action; close: () => void; saved: (message: string) => void }) {
  const dialog = useRef<HTMLDialogElement>(null)
  const submitting = useRef(false)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [ready, setReady] = useState(false)
  const [reasons, setReasons] = useState<Reason[]>([])
  const [manufacturers, setManufacturers] = useState<Partner[]>([])
  const [suppliers, setSuppliers] = useState<Partner[]>([])
  const [detail, setDetail] = useState<Detail | null>(null)
  const [history, setHistory] = useState<Movement[]>([])
  const [reasonId, setReasonId] = useState('')
  const reasonMenu = useRef<HTMLDetailsElement>(null)
  const [attempt, setAttempt] = useState(0)
  const item = 'item' in action ? action.item : null
  const reason = reasons.find(entry => entry.id === Number(reasonId))
  const maximum = reason?.type === 'OUT' ? item?.current_balance : 999999999 - (item?.current_balance ?? 0)
  useEffect(() => { dialog.current?.showModal() }, [])
  useEffect(() => {
    const controller = new AbortController()
    const options = { signal: controller.signal }
    async function load() {
      setReady(false)
      setError('')
      if (action.kind === 'move') {
        const active = await domainApi<Reason[]>('/movement/reasons', options)
        setReasons(active.filter(entry => entry.type === action.direction))
      }
      if (action.kind === 'edit' || action.kind === 'new') {
        const [makers, vendors] = await Promise.all([
          domainApi<Partner[]>('/manufacturer', options),
          domainApi<Partner[]>('/supplier', options),
        ])
        setManufacturers(makers)
        setSuppliers(vendors)
      }
      if (action.kind === 'edit' || action.kind === 'history')
        setDetail(await domainApi<Detail>(`/pastilles/${action.item.id}`, options))
      if (action.kind === 'history')
        setHistory(await domainApi<Movement[]>(`/movement?pastille_id=${action.item.id}`, options))
      if (!controller.signal.aborted) setReady(true)
    }
    load().catch(e => { if (!controller.signal.aborted) setError(e.message) })
    return () => controller.abort()
  }, [action, attempt])
  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (submitting.current || !ready) return
    const form = new FormData(event.currentTarget)
    const value = (key: string) => String(form.get(key) ?? '').trim()
    submitting.current = true
    setBusy(true)
    setError('')
    try {
      if (action.kind === 'move') {
        await domainApi('/movement', { method: 'POST', body: JSON.stringify({ pastille: action.item.id, reason_movement_id: Number(reasonId), quantity: Number(value('quantity')) }) })
        saved('Movimentação registrada.')
      } else if (action.kind === 'new' || action.kind === 'edit') {
        const fields = { supplier_ids: form.getAll('supplier').map(Number), description: value('description'), manufacturer_id: Number(value('manufacturer')), minimum_stock_level: Number(value('minimum')) }
        if (action.kind === 'new')
          await domainApi('/pastilles', { method: 'POST', body: JSON.stringify({ ...fields, sku_code: value('sku') }) })
        else await domainApi(`/pastilles/${action.item.id}`, { method: 'PUT', body: JSON.stringify(fields) })
        saved('Pastilha salva.')
      } else if (action.kind === 'partner') {
        const { catalog, partner } = action
        const resource = catalog === 'supplier' ? 'Supplier' : 'Manufacturer'
        const path = partner ? `update${resource}/${partner.id}` : `create${resource}`
        await domainApi(`/${catalog}/${path}`, { method: partner ? 'PUT' : 'POST', body: JSON.stringify({ name: value('name'), contact: value('contact'), pastilles: catalog === 'supplier' ? partner?.pastilles.map(p => ({ id: p.id })) ?? [] : undefined }) })
        saved('Cadastro salvo.')
      }
    } catch (e) { setError((e as Error).message) }
    finally { submitting.current = false; setBusy(false) }
  }
  const titles = { new: 'Nova pastilha', edit: 'Editar pastilha', move: 'Registrar movimentação', history: 'Histórico da pastilha', partner: 'Cadastro' }
  return <dialog ref={dialog} onCancel={event => { event.preventDefault(); if (!busy) close() }} aria-labelledby="pastille-dialog">
    <div className="dialog-heading"><h2 id="pastille-dialog">{titles[action.kind]}</h2><button className="secondary-button" disabled={busy} onClick={close}>Fechar</button></div>
    {item && <p><strong>{item.sku_code}</strong> · {item.description}</p>}
    {error && <p role="alert" className="form-error">{error}</p>}
    {!ready && (error ? <button onClick={() => setAttempt(n => n + 1)}>Tentar novamente</button> : <p role="status">Carregando…</p>)}
    {ready && (action.kind === 'history' ? <>
      <p>Fornecedores: {detail?.suppliers.map(p => p.name).join(', ') || 'Nenhum vinculado'}</p>
      {history.length ? <ol className="product-history">{history.map(m => <li key={m.id}><strong>{m.reason.type === 'IN' ? '+' : '−'}{m.quantity}</strong> · {m.reason.description}<br/>{m.user.name} · <time dateTime={m.date_time}>{m.date_time.slice(0, 16).replace('T', ' ')}</time></li>)}</ol> : <p>Nenhuma movimentação registrada.</p>}
    </> : <form onSubmit={submit}>
      {action.kind === 'move' && <>
        <p>Saldo atual: <strong>{item?.current_balance}</strong></p>
        <div className="movement-reason">
          <span id="reason-label">Motivo</span>
          <details ref={reasonMenu} onKeyDown={event => {
            if (event.key === 'Escape' && event.currentTarget.open) {
              event.preventDefault()
              event.stopPropagation()
              event.currentTarget.open = false
              event.currentTarget.querySelector('summary')?.focus()
            }
          }} onBlur={event => {
            if (!event.currentTarget.contains(event.relatedTarget)) event.currentTarget.open = false
          }}>
            <summary aria-labelledby="reason-label reason-value"><span id="reason-value">
              {reason ? `${reason.type === 'IN' ? 'Entrada' : 'Saída'} · ${reason.description}` : 'Selecione'}
            </span></summary>
            <div className="reason-options" role="group" aria-labelledby="reason-label">
              {reasons.map(r => <button type="button" key={r.id} aria-pressed={reasonId === String(r.id)} onClick={() => {
                setReasonId(String(r.id))
                if (reasonMenu.current) {
                  reasonMenu.current.open = false
                  reasonMenu.current.querySelector('summary')?.focus()
                }
              }}>{r.type === 'IN' ? 'Entrada' : 'Saída'} · {r.description}</button>)}
            </div>
          </details>
        </div>
        <label>Quantidade<input type="number" name="quantity" min={1} max={maximum} step={1} required defaultValue={1}/></label>
        {!reasons.length && <p role="status">Nenhum motivo ativo para esta operação.</p>}
      </>}
      {(action.kind === 'new' || action.kind === 'edit') && <>
        {action.kind === 'new' && <label>SKU<input name="sku" maxLength={30} required pattern=".*\S.*"/></label>}
        <label>Descrição<input name="description" maxLength={255} defaultValue={item?.description ?? ''}/></label>
        <label>Fabricante<select name="manufacturer" required defaultValue={detail?.manufacturer?.id ?? ''}><option value="">Selecione</option>{manufacturers.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}</select></label>
        <fieldset className="pastille-suppliers"><legend>Fornecedores</legend>
          {suppliers.length ? suppliers.map(s => <label key={s.id}>
            <input type="checkbox" name="supplier" value={s.id} defaultChecked={detail?.suppliers.some(link => link.id === s.id)}/>
            {s.name}
          </label>) : <span>Nenhum fornecedor cadastrado.</span>}
        </fieldset>
        <label>Estoque mínimo<input type="number" name="minimum" step={1} min={action.kind === 'new' ? 1 : 0} max={999999999} defaultValue={item?.minimum_stock_level ?? 1} required/></label>
        {!manufacturers.length && <p>Cadastre um fabricante para adicionar uma pastilha.</p>}
      </>}
      {action.kind === 'partner' && <>
        <label>Nome<input name="name" maxLength={120} required defaultValue={action.partner?.name}/></label>
        <label>Contato<input name="contact" maxLength={120} required defaultValue={action.partner?.contact}/></label>
      </>}
      <button className="primary-button" disabled={busy || (action.kind === 'move' && (!reason || !maximum))}>{busy ? 'Salvando…' : 'Salvar'}</button>
    </form>)}
  </dialog>
}

export default function Pastilles({ tab }: { tab: 'pastilles' | Catalog }) {
  const [items, setItems] = useState<Item[]>([])
  const [partners, setPartners] = useState<Partner[]>([])
  const [alertOnly, setAlertOnly] = useState(false)
  const [stock, setStock] = useState('all')
  const [sort, setSort] = useState<'sku_code' | 'description' | 'manufacturer' | 'current_balance'>('sku_code')
  const [descending, setDescending] = useState(false)
  const [pageSize, setPageSize] = useState(50)
  const [page, setPage] = useState(1)
  const [query, setQuery] = useState('')
  const [action, setAction] = useState<Action | null>(null)
  const [revision, setRevision] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  useEffect(() => {
    const controller = new AbortController()
    const options = { signal: controller.signal }
    setLoading(true)
    setError('')
    const load = tab === 'pastilles'
      ? domainApi<Item[]>(alertOnly ? '/pastilles?on_alert=true' : '/pastilles', options).then(setItems)
      : domainApi<Partner[]>(`/${tab}`, options).then(setPartners)
    load.catch(e => { if (!controller.signal.aborted) setError(e.message) })
      .finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [tab, alertOnly, revision])
  const labels = { pastilles: 'Pastilhas', manufacturer: 'Fabricantes', supplier: 'Fornecedores' }
  const search = query.trim().toLocaleLowerCase('pt-BR')
  const filtered = items.filter(p => [p.id, p.sku_code, p.description, p.manufacturer].join(' ').toLocaleLowerCase('pt-BR').includes(search)
    && (stock === 'all' || (stock === 'empty' ? p.current_balance === 0 : p.current_balance > 0)))
    .sort((a, b) => {
      const order = sort === 'current_balance' ? a.current_balance - b.current_balance :
        (a[sort] ?? '').localeCompare(b[sort] ?? '', 'pt-BR', { numeric: true })
      return descending ? -order : order
    })
  const pages = Math.max(1, Math.ceil(filtered.length / pageSize))
  const currentPage = Math.min(page, pages)
  const visible = filtered.slice((currentPage - 1) * pageSize, currentPage * pageSize)
  const matching = partners.filter(p => [p.name, p.contact].join(' ').toLocaleLowerCase('pt-BR').includes(search))
  return <section aria-labelledby="catalog-title">
    <header className="page-header"><h2 id="catalog-title">{labels[tab]}</h2><button className="primary-button" onClick={() => setAction(tab === 'pastilles' ? { kind: 'new' } : { kind: 'partner', catalog: tab })}>{tab === 'pastilles' ? 'Nova pastilha' : tab === 'supplier' ? 'Novo fornecedor' : 'Novo fabricante'}</button></header>
    {tab === 'pastilles' && !loading && !error && <div className="inventory-summary">
      <span>{alertOnly ? 'Estoque em alerta' : 'Visão geral do estoque'}</span>
      <span><strong>{items.length}</strong> pastilhas · <strong>{items.reduce((sum, item) => sum + item.current_balance, 0).toLocaleString('pt-BR')}</strong> unidades</span>
    </div>}
    <section className="filter-toolbar" onChange={() => setPage(1)}>
      <label className="search-field"><input type="search" aria-label="Pesquisar cadastros" placeholder={tab === 'pastilles' ? 'Pesquisar SKU, descrição ou fabricante' : 'Pesquisar nome ou contato'} value={query} onChange={e => setQuery(e.target.value)}/></label>
      {tab === 'pastilles' && <label><input type="checkbox" checked={alertOnly} onChange={e => setAlertOnly(e.target.checked)}/> Somente em alerta</label>}
      {tab === 'pastilles' && <label className="toolbar-field">Disponibilidade<select value={stock} onChange={e => setStock(e.target.value)}>
        <option value="all">Todas</option><option value="available">Com estoque</option><option value="empty">Sem estoque</option>
      </select></label>}
    </section>
    {notice && <p role="status">{notice}</p>}
    {loading ? <p role="status">Carregando…</p> : error ? <div role="alert">{error} <button onClick={() => setRevision(n => n + 1)}>Tentar novamente</button></div> :
      <section className="products-section table-scroll">{tab === 'pastilles' && <div className="table-toolbar">
        <div><h2>Pastilhas no estoque</h2><span className="result-count">{filtered.length} resultado(s)</span></div>
        <div className="sort-controls"><label htmlFor="stock-sort">Ordenar por</label><select id="stock-sort" value={sort} onChange={e => { setSort(e.target.value as typeof sort); setPage(1) }}>
          <option value="sku_code">SKU</option><option value="description">Descrição</option><option value="manufacturer">Fabricante</option><option value="current_balance">Saldo</option></select>
          <button className="sort-direction" aria-label="Inverter ordenação" onClick={() => { setDescending(!descending); setPage(1) }}>{descending ? '↓' : '↑'}</button>
        </div></div>}
        {tab === 'pastilles' ? <table className="pastilles-table"><thead><tr>{['SKU', 'Descrição', 'Fabricante', 'Fornecedores', 'Mínimo', 'Saldo', 'Operações'].map(h => <th key={h} scope="col">{h}</th>)}</tr></thead>
        <tbody>{visible.map(p => <tr key={p.id}><td>{p.sku_code}</td><td>{p.description}</td><td>{p.manufacturer}</td><td>{p.suppliers.join(', ') || 'Sem fornecedor'}</td><td>{p.minimum_stock_level}</td><td><div className="stock-control">
          <button className="stock-button" aria-label={`Retirar unidades de ${p.sku_code}`} disabled={p.current_balance <= 0} onClick={() => setAction({ kind: 'move', item: p, direction: 'OUT' })}>−</button>
          <span className="quantity">{p.current_balance}</span>
          <button className="stock-button" aria-label={`Adicionar unidades a ${p.sku_code}`} disabled={p.current_balance >= 999999999} onClick={() => setAction({ kind: 'move', item: p, direction: 'IN' })}>+</button>
        </div></td><td><div className="row-actions">
          <button type="button" title="Ver histórico" aria-label={`Ver histórico de ${p.sku_code}`} onClick={() => setAction({ kind: 'history', item: p })}><Icon name="eye" /></button>
          <button type="button" title="Editar pastilha" aria-label={`Editar pastilha ${p.sku_code}`} onClick={() => setAction({ kind: 'edit', item: p })}><Icon name="edit" /></button>
        </div></td></tr>)}</tbody></table> : <table><thead><tr><th>Nome</th><th>Contato</th><th>Operações</th></tr></thead><tbody>{matching.map(p => <tr key={p.id}><td>{p.name}</td><td>{p.contact}</td><td><button className="secondary-button" onClick={() => setAction({ kind: 'partner', catalog: tab, partner: p })}>Editar</button></td></tr>)}</tbody></table>}
        {!(tab === 'pastilles' ? filtered : matching).length && <p className="empty-state">Nenhum registro encontrado.</p>}
        {tab === 'pastilles' && <footer className="table-footer"><label>Por página <select value={pageSize} onChange={e => { setPageSize(Number(e.target.value)); setPage(1) }}>
          {[50, 100, 200].map(size => <option key={size}>{size}</option>)}</select></label>
          <nav className="pagination" aria-label="Paginação de pastilhas">
            <button className="secondary-button" disabled={currentPage === 1} onClick={() => setPage(currentPage - 1)}>Anterior</button>
            <span>Página {currentPage} de {pages}</span>
            <button className="secondary-button" disabled={currentPage === pages} onClick={() => setPage(currentPage + 1)}>Próxima</button>
          </nav></footer>}
      </section>}
    {action && <PastilleDialog action={action} close={() => setAction(null)} saved={message => { setAction(null); setNotice(message); setRevision(n => n + 1) }}/>}
  </section>
}
