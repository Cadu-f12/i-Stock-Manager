import { useEffect, useState } from 'react'
import { domainApi } from './api'
import { categoryTotals } from './reportData'

type Category = { name: string }
type Distribution = { total: number; groups: ReturnType<typeof categoryTotals> }
const color = (index: number) => `hsl(${(index * 137.508 + 215) % 360} 60% 44%)`
const percent = new Intl.NumberFormat('pt-BR', { style: 'percent', maximumFractionDigits: 1 })

export default function CategoryReport() {
  const [data, setData] = useState<Distribution | null>(null)
  const [error, setError] = useState('')
  const [retry, setRetry] = useState(0)
  useEffect(() => {
    const controller = new AbortController()
    const options = { signal: controller.signal }
    setError('')
    Promise.all([
      domainApi<Category[]>('/pastilles/categories', options),
      domainApi<{ category: Category | null }[]>('/pastilles', options),
    ]).then(([categories, items]) => {
      if (controller.signal.aborted) return
      const groups = categoryTotals(items.map(item => ({ category: item.category?.name ?? 'Sem categoria' })), categories.map(c => c.name))
      setData({ total: items.length, groups })
    }).catch(e => { if (!controller.signal.aborted) setError(e.message) })
    return () => controller.abort()
  }, [retry])

  let offset = 0
  const slices = data?.groups.filter(group => group.count > 0).map((group, index) => {
    const start = offset
    offset += group.count / data.total * 100
    return `${color(index)} ${start}% ${offset}%`
  })
  return <details className="report-card chart-panel" open>
    <summary><h2>Produtos por categoria</h2></summary>
    {error ? <p role="alert">{error} <button onClick={() => setRetry(n => n + 1)}>Tentar novamente</button></p> :
      !data ? <p role="status">Carregando categorias e pastilhas.</p> : <>
        <p className="report-description">Total de pastilhas cadastradas: {data.total.toLocaleString('pt-BR')}. Cada cadastro conta uma vez, independentemente do saldo.</p>
        {data.groups.length > 0 ? <div className="distribution-layout">
          {data.total > 0 && <div className="category-pie" role="img" aria-label="Distribuição das pastilhas por categoria. Valores na legenda."
            style={{ background: `conic-gradient(${slices?.join(',')})` }} />}
          <ul className="category-legend" aria-label="Quantidades por categoria">{data.groups.map((group, index) =>
            <li key={group.name}><span className="chart-swatch" style={{ background: color(index) }} aria-hidden="true" />
              <span>{group.name}</span><strong>{group.count}</strong><span>{percent.format(data.total ? group.count / data.total : 0)}</span></li>
          )}</ul>
        </div> : <p className="empty-state">Nenhuma categoria ou pastilha cadastrada.</p>}
      </>}
  </details>
}
