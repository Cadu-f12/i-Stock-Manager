import { useEffect, useState } from 'react'
import { domainApi } from './api'
import type { ReactNode } from 'react'
import Select from './Select'
import MonthlyChart from './MonthlyChart'
import { reportDate } from './reportData'

type Row = { quantity: number; date_time: string; reason: { type: 'IN' | 'OUT' }; pastille: { id: number } }
type Item = { id: number; sku_code: string; description: string | null; category: { id: number; name: string } | null }

export default function PastilleReport({ children }: { children: ReactNode }) {
  const currentYear = String(reportDate(new Date().toISOString())!.year)
  const [year, setYear] = useState(currentYear)
  const [category, setCategory] = useState('')
  const [product, setProduct] = useState('')
  const [items, setItems] = useState<Item[]>([])
  const [rows, setRows] = useState<{ year: string; movements: Row[] } | null>(null)
  const [catalogError, setCatalogError] = useState('')
  const [error, setError] = useState('')
  const [retry, setRetry] = useState(0)
  useEffect(() => {
    const controller = new AbortController()
    setCatalogError('')
    domainApi<Item[]>('/pastilles', { signal: controller.signal }).then(setItems)
      .catch(e => { if (!controller.signal.aborted) setCatalogError(e.message) })
    return () => controller.abort()
  }, [retry])
  const validYear = /^[1-9]\d{3}$/.test(year)
  useEffect(() => {
    const controller = new AbortController()
    setError('')
    if (validYear) {
      domainApi<Row[]>(`/movement?from=${year}-01-01&to=${year}-12-31`, { signal: controller.signal })
        .then(movements => { if (!controller.signal.aborted) setRows({ year, movements }) })
        .catch(e => { if (!controller.signal.aborted) setError(e.message) })
    }
    return () => controller.abort()
  }, [year, validYear, retry])
  const categoryKey = (item: Item) => String(item.category?.id ?? 'none')
  const categories = new Map(items.map(item => [categoryKey(item), item.category?.name ?? 'Sem categoria']))
  const available = items.filter(item => !category || categoryKey(item) === category)
  const ids = new Set(available.map(item => item.id))
  const totals = Array.from({ length: 12 }, () => ({ incoming: 0, outgoing: 0 }))
  for (const row of rows?.movements ?? []) {
    if (category && !ids.has(row.pastille.id) || product && product !== String(row.pastille.id)) continue
    const month = Number(row.date_time.slice(5, 7)) - 1
    totals[month][row.reason.type === 'IN' ? 'incoming' : 'outgoing'] += row.quantity
  }
  return <>
    <div className="report-filters">
      {children}
      <label>Ano<input type="number" min="1000" max="9999" value={year} onChange={e => setYear(e.target.value)} /></label>
      <label>Categoria<Select value={category} onChange={e => { setCategory(e.target.value); setProduct('') }}>
        <option value="">Todas as categorias</option>
        {Array.from(categories, ([id, name]) => <option key={id} value={id}>{name}</option>)}
      </Select></label>
      <label>Produto<Select value={product} onChange={e => setProduct(e.target.value)}>
        <option value="">Todos os produtos</option>
        {available.map(item => <option key={item.id} value={item.id}>{item.sku_code} {item.description}</option>)}
      </Select></label>
      <button className="secondary-button" onClick={() => { setYear(currentYear); setCategory(''); setProduct('') }}>Limpar filtros</button>
    </div>
    {error || catalogError ? <p role="alert">{error || catalogError} <button onClick={() => setRetry(n => n + 1)}>Tentar novamente</button></p> :
      rows?.year !== year && <p role="status">{validYear ? 'Carregando.' : 'Informe o ano com quatro dígitos.'}</p>}
    {rows && <div style={{ visibility: rows.year === year && !error && !catalogError ? 'visible' : 'hidden' }}>
      <MonthlyChart totals={totals} year={rows.year} />
    </div>}
  </>
}
