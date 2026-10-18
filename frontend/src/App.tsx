import { useEffect, useRef, useState } from 'react'
import type { Dispatch, FormEvent, SetStateAction } from 'react'
import './App.css'
import Icon from './Icon'
import Users from './Users'
import type { User } from './usersData'
import { api } from './api'
import ProductHistory from './ProductHistory'
import { uncategorized } from './inventoryData'
import type { Product } from './inventoryData'

type SortKey = 'name' | 'category' | 'quantity' | 'updatedAt'
const normalize = (value: string) => value.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('pt-BR')
const formatDate = (value: string) => new Intl.DateTimeFormat('pt-BR').format(new Date(`${value}T12:00:00`))
function today() {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
function App({ currentUser, onLogout, refreshSession, products, setProducts, categories, setCategories }: {
  currentUser: User; onLogout: () => Promise<void>; refreshSession: () => Promise<void>;
  products: Product[]; setProducts: Dispatch<SetStateAction<Product[]>>;
  categories: string[]; setCategories: Dispatch<SetStateAction<string[]>>;
}) {
  const [screen, setScreen] = useState(() => window.location.hash === '#usuarios' ? 'users' : 'products')
  useEffect(() => {
    function navigate() { setScreen(window.location.hash === '#usuarios' ? 'users' : 'products'); window.scrollTo(0, 0) }
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
  const [categoryModal, setCategoryModal] = useState(false)
  const categoryDialog = useRef<HTMLDialogElement>(null)
  useEffect(() => { if (categoryModal) categoryDialog.current?.showModal() }, [categoryModal])
  const [categoryQuery, setCategoryQuery] = useState('')
  const categoryFilter = useRef<HTMLDetailsElement>(null)
  useEffect(() => {
    function closeOnOutsideClick(event: PointerEvent) {
      if (categoryFilter.current && !categoryFilter.current.contains(event.target as Node)) categoryFilter.current.open = false
    }
    function closeOnEscape(event: KeyboardEvent) {
      if (event.key === 'Escape' && categoryFilter.current?.open) {
        categoryFilter.current.open = false
        categoryFilter.current.querySelector('summary')?.focus()
      }
    }
    document.addEventListener('pointerdown', closeOnOutsideClick)
    document.addEventListener('keydown', closeOnEscape)
    return () => {
      document.removeEventListener('pointerdown', closeOnOutsideClick)
      document.removeEventListener('keydown', closeOnEscape)
    }
  }, [])
  const [query, setQuery] = useState('')
  const [selectedCategories, setSelectedCategories] = useState<string[]>([])
  const [stock, setStock] = useState('all')
  const [date, setDate] = useState('')
  const [sort, setSort] = useState<SortKey>('name')
  const [descending, setDescending] = useState(false)
  const [pageSize, setPageSize] = useState(50)
  const [page, setPage] = useState(1)
  useEffect(() => { setPage(1) }, [query, selectedCategories, stock, date, sort, descending, pageSize])
  const [modal, setModal] = useState<{ mode: 'view' | 'edit' | 'new'; productId?: number } | null>(null)
  const modalProduct = products.find(product => product.id === modal?.productId)
  const [editingCategory, setEditingCategory] = useState(uncategorized)
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
  const pageCount = Math.max(1, Math.ceil(filtered.length / pageSize))
  const currentPage = Math.min(page, pageCount)
  const pageStart = (currentPage - 1) * pageSize
  const paginatedProducts = filtered.slice(pageStart, pageStart + pageSize)
  const hasFilters = Boolean(query || selectedCategories.length || stock !== 'all' || date)
  function clearFilters() { setQuery(''); setSelectedCategories([]); setStock('all'); setDate('') }
  function deleteCategory(category: string) {
    if (category === uncategorized || !categories.includes(category)) return
    const count = products.filter(product => product.category === category).length
    setProducts(current => current.map(product => product.category === category
      ? { ...product, category: uncategorized, updatedAt: today(), history: [{ author: currentUser.name, userId: currentUser.id, at: new Date().toISOString(), changes: [`Categoria: ${category} → ${uncategorized} (categoria excluída)`] }, ...(product.history ?? [])] }
      : product))
    setCategories(current => current.filter(item => item !== category))
    setSelectedCategories(current => [...new Set(current.map(item => item === category ? uncategorized : item))])
    setNotice(`${category}: categoria excluída. ${count} ${count === 1 ? 'produto movido' : 'produtos movidos'} para ${uncategorized}.`)
  }
  function saveCategory(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const input = event.currentTarget.elements.namedItem('categoryName') as HTMLInputElement
    const name = input.value.trim().replace(/\s+/g, ' ')
    if (!name || categories.some(category => normalize(category) === normalize(name))) {
      input.setCustomValidity(name ? 'Esta categoria já existe.' : 'Informe um nome para a categoria.')
      input.reportValidity()
      return
    }
    setCategories(current => [...current, name])
    setNotice(`${name}: categoria cadastrada nesta demonstração.`)
    setCategoryModal(false)
  }
  function openProductModal(mode: 'view' | 'edit' | 'new', product?: Product) {
    const currentProduct = products.find(item => item.id === product?.id)
    setEditingCategory(currentProduct?.category ?? uncategorized)
    setModal({ mode, productId: currentProduct?.id })
  }
  function saveProduct(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    const name = String(form.get('name')).trim()
    if (!name) return
    const author = currentUser
    if (modal?.mode === 'edit' && !author) return
    const product: Product = {
      id: modal?.productId ?? Math.max(0, ...products.map(item => item.id)) + 1,
      name, category: categories.includes(editingCategory) ? editingCategory : uncategorized, quantity: Number(form.get('quantity')), updatedAt: today(),
    }
    if (modal?.mode === 'edit' && modalProduct && author) {
      const changes: string[] = []
      if (product.name !== modalProduct.name) changes.push(`Nome: ${modalProduct.name} → ${product.name}`)
      if (product.category !== modalProduct.category) changes.push(`Categoria: ${modalProduct.category} → ${product.category}`)
      if (product.quantity !== modalProduct.quantity) changes.push(`Quantidade: ${modalProduct.quantity} → ${product.quantity}`)
      product.history = changes.length ? [{ author: author.name, userId: author.id, at: new Date().toISOString(), changes }, ...(modalProduct.history ?? [])] : modalProduct.history
      if (!changes.length) product.updatedAt = modalProduct.updatedAt
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
        <nav aria-label="Menu principal">
          <a className={`nav-link ${(screen !== 'users' || !isAdmin) ? 'active' : ''}`} href="#estoque" aria-current={(screen !== 'users' || !isAdmin) ? 'page' : undefined}><Icon name="box" /><span>Estoque</span></a>
          {isAdmin && <a className={`nav-link ${screen === 'users' ? 'active' : ''}`} href="#usuarios" aria-current={screen === 'users' ? 'page' : undefined}><Icon name="users" /><span>Usuários</span></a>}
        </nav>
        <div className="sidebar-footer account-footer"><div><strong>{currentUser.name}</strong><small>{currentUser.role}</small></div><button className="secondary-button" onClick={() => void logout()}>Sair</button>{accountError && <p role="alert" className="form-error">{accountError}</p>}</div>
      </aside>
      <main id="main" hidden={screen === 'users' && isAdmin}>
        <header className="page-header"><div><div className="eyebrow">CONTROLE DE ESTOQUE <span>/</span> PRODUTOS</div><h1>Gestão de produtos<span>.</span></h1><p>Todos os seus produtos, em um só lugar.</p></div><div className="header-actions"><button className="primary-button" onClick={() => openProductModal('new')}><Icon name="plus" /> Novo produto</button><button className="secondary-button" onClick={() => setCategoryModal(true)}><Icon name="plus" /> Nova categoria</button></div></header>
        <div className="inventory-summary"><span><span className="status-dot" /> Visão geral do estoque</span><span><strong>{products.length}</strong> produtos cadastrados <span className="summary-divider">/</span> <strong>{products.reduce((sum, product) => sum + product.quantity, 0).toLocaleString('pt-BR')}</strong> unidades</span></div>
        <section className="filter-panel" aria-label="Filtros de produtos">
          <div className="filter-topline">
            <details className="category-filter" ref={categoryFilter}>
              <summary><Icon name="filter" /><span>Categorias</span><span className="category-selection-count">{selectedCategories.length ? `${selectedCategories.length} selecionada${selectedCategories.length > 1 ? 's' : ''}` : 'Todas'}</span><span aria-hidden="true">⌄</span></summary>
              <div className="category-popover">
                <label className="search-field"><Icon name="search" /><input type="search" aria-label="Buscar categoria" placeholder="Buscar categoria..." value={categoryQuery} onChange={event => setCategoryQuery(event.target.value)} /></label>
                <div className="category-menu-heading"><span>{categories.length} categorias</span><button className="clear-button" disabled={!selectedCategories.length} onClick={() => setSelectedCategories([])}>Limpar seleção</button></div>
                <div className="category-list">{categories.filter(category => normalize(category).includes(normalize(categoryQuery.trim()))).map(category => <div className="category-item" key={category}>
                  <label className="category-option"><input type="checkbox" checked={selectedCategories.includes(category)} onChange={() => setSelectedCategories(current => current.includes(category) ? current.filter(item => item !== category) : [...current, category])} /><span>{category}</span><span className="category-product-count">{products.filter(product => product.category === category).length}</span></label>
                  {category !== uncategorized && <button className="delete-category-button" aria-label={`Excluir categoria ${category}`} title={`Excluir ${category} e mover seus produtos para ${uncategorized}`} onClick={() => deleteCategory(category)}><Icon name="trash" /></button>}
                </div>)}</div>
                {!categories.some(category => normalize(category).includes(normalize(categoryQuery.trim()))) && <p className="category-no-results" role="status">Nenhuma categoria encontrada.</p>}
              </div>
            </details>
            <button className="clear-button" onClick={clearFilters} disabled={!hasFilters}>Limpar filtros <Icon name="close" /></button>
          </div>
          <div className="filter-toolbar">
            <label className="search-field"><Icon name="search" /><input type="search" aria-label="Pesquisar por nome, ID ou categoria" placeholder="Pesquisar por nome, ID ou categoria..." value={query} onChange={event => setQuery(event.target.value)} /></label>
            <label className="toolbar-field"><span>Atualização</span><input aria-label="Data da última atualização" type="date" value={date} onChange={event => setDate(event.target.value)} /></label>
            <label className="toolbar-field"><span>Quantidade</span><select value={stock} onChange={event => setStock(event.target.value)}><option value="all">Todas</option><option value="available">Com estoque</option><option value="empty">Sem estoque</option></select></label>
          </div>
        </section>
        <section className="products-section" aria-labelledby="products-title">
          <div className="table-toolbar"><div><h2 id="products-title">Produtos no estoque</h2><span className="result-count" role="status">{filtered.length} {filtered.length === 1 ? 'resultado' : 'resultados'}</span></div><div className="sort-controls"><label htmlFor="sort">Ordenar por</label><select id="sort" value={sort} onChange={event => setSort(event.target.value as SortKey)}><option value="name">Nome</option><option value="category">Categoria</option><option value="updatedAt">Atualização</option><option value="quantity">Quantidade</option></select><button className="sort-direction" onClick={() => setDescending(!descending)} aria-label={descending ? 'Ordenação decrescente. Alterar para crescente' : 'Ordenação crescente. Alterar para decrescente'}>{descending ? '↓' : '↑'}</button></div></div>
          <div className="table-scroll"><table><thead><tr><th scope="col">ID</th><th scope="col">Nome do produto</th><th scope="col">Categoria</th><th scope="col">Última atualização</th><th scope="col" className="quantity-column">QTD</th><th scope="col" className="actions-column">Operações</th></tr></thead><tbody>{paginatedProducts.map(product => <tr key={product.id}><td className="product-id">#{String(product.id).padStart(3, '0')}</td><td className="product-name">{product.name}</td><td><span className="category-tag">{product.category}</span></td><td className="date-cell">{formatDate(product.updatedAt)}</td><td className="quantity-column"><span className={`quantity ${product.quantity === 0 ? 'empty-stock' : ''}`}>{product.quantity}</span></td><td><div className="row-actions"><button title="Visualizar produto" aria-label={`Visualizar ${product.name}`} onClick={() => openProductModal('view', product)}><Icon name="eye" /></button><button title="Editar produto" aria-label={`Editar ${product.name}`} onClick={() => openProductModal('edit', product)}><Icon name="edit" /></button></div></td></tr>)}</tbody></table>{!filtered.length && <div className="empty-state"><Icon name="search" /><h3>Nenhum produto encontrado</h3><p>Tente outro termo ou remova os filtros da pesquisa.</p><button className="secondary-button" onClick={clearFilters}>Limpar filtros</button></div>}</div>
          <footer className="table-footer">
            <span role="status">Exibindo {filtered.length ? pageStart + 1 : 0}–{Math.min(pageStart + pageSize, filtered.length)} de {filtered.length} produtos{hasFilters && ` (${products.length} no total)`}</span>
            <nav className="pagination" aria-label="Paginação dos produtos">
              <label htmlFor="page-size">Por página</label>
              <select id="page-size" value={pageSize} onChange={event => setPageSize(Number(event.target.value))}>
                {[50, 100, 200].map(size => <option key={size} value={size}>{size}</option>)}
              </select>
              <button className="secondary-button" disabled={currentPage === 1} onClick={() => setPage(currentPage - 1)}>Anterior</button>
              <span>Página {currentPage} de {pageCount}</span>
              <button className="secondary-button" disabled={currentPage === pageCount} onClick={() => setPage(currentPage + 1)}>Próxima</button>
            </nav>
          </footer>
        </section>
        <p className="save-notice" role="status">{notice}</p>
        <footer className="page-footer"><span>iStock <span> / </span> Organização que faz a diferença.</span><span>GESTÃO DE ESTOQUE</span></footer>
      </main>
      {isAdmin && <Users hidden={screen !== 'users'} users={users} setUsers={setUsers} refreshSession={refreshSession} />}
      {categoryModal && <dialog ref={categoryDialog} onCancel={() => setCategoryModal(false)} onClose={() => setCategoryModal(false)} aria-labelledby="category-dialog-title">
        <div className="dialog-heading"><h2 id="category-dialog-title">Nova categoria</h2><button className="icon-button" aria-label="Fechar" onClick={() => setCategoryModal(false)}><Icon name="close" /></button></div>
        <form onSubmit={saveCategory}>
          <label>Nome da categoria<input name="categoryName" required maxLength={60} autoFocus onChange={event => event.currentTarget.setCustomValidity('')} /></label>
          <p className="form-hint">Dados de demonstração. As alterações são mantidas até recarregar a página.</p>
          <div className="dialog-actions"><button type="button" className="secondary-button" onClick={() => setCategoryModal(false)}>Cancelar</button><button className="primary-button" type="submit">Cadastrar categoria</button></div>
        </form>
      </dialog>}
      {modal && <dialog ref={dialog} onCancel={() => setModal(null)} onClose={() => setModal(null)} aria-labelledby="dialog-title"><div className="dialog-heading"><h2 id="dialog-title">{modal.mode === 'new' ? 'Novo produto' : modal.mode === 'edit' ? 'Editar produto' : 'Detalhes do produto'}</h2><button className="icon-button" aria-label="Fechar" onClick={() => setModal(null)}><Icon name="close" /></button></div>{modal.mode === 'view' && modalProduct ? <><dl className="product-details"><dt>ID</dt><dd>#{String(modalProduct.id).padStart(3, '0')}</dd><dt>Nome</dt><dd>{modalProduct.name}</dd><dt>Categoria</dt><dd>{modalProduct.category}</dd><dt>Quantidade</dt><dd>{modalProduct.quantity} unidades</dd><dt>Última atualização</dt><dd>{formatDate(modalProduct.updatedAt)}</dd></dl><ProductHistory entries={modalProduct.history ?? []} /><button className="primary-button" onClick={() => openProductModal('edit', modalProduct)}><Icon name="edit" />Editar produto</button></> : <form key={modalProduct?.id ?? 'new'} onSubmit={saveProduct}><label>Nome do produto<input name="name" required maxLength={120} pattern=".*\S.*" title="Informe um nome para o produto" defaultValue={modalProduct?.name} autoFocus /></label><label>Categoria<select name="category" value={editingCategory} onChange={event => setEditingCategory(event.target.value)}>{categories.map(category => <option key={category} value={category}>{category}</option>)}</select></label><label>Quantidade<input type="number" name="quantity" min="0" max="999999999" step="1" required defaultValue={modalProduct?.quantity ?? 0} /></label><p className="form-hint">Dados de demonstração. As alterações são mantidas até recarregar a página.</p><div className="dialog-actions"><button type="button" className="secondary-button" onClick={() => setModal(null)}>Cancelar</button><button className="primary-button" type="submit">{modal.mode === 'new' ? 'Cadastrar produto' : 'Salvar alterações'}</button></div></form>}</dialog>}
    </div>
  )
}
export default App
