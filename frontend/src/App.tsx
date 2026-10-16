import { useEffect, useRef, useState } from 'react'
import type { FormEvent, ReactNode } from 'react'
import './App.css'

type Product = { id: number; name: string; category: string; quantity: number; updatedAt: string }
type SortKey = 'name' | 'category' | 'quantity' | 'updatedAt'
type IconName = 'box' | 'search' | 'eye' | 'edit' | 'plus' | 'close' | 'filter'
function Icon({ name }: { name: IconName }) {
  const paths: Record<IconName, ReactNode> = {
    box: <path d="m12 3 9 5v8l-9 5-9-5V8zM3 8l9 5 9-5M12 13v8M7.5 5.5l9 5" />,
    search: <><circle cx="10.5" cy="10.5" r="6.5" /><path d="m16 16 5 5" /></>,
    eye: <><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z" /><circle cx="12" cy="12" r="3" /></>,
    edit: <path d="m15 5 4 4M4 20l5-1L20 8a2.8 2.8 0 0 0-4-4L5 15z" />,
    plus: <path d="M12 5v14M5 12h14" />,
    close: <path d="m6 6 12 12M6 18 18 6" />,
    filter: <path d="M4 6h16M7 12h10M10 18h4" />,
  }
  return <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">{paths[name]}</svg>
}
const categories = ['Ferramentas', 'Elétrica', 'Hidráulica', 'Fixadores', 'Pintura', 'Segurança']
const initialProducts: Product[] = [
  { id: 1, name: 'Furadeira de impacto 650 W', category: 'Ferramentas', quantity: 18, updatedAt: '2026-10-16' },
  { id: 2, name: 'Cabo flexível 2,5 mm² · 100 m', category: 'Elétrica', quantity: 42, updatedAt: '2026-10-16' },
  { id: 3, name: 'Joelho PVC 90° · 25 mm', category: 'Hidráulica', quantity: 156, updatedAt: '2026-10-15' },
  { id: 4, name: 'Parafuso Phillips 4 × 40 mm', category: 'Fixadores', quantity: 320, updatedAt: '2026-10-15' },
  { id: 5, name: 'Tinta acrílica branco fosco · 18 L', category: 'Pintura', quantity: 8, updatedAt: '2026-10-14' },
  { id: 6, name: 'Luva de proteção nitrílica', category: 'Segurança', quantity: 64, updatedAt: '2026-10-14' },
  { id: 7, name: 'Alicate universal 8 polegadas', category: 'Ferramentas', quantity: 27, updatedAt: '2026-10-13' },
  { id: 8, name: 'Tomada dupla 10 A', category: 'Elétrica', quantity: 0, updatedAt: '2026-10-13' },
  { id: 9, name: 'Rolo de pintura lã · 23 cm', category: 'Pintura', quantity: 35, updatedAt: '2026-10-12' },
]
const normalize = (value: string) => value.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('pt-BR')
const formatDate = (value: string) => new Intl.DateTimeFormat('pt-BR').format(new Date(`${value}T12:00:00`))
function today() {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
function App() {
  const [products, setProducts] = useState(initialProducts)
  const [query, setQuery] = useState('')
  const [selectedCategories, setSelectedCategories] = useState<string[]>([])
  const [stock, setStock] = useState('all')
  const [date, setDate] = useState('')
  const [sort, setSort] = useState<SortKey>('name')
  const [descending, setDescending] = useState(false)
  const [modal, setModal] = useState<{ mode: 'view' | 'edit' | 'new'; product?: Product } | null>(null)
  const [notice, setNotice] = useState('')
  const dialog = useRef<HTMLDialogElement>(null)
  useEffect(() => { if (modal) dialog.current?.showModal() }, [modal])
  const filtered = products.filter(product => (
    normalize(`${product.name} ${product.id} ${product.category}`).includes(normalize(query.trim())) &&
    (!selectedCategories.length || selectedCategories.includes(product.category)) &&
    (stock === 'all' || (stock === 'available' ? product.quantity > 0 : product.quantity === 0)) &&
    (!date || product.updatedAt === date)
  )).sort((a, b) => {
    const result = sort === 'quantity' ? a.quantity - b.quantity : String(a[sort]).localeCompare(String(b[sort]), 'pt-BR')
    return descending ? -result : result
  })
  const hasFilters = Boolean(query || selectedCategories.length || stock !== 'all' || date)
  function clearFilters() { setQuery(''); setSelectedCategories([]); setStock('all'); setDate('') }
  function saveProduct(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const name = String(form.get('name')).trim()
    if (!name) return
    const product: Product = {
      id: modal?.product?.id ?? Math.max(0, ...products.map(item => item.id)) + 1,
      name, category: String(form.get('category')), quantity: Number(form.get('quantity')), updatedAt: today(),
    }
    setProducts(current => modal?.mode === 'edit' ? current.map(item => item.id === product.id ? product : item) : [...current, product])
    setNotice(`${product.name}: ${modal?.mode === 'edit' ? 'alterações salvas' : 'produto cadastrado'} nesta demonstração.`)
    setModal(null)
  }
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <a className="brand" href="#main" aria-label="iStock — início"><span className="brand-symbol"><Icon name="box" /></span><span>iStock<span className="brand-dot">.</span></span></a>
        <span className="nav-caption">GERENCIAMENTO</span>
        <nav aria-label="Menu principal"><a className="nav-link active" href="#main" aria-current="page"><Icon name="box" /><span>Estoque</span><span className="nav-indicator" /></a></nav>
        <div className="sidebar-footer"><span className="avatar">IS</span><div><strong>Meu estoque</strong><small>Gestão de produtos</small></div></div>
      </aside>
      <main id="main">
        <header className="page-header"><div><div className="eyebrow">CONTROLE DE ESTOQUE <span>/</span> PRODUTOS</div><h1>Gestão de produtos<span>.</span></h1><p>Todos os seus produtos, em um só lugar.</p></div><button className="primary-button" onClick={() => setModal({ mode: 'new' })}><Icon name="plus" /> Novo produto</button></header>
        <div className="inventory-summary"><span><span className="status-dot" /> Visão geral do estoque</span><span><strong>{products.length}</strong> produtos cadastrados <span className="summary-divider">/</span> <strong>{products.reduce((sum, product) => sum + product.quantity, 0).toLocaleString('pt-BR')}</strong> unidades</span></div>
        <section className="filter-panel" aria-label="Filtros de produtos">
          <div className="filter-topline"><span><Icon name="filter" /> Filtrar por categoria</span><button className="clear-button" onClick={clearFilters} disabled={!hasFilters}>Limpar filtros <Icon name="close" /></button></div>
          <div className="category-list">{categories.map(category => <button key={category} aria-pressed={selectedCategories.includes(category)} className={`category-button ${selectedCategories.includes(category) ? 'selected' : ''}`} onClick={() => setSelectedCategories(current => current.includes(category) ? current.filter(item => item !== category) : [...current, category])}>{category}<span>{products.filter(product => product.category === category).length}</span></button>)}</div>
          <div className="filter-toolbar">
            <label className="search-field"><Icon name="search" /><input type="search" aria-label="Pesquisar por nome, ID ou categoria" placeholder="Pesquisar por nome, ID ou categoria..." value={query} onChange={event => setQuery(event.target.value)} /></label>
            <label className="toolbar-field"><span>Atualização</span><input aria-label="Data da última atualização" type="date" value={date} onChange={event => setDate(event.target.value)} /></label>
            <label className="toolbar-field"><span>Quantidade</span><select value={stock} onChange={event => setStock(event.target.value)}><option value="all">Todas</option><option value="available">Com estoque</option><option value="empty">Sem estoque</option></select></label>
          </div>
        </section>
        <section className="products-section" aria-labelledby="products-title">
          <div className="table-toolbar"><div><h2 id="products-title">Produtos no estoque</h2><span className="result-count" role="status">{filtered.length} {filtered.length === 1 ? 'resultado' : 'resultados'}</span></div><div className="sort-controls"><label htmlFor="sort">Ordenar por</label><select id="sort" value={sort} onChange={event => setSort(event.target.value as SortKey)}><option value="name">Nome</option><option value="category">Categoria</option><option value="updatedAt">Atualização</option><option value="quantity">Quantidade</option></select><button className="sort-direction" onClick={() => setDescending(!descending)} aria-label={descending ? 'Ordenação decrescente. Alterar para crescente' : 'Ordenação crescente. Alterar para decrescente'}>{descending ? '↓' : '↑'}</button></div></div>
          <div className="table-scroll"><table><thead><tr><th scope="col">ID</th><th scope="col">Nome do produto</th><th scope="col">Categoria</th><th scope="col">Última atualização</th><th scope="col" className="quantity-column">QTD</th><th scope="col" className="actions-column">Operações</th></tr></thead><tbody>{filtered.map(product => <tr key={product.id}><td className="product-id">#{String(product.id).padStart(3, '0')}</td><td className="product-name">{product.name}</td><td><span className="category-tag">{product.category}</span></td><td className="date-cell">{formatDate(product.updatedAt)}</td><td className="quantity-column"><span className={`quantity ${product.quantity === 0 ? 'empty-stock' : ''}`}>{product.quantity}</span></td><td><div className="row-actions"><button title="Visualizar produto" aria-label={`Visualizar ${product.name}`} onClick={() => setModal({ mode: 'view', product })}><Icon name="eye" /></button><button title="Editar produto" aria-label={`Editar ${product.name}`} onClick={() => setModal({ mode: 'edit', product })}><Icon name="edit" /></button></div></td></tr>)}</tbody></table>{!filtered.length && <div className="empty-state"><Icon name="search" /><h3>Nenhum produto encontrado</h3><p>Tente outro termo ou remova os filtros da pesquisa.</p><button className="secondary-button" onClick={clearFilters}>Limpar filtros</button></div>}</div>
          <footer className="table-footer"><span>Exibindo {filtered.length} de {products.length} produtos</span><span>Dados de demonstração · alterações válidas nesta sessão</span></footer>
        </section>
        <p className="save-notice" role="status">{notice}</p>
        <footer className="page-footer"><span>iStock <span> / </span> Organização que faz a diferença.</span><span>GESTÃO DE ESTOQUE</span></footer>
      </main>
      {modal && <dialog ref={dialog} onCancel={() => setModal(null)} onClose={() => setModal(null)} aria-labelledby="dialog-title"><div className="dialog-heading"><h2 id="dialog-title">{modal.mode === 'new' ? 'Novo produto' : modal.mode === 'edit' ? 'Editar produto' : 'Detalhes do produto'}</h2><button className="icon-button" aria-label="Fechar" onClick={() => setModal(null)}><Icon name="close" /></button></div>{modal.mode === 'view' && modal.product ? <><dl className="product-details"><dt>ID</dt><dd>#{String(modal.product.id).padStart(3, '0')}</dd><dt>Nome</dt><dd>{modal.product.name}</dd><dt>Categoria</dt><dd>{modal.product.category}</dd><dt>Quantidade</dt><dd>{modal.product.quantity} unidades</dd><dt>Última atualização</dt><dd>{formatDate(modal.product.updatedAt)}</dd></dl><button className="primary-button" onClick={() => setModal({ ...modal, mode: 'edit' })}><Icon name="edit" />Editar produto</button></> : <form onSubmit={saveProduct}><label>Nome do produto<input name="name" required maxLength={120} pattern=".*\S.*" title="Informe um nome para o produto" defaultValue={modal.product?.name} autoFocus /></label><label>Categoria<select name="category" defaultValue={modal.product?.category ?? categories[0]}>{categories.map(category => <option key={category}>{category}</option>)}</select></label><label>Quantidade<input type="number" name="quantity" min="0" max="999999999" step="1" required defaultValue={modal.product?.quantity ?? 0} /></label><p className="form-hint">Dados de demonstração. As alterações são mantidas até recarregar a página.</p><div className="dialog-actions"><button type="button" className="secondary-button" onClick={() => setModal(null)}>Cancelar</button><button className="primary-button" type="submit">{modal.mode === 'new' ? 'Cadastrar produto' : 'Salvar alterações'}</button></div></form>}</dialog>}
    </div>
  )
}
export default App
