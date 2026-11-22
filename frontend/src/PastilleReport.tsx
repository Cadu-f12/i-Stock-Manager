import { useEffect, useState } from 'react'
import { domainApi } from './api'

type Row = { quantity: number; date_time: string; reason: { type: 'IN' | 'OUT' } }
export default function PastilleReport() {
  const [year, setYear] = useState(String(new Date().getFullYear()))
  const [movements, setMovements] = useState<Row[] | null>(null)
  const [error, setError] = useState('')
  const [retry, setRetry] = useState(0)
  useEffect(() => {
    const controller = new AbortController()
    setError('')
    setMovements(null)
    if (/^[1-9]\d{3}$/.test(year))
      domainApi<Row[]>(`/movement?from=${year}-01-01&to=${year}-12-31`, { signal: controller.signal })
        .then(setMovements).catch(e => { if (!controller.signal.aborted) setError(e.message) })
    return () => controller.abort()
  }, [year, retry])
  const totals = Array.from({ length: 12 }, () => ({ IN: 0, OUT: 0 }))
  for (const movement of movements ?? []) {
    const month = Number(movement.date_time.slice(5, 7)) - 1
    totals[month][movement.reason.type] += movement.quantity
  }
  return <main><header className="page-header"><h1>Movimentações de pastilhas</h1></header>
    <label className="toolbar-field">Ano<input type="number" min="1000" max="9999" value={year} onChange={e => setYear(e.target.value)}/></label>
    {error ? <p role="alert">{error} <button onClick={() => setRetry(n => n + 1)}>Tentar novamente</button></p> : movements ?
      <section className="products-section"><table><thead><tr><th>Mês</th><th>Entradas (un.)</th><th>Saídas (un.)</th></tr></thead><tbody>{totals.map((t,i) => <tr key={i}><td>{new Intl.DateTimeFormat('pt-BR', { month: 'long' }).format(new Date(2000,i,1))}</td><td>{t.IN}</td><td>{t.OUT}</td></tr>)}</tbody></table></section>
      : <p role="status">{/^[1-9]\d{3}$/.test(year) ? 'Carregando…' : 'Informe o ano com quatro dígitos.'}</p>}
  </main>
}
