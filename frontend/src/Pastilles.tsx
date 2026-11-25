import PageHeader from './PageHeader'
import Select from './Select'
import { useEffect, useRef, useState } from 'react'
import type { FormEvent, ReactNode } from 'react'
import { domainApi } from './api'
import Icon from './Icon'

type Category = { id: number; name: string }
type Item = { id: number; sku_code: string; description: string | null; minimum_stock_level: number; current_balance: number; category: Category | null; manufacturer: string | null; suppliers: string[] }
type Partner = { id: number; name: string; contact: string; pastilles: Item[]; isActive?: boolean; is_active?: boolean }
type Reason = { id: number; description: string; type: 'IN' | 'OUT' }
type Movement = { id: number; date_time: string; quantity: number; reason: Reason; user: { name: string } }
type Detail = { category: Category | null; manufacturer: Partner | null; suppliers: Partner[] }
type Catalog = 'manufacturer' | 'supplier'
type Action = { kind: 'deleteCategory'; category: Category } | { kind: 'deletePartner'; catalog: Catalog; partner: Partner } | { kind: 'reason'; reason?: Reason; active?: boolean } | { kind: 'deactivate'; reason: Reason } | { kind: 'delete'; item: Item } | { kind: 'category'; category?: Category } | { kind: 'new' } | { kind: 'edit' | 'history'; item: Item } | { kind: 'move'; item: Item; direction: Reason['type'] } | { kind: 'partner'; catalog: Catalog; partner?: Partner }

function PastilleDialog({ action, close, saved }: { action: Action; close: () => void; saved: (message: string) => void }) {
  const dialog = useRef<HTMLDialogElement>(null)
  const submitting = useRef(false)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [ready, setReady] = useState(false)
  const [reasons, setReasons] = useState<Reason[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [manufacturers, setManufacturers] = useState<Partner[]>([])
  const [suppliers, setSuppliers] = useState<Partner[]>([])
  const [detail, setDetail] = useState<Detail | null>(null)
  const [history, setHistory] = useState<Movement[]>([])
  const [reasonId, setReasonId] = useState('')
  const [customText, setCustomText] = useState('')
  const customEntry = action.kind === 'move' && action.direction === 'IN' && reasonId === 'custom'
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
        const active = await domainApi<Reason[]>('/reason-movement?is_active=true', options)
        setReasons(active.filter(entry => entry.type === action.direction))
      }
      if (action.kind === 'edit' || action.kind === 'new') {
        const [makers, vendors, categoryList] = await Promise.all([
          domainApi<Partner[]>('/manufacturer', options),
          domainApi<Partner[]>('/supplier', options),
          domainApi<Category[]>('/pastilles/categories', options),
        ])
        setManufacturers(makers)
        setSuppliers(vendors)
        setCategories(categoryList)
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
  function selectReason(id: string) {
    setReasonId(id)
    setCustomText('')
    if (reasonMenu.current) {
      reasonMenu.current.open = false
      reasonMenu.current.querySelector('summary')?.focus()
    }
  }
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
        const selection = customEntry ? { custom_reason: customText.trim() } : { reason_movement_id: Number(reasonId) }
        await domainApi('/movement', { method: 'POST', body: JSON.stringify({ pastille: action.item.id, ...selection, quantity: Number(value('quantity')) }) })
        saved('Movimentação registrada.')
      } else if (action.kind === 'new' || action.kind === 'edit') {
        const fields = { category: { id: Number(value('category')) || null }, supplier_ids: form.getAll('supplier').map(Number), description: value('description'), manufacturer_id: Number(value('manufacturer')), minimum_stock_level: Number(value('minimum')) }
        if (action.kind === 'new')
          await domainApi('/pastilles', { method: 'POST', body: JSON.stringify({ ...fields, sku_code: value('sku') }) })
        else await domainApi(`/pastilles/${action.item.id}`, { method: 'PUT', body: JSON.stringify(fields) })
        saved('Pastilha salva.')
      } else if (action.kind === 'deleteCategory') {
        await domainApi(`/pastilles/categories/${action.category.id}`, { method: 'DELETE' })
        saved('Categoria excluída.')
      } else if (action.kind === 'deletePartner') {
        const route = action.catalog === 'supplier' ? '/supplier/deleteSupplier' : '/manufacturer/deleteManufacturer'
        await domainApi(`${route}/${action.partner.id}`, { method: 'DELETE' })
        saved(action.catalog === 'supplier' ? 'Fornecedor excluído.' : 'Fabricante excluído.')
      } else if (action.kind === 'delete') {
        await domainApi(`/pastilles/${action.item.id}`, { method: 'DELETE' })
        saved('Pastilha e movimentações excluídas.')
      } else if (action.kind === 'deactivate') {
        await domainApi(`/reason-movement/${action.reason.id}`, { method: 'DELETE' })
        saved('Motivo desativado.')
      } else if (action.kind === 'reason') {
        const description = value('description')
        if (action.reason) {
          await domainApi(`/reason-movement/${action.reason.id}`, { method: 'PUT', body: JSON.stringify({ description, is_active: value('active') === 'true' }) })
        } else {
          await domainApi('/reason-movement', { method: 'POST', body: JSON.stringify({ description, type: value('type') }) })
        }
        saved('Motivo salvo.')
      } else if (action.kind === 'category') {
        const path = action.category ? `/pastilles/categories/${action.category.id}` as const : '/pastilles/categories'
        await domainApi(path, { method: action.category ? 'PUT' : 'POST', body: JSON.stringify({ name: value('name') }) })
        saved('Categoria salva.')
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
  const titles = { deleteCategory: 'Excluir categoria', deletePartner: action.kind === 'deletePartner' && action.catalog === 'supplier' ? 'Excluir fornecedor' : 'Excluir fabricante', reason: action.kind === 'reason' && action.reason ? 'Editar motivo' : 'Novo motivo', deactivate: 'Desativar motivo', delete: 'Excluir pastilha', new: 'Nova pastilha', edit: 'Editar pastilha', move: 'Registrar movimentação', history: 'Histórico da pastilha', partner: 'Cadastro', category: action.kind === 'category' && action.category ? 'Editar categoria' : 'Nova categoria' }
  return <dialog ref={dialog} onCancel={event => { event.preventDefault(); if (!busy) close() }} aria-labelledby="pastille-dialog">
    <div className="dialog-heading"><h2 id="pastille-dialog">{titles[action.kind]}</h2><button className="secondary-button" disabled={busy} onClick={close}>Fechar</button></div>
    {item && <p><strong>{item.sku_code}</strong> · {item.description}</p>}
    {error && <p role="alert" className="form-error">{error}</p>}
    {!ready && (error ? <button onClick={() => setAttempt(n => n + 1)}>Tentar novamente</button> : <p role="status">Carregando…</p>)}
    {ready && (action.kind === 'history' ? <>
      <p>Fornecedores: {detail?.suppliers.map(p => p.name).join(', ') || 'Nenhum vinculado'}</p>
      {history.length ? <ol className="product-history">{history.map(m => <li key={m.id}><strong>{m.reason.type === 'IN' ? '+' : '−'}{m.quantity}</strong> · {m.reason.description}<br/>{m.user.name} · <time dateTime={m.date_time}>{m.date_time.slice(0, 16).replace('T', ' ')}</time></li>)}</ol> : <p>Nenhuma movimentação registrada.</p>}
    </> : <form onSubmit={submit}>
      {action.kind === 'delete' && <p>A exclusão remove esta pastilha, seu saldo e todo o histórico de movimentações. Essa ação é permanente.</p>}
      {action.kind === 'deleteCategory' && <p>Excluir definitivamente a categoria <strong>{action.category.name}</strong>?</p>}
      {action.kind === 'deletePartner' && <p>Excluir definitivamente o cadastro de <strong>{action.partner.name}</strong>?</p>}
      {action.kind === 'deactivate' && <p>Desativar o motivo <strong>{action.reason.description}</strong>? O histórico será preservado.</p>}
      {action.kind === 'reason' && <>
        <label>Descrição<input name="description" required maxLength={80} pattern=".*\S.*" defaultValue={action.reason?.description}/></label>
        {action.reason ? <>
          <p>Tipo: {action.reason.type === 'IN' ? 'Entrada' : 'Saída'}</p>
          <label>Situação<Select name="active" defaultValue={String(action.active)}><option value="true">Ativo</option><option value="false">Inativo</option></Select></label>
        </> : <label>Tipo<Select name="type" required><option value="IN">Entrada</option><option value="OUT">Saída</option></Select></label>}
      </>}
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
              {customEntry ? 'Entrada - Customizada' : reason ? `${reason.type === 'IN' ? 'Entrada' : 'Saída'} · ${reason.description}` : 'Selecione'}
            </span></summary>
            <div className="reason-options" role="group" aria-labelledby="reason-label">
              {reasons.map(r => <button type="button" key={r.id} aria-pressed={reasonId === String(r.id)} onClick={() => selectReason(String(r.id))}>{r.type === 'IN' ? 'Entrada' : 'Saída'} · {r.description}</button>)}
              {action.direction === 'IN' && <button type="button" aria-pressed={customEntry} onClick={() => selectReason('custom')}>Entrada - Customizada</button>}
            </div>
          </details>
        </div>
        {customEntry && <label>Motivo da entrada<input name="custom_reason" value={customText} onChange={e => setCustomText(e.target.value)} maxLength={80} required placeholder="Descreva o motivo desta entrada"/></label>}
        <label>Quantidade<input type="number" name="quantity" min={1} max={maximum} step={1} required defaultValue={1}/></label>
        {action.direction === 'OUT' && !reasons.length && <p role="status">Nenhum motivo ativo para esta operação.</p>}
      </>}
      {(action.kind === 'new' || action.kind === 'edit') && <>
        {action.kind === 'new' && <label>SKU<input name="sku" maxLength={30} required pattern=".*\S.*"/></label>}
        <label>Descrição<input name="description" maxLength={255} defaultValue={item?.description ?? ''}/></label>
        <label>Fabricante<Select name="manufacturer" required defaultValue={detail?.manufacturer?.id ?? ''}><option value="">Selecione</option>{manufacturers.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}</Select></label>
        <label>Categoria<Select name="category" defaultValue={detail?.category?.id ?? ''}>
          <option value="">Sem categoria</option>
          {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
        </Select></label>
        <fieldset className="pastille-suppliers"><legend>Fornecedores</legend>
          {suppliers.length ? suppliers.map(s => <label key={s.id}>
            <input type="checkbox" name="supplier" value={s.id} defaultChecked={detail?.suppliers.some(link => link.id === s.id)}/>
            {s.name}
          </label>) : <span>Nenhum fornecedor cadastrado.</span>}
        </fieldset>
        <label>Estoque mínimo<input type="number" name="minimum" step={1} min={action.kind === 'new' ? 1 : 0} max={999999999} defaultValue={item?.minimum_stock_level ?? 1} required/></label>
        {!manufacturers.length && <p>Cadastre um fabricante para adicionar uma pastilha.</p>}
      </>}
      {action.kind === 'category' && <label>Nome<input name="name" required maxLength={60} pattern=".*\S.*" defaultValue={action.category?.name}/></label>}
      {action.kind === 'partner' && <>
        <label>Nome<input name="name" maxLength={120} required defaultValue={action.partner?.name}/></label>
        <label>Contato<input name="contact" maxLength={120} required defaultValue={action.partner?.contact}/></label>
      </>}
      <button className="primary-button" disabled={busy || (action.kind === 'move' && ((customEntry ? !customText.trim() : !reason) || !maximum))}>{busy ? 'Salvando…' : (action.kind === 'delete' || action.kind === 'deleteCategory' || action.kind === 'deletePartner') ? 'Excluir definitivamente' : action.kind === 'deactivate' ? 'Desativar' : 'Salvar'}</button>
    </form>)}
  </dialog>
}

export default function Pastilles({ tab, children }: { tab: 'pastilles' | 'categories' | 'reasons' | Catalog; children: ReactNode }) {
  const [items, setItems] = useState<Item[]>([])
  const [categories, setCategories] = useState<(Category & { pastilleCount: number })[]>([])
  const [reasons, setReasons] = useState<Reason[]>([])
  const [activeReasons, setActiveReasons] = useState(true)
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
      : tab === 'reasons' ? domainApi<Reason[]>(`/reason-movement?is_active=${activeReasons}`, options).then(setReasons)
      : tab === 'categories' ? domainApi<(Category & { pastilleCount: number })[]>('/pastilles/categories', options).then(setCategories)
      : domainApi<Partner[]>(`/${tab}`, options).then(setPartners)
    load.catch(e => { if (!controller.signal.aborted) setError(e.message) })
      .finally(() => { if (!controller.signal.aborted) setLoading(false) })
    return () => controller.abort()
  }, [tab, alertOnly, activeReasons, revision])
  const creationActions: Record<typeof tab, { label: string; action: Action }> = {
    pastilles: { label: 'Nova pastilha', action: { kind: 'new' } },
    reasons: { label: 'Novo motivo', action: { kind: 'reason' } },
    categories: { label: 'Nova categoria', action: { kind: 'category' } },
    manufacturer: { label: 'Novo fabricante', action: { kind: 'partner', catalog: 'manufacturer' } },
    supplier: { label: 'Novo fornecedor', action: { kind: 'partner', catalog: 'supplier' } },
  }
  const creation = creationActions[tab]
  const labels = { reasons: 'Motivos de movimentação', categories: 'Categorias', pastilles: 'Pastilhas', manufacturer: 'Fabricantes', supplier: 'Fornecedores' }
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
  const matchingCategories = categories.filter(c => c.name.toLocaleLowerCase('pt-BR').includes(search))
  const matching = partners.filter(p => [p.name, p.contact].join(' ').toLocaleLowerCase('pt-BR').includes(search))
  const matchingReasons = reasons.filter(r => r.description.toLocaleLowerCase('pt-BR').includes(search))
  const count = tab === 'pastilles' ? filtered.length : tab === 'categories' ? matchingCategories.length : tab === 'reasons' ? matchingReasons.length : matching.length
  return <>
    <PageHeader section="Produtos" title="Gestão de produtos" description="Consulte o estoque de pastilhas e gerencie seus cadastros."><button className="primary-button" onClick={() => setAction(creation.action)}><Icon name="plus" />{creation.label}</button></PageHeader>
    {children}
    {tab === 'pastilles' && !loading && !error && <div className="inventory-summary">
      <span>{alertOnly ? 'Estoque em alerta' : 'Visão geral do estoque'}</span>
      <span><strong>{items.length}</strong> pastilhas · <strong>{items.reduce((sum, item) => sum + item.current_balance, 0).toLocaleString('pt-BR')}</strong> unidades</span>
    </div>}
    <section className="filter-panel">
      <div className="filter-topline"><span><Icon name="filter" />Filtrar {labels[tab].toLocaleLowerCase('pt-BR')}</span>
        <button className="clear-button" disabled={!query && !alertOnly && stock === 'all' && activeReasons} onClick={() => { setQuery(''); setActiveReasons(true); setAlertOnly(false); setStock('all'); setPage(1) }}>Limpar filtros<Icon name="close" /></button>
      </div>
      <div className="filter-toolbar" onChange={() => setPage(1)}>
      <label className="search-field"><Icon name="search" /><input type="search" aria-label="Pesquisar cadastros" placeholder={tab === 'reasons' ? 'Pesquisar motivo' : tab === 'pastilles' ? 'Pesquisar SKU, descrição ou fabricante' : tab === 'categories' ? 'Pesquisar categoria' : 'Pesquisar nome ou contato'} value={query} onChange={e => setQuery(e.target.value)}/></label>
      {tab === 'reasons' && <div className="reason-status-filter">
        <label htmlFor="reason-status">Situação</label>
        <Select id="reason-status" value={String(activeReasons)} onChange={e => { setLoading(true); setActiveReasons(e.target.value === 'true') }}>
          <option value="true">Ativos</option>
          <option value="false">Inativos</option>
        </Select>
      </div>}
      {tab === 'pastilles' && <label><input type="checkbox" checked={alertOnly} onChange={e => setAlertOnly(e.target.checked)}/> Somente em alerta</label>}
      {tab === 'pastilles' && <label className="toolbar-field">Disponibilidade<Select value={stock} onChange={e => setStock(e.target.value)}>
        <option value="all">Todas</option><option value="available">Com estoque</option><option value="empty">Sem estoque</option>
      </Select></label>}
      </div>
    </section>
    {notice && <p role="status">{notice}</p>}
    {loading ? <p role="status">Carregando…</p> : error ? <div role="alert">{error} <button onClick={() => setRevision(n => n + 1)}>Tentar novamente</button></div> :
      <section className="products-section table-scroll"><div className="table-toolbar">
        <div><h2>{tab === 'pastilles' ? 'Pastilhas no estoque' : labels[tab]}</h2><span className="result-count">{count} resultado(s)</span></div>
        {tab === 'pastilles' && <div className="sort-controls"><label htmlFor="stock-sort">Ordenar por</label><Select id="stock-sort" value={sort} onChange={e => { setSort(e.target.value as typeof sort); setPage(1) }}>
          <option value="sku_code">SKU</option><option value="description">Descrição</option><option value="manufacturer">Fabricante</option><option value="current_balance">Saldo</option></Select>
          <button className="sort-direction" aria-label="Inverter ordenação" onClick={() => { setDescending(!descending); setPage(1) }}>{descending ? '↓' : '↑'}</button>
        </div>}</div>
        {tab === 'pastilles' ? <table className="pastilles-table"><thead><tr>{['SKU', 'Descrição', 'Categoria', 'Fabricante', 'Fornecedores', 'Mínimo', 'Saldo', 'Operações'].map(h => <th key={h} scope="col">{h}</th>)}</tr></thead>
        <tbody>{visible.map(p => <tr key={p.id}><td>{p.sku_code}</td><td>{p.description}</td><td>{p.category?.name ?? 'Sem categoria'}</td><td>{p.manufacturer}</td><td>{p.suppliers.join(', ') || 'Sem fornecedor'}</td><td>{p.minimum_stock_level}</td><td><div className="stock-control">
          <button className="stock-button" aria-label={`Retirar unidades de ${p.sku_code}`} disabled={p.current_balance <= 0} onClick={() => setAction({ kind: 'move', item: p, direction: 'OUT' })}>−</button>
          <span className="quantity">{p.current_balance}</span>
          <button className="stock-button" aria-label={`Adicionar unidades a ${p.sku_code}`} disabled={p.current_balance >= 999999999} onClick={() => setAction({ kind: 'move', item: p, direction: 'IN' })}>+</button>
        </div></td><td><div className="row-actions">
          <button type="button" title="Ver histórico" aria-label={`Ver histórico de ${p.sku_code}`} onClick={() => setAction({ kind: 'history', item: p })}><Icon name="eye" /></button>
          <button type="button" title="Editar pastilha" aria-label={`Editar pastilha ${p.sku_code}`} onClick={() => setAction({ kind: 'edit', item: p })}><Icon name="edit" /></button>
          <button type="button" title="Excluir pastilha" aria-label={`Excluir pastilha ${p.sku_code}`} onClick={() => setAction({ kind: 'delete', item: p })}><Icon name="trash" /></button>
        </div></td></tr>)}</tbody></table> : tab === 'reasons' ? <table>
          <thead><tr><th>Motivo</th><th>Tipo</th><th className="actions-column" scope="col">Operações</th></tr></thead>
          <tbody>{matchingReasons.map(r => <tr key={r.id}><td>{r.description}</td><td>{r.type === 'IN' ? 'Entrada' : 'Saída'}</td><td><div className="row-actions">
            <button title="Editar motivo" aria-label={`Editar motivo ${r.description}`} onClick={() => setAction({ kind: 'reason', reason: r, active: activeReasons })}><Icon name="edit" /></button>
            {activeReasons && <button title="Desativar motivo" aria-label={`Desativar motivo ${r.description}`} onClick={() => setAction({ kind: 'deactivate', reason: r })}><Icon name="trash" /></button>}
          </div></td></tr>)}</tbody></table> : tab === 'categories' ? <table>
          <thead><tr><th>Nome</th><th className="actions-column">Operações</th></tr></thead>
          <tbody>{matchingCategories.map(c => <tr key={c.id}><td>{c.name}</td><td>
            <div className="row-actions"><button type="button" title="Editar categoria" aria-label={`Editar categoria ${c.name}`} onClick={() => setAction({ kind: 'category', category: c })}><Icon name="edit" /></button>
              <button type="button" title={c.pastilleCount ? 'Desvincule as pastilhas antes de excluir' : 'Excluir categoria'}
                aria-label={`Excluir categoria ${c.name}`} disabled={c.pastilleCount > 0}
                onClick={() => setAction({ kind: 'deleteCategory', category: c })}><Icon name="trash" /></button>
            </div>
          </td></tr>)}</tbody>
        </table> : <table><thead><tr><th>Nome</th><th>Contato</th><th className="actions-column">Operações</th></tr></thead>
          <tbody>{matching.map(p => <tr key={p.id}><td>{p.name}</td><td>{p.contact}</td><td><div className="row-actions">
            <button type="button" title={tab === 'supplier' ? 'Editar fornecedor' : 'Editar fabricante'}
              aria-label={`Editar ${tab === 'supplier' ? 'fornecedor' : 'fabricante'} ${p.name}`}
              onClick={() => setAction({ kind: 'partner', catalog: tab, partner: p })}><Icon name="edit" /></button>
            <button type="button" title={p.pastilles.length ? 'Desvincule as pastilhas antes de excluir' : 'Excluir cadastro'}
              aria-label={`Excluir ${tab === 'supplier' ? 'fornecedor' : 'fabricante'} ${p.name}`}
              disabled={p.pastilles.length > 0}
              onClick={() => setAction({ kind: 'deletePartner', catalog: tab, partner: p })}><Icon name="trash" /></button>
          </div></td></tr>)}</tbody></table>}
        {!count && <p className="empty-state">Nenhum registro encontrado.</p>}
        {tab === 'pastilles' && <footer className="table-footer"><label>Por página <Select value={pageSize} onChange={e => { setPageSize(Number(e.target.value)); setPage(1) }}>
          {[50, 100, 200].map(size => <option key={size}>{size}</option>)}</Select></label>
          <nav className="pagination" aria-label="Paginação de pastilhas">
            <button className="secondary-button" disabled={currentPage === 1} onClick={() => setPage(currentPage - 1)}>Anterior</button>
            <span>Página {currentPage} de {pages}</span>
            <button className="secondary-button" disabled={currentPage === pages} onClick={() => setPage(currentPage + 1)}>Próxima</button>
          </nav></footer>}
      </section>}
    {action && <PastilleDialog action={action} close={() => setAction(null)} saved={message => { setAction(null); setNotice(message); setRevision(n => n + 1) }}/>}
  </>
}
