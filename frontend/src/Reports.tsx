import { useMemo, useState } from 'react'
import type { Product } from './inventoryData'
import { categoryTotals, monthlyTotals, movementYears, reportDate } from './reportData'
import './Reports.css'

const months = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez']
const number = (value: number) => value.toLocaleString('pt-BR')
const color = (index: number) => `hsl(${(index * 137.508 + 215) % 360} 60% 44%)`

export default function Reports({ products, categories }: { products: Product[]; categories: string[] }) {
  const currentYear = reportDate(new Date().toISOString())!.year
  const [year, setYear] = useState(currentYear)
  const [category, setCategory] = useState('')
  const [productId, setProductId] = useState('')
  const distribution = useMemo(() => categoryTotals(products), [products])
  const years = useMemo(() => movementYears(products, currentYear), [products, currentYear])
  const totals = useMemo(() => monthlyTotals(products, year, category, productId), [products, year, category, productId])
  const availableProducts = products.filter(product => !category || product.category === category)
  const incoming = totals.reduce((sum, month) => sum + month.incoming, 0)
  const outgoing = totals.reduce((sum, month) => sum + month.outgoing, 0)
  const maximum = Math.max(1, ...totals.flatMap(month => [month.incoming, month.outgoing]))
  const axisMax = Math.ceil(maximum / 4) * 4
  let offset = 0
  const slices = distribution.map((item, index) => {
    const start = offset
    offset += item.count / products.length * 100
    return `${color(index)} ${start}% ${offset}%`
  })

  return <main id="reports">
    <header className="page-header"><div><div className="eyebrow">CONTROLE DE ESTOQUE <span>/</span> RELATÓRIOS</div><h1>Relatórios do estoque<span>.</span></h1><p>Acompanhe seus produtos e o fluxo mensal de unidades.</p></div></header>
    <section className="report-card" aria-labelledby="category-chart-title">
      <h2 id="category-chart-title">Produtos por categoria</h2>
      <p className="report-description">Distribuição dos {number(products.length)} produtos cadastrados, independentemente do saldo em estoque.</p>
      {products.length ? <div className="distribution-layout">
        <div className="category-pie" role="img" aria-label={`Distribuição de ${products.length} produtos por categoria. Quantidades e percentuais na legenda ao lado.`} style={{ background: `conic-gradient(${slices.join(',')})` }} />
        <ul className="category-legend" aria-label="Quantidades por categoria">{distribution.map((item, index) => <li key={item.name}><span className="chart-swatch" style={{ background: color(index) }} aria-hidden="true" /><span>{item.name}</span><strong>{number(item.count)}</strong><span>{(item.count / products.length * 100).toLocaleString('pt-BR', { maximumFractionDigits: 1 })}%</span></li>)}</ul>
      </div> : <p className="empty-state" role="status">Nenhum produto cadastrado para exibir no gráfico.</p>}
    </section>
    <section className="report-card" aria-labelledby="movement-chart-title">
      <h2 id="movement-chart-title">Entradas e saídas por mês</h2>
      <p className="report-description">Quantidade de unidades movimentadas. Os filtros abaixo se aplicam a este gráfico.</p>
      <div className="report-filters">
        <label>Ano<select value={year} onChange={event => setYear(Number(event.target.value))}>{years.map(value => <option key={value}>{value}</option>)}</select></label>
        <label>Categoria<select value={category} onChange={event => { setCategory(event.target.value); setProductId('') }}><option value="">Todas as categorias</option>{categories.map(value => <option key={value}>{value}</option>)}</select></label>
        <label>Produto<select value={productId} onChange={event => setProductId(event.target.value)}><option value="">Todos os produtos</option>{availableProducts.map(product => <option key={product.id} value={product.id}>{product.name} (#{product.id})</option>)}</select></label>
        <button className="secondary-button" onClick={() => { setCategory(''); setProductId(''); setYear(currentYear) }}>Limpar filtros</button>
      </div>
      <div className="movement-totals" aria-live="polite"><span><i className="chart-swatch incoming" />Entradas: <strong>{number(incoming)}</strong></span><span><i className="chart-swatch outgoing" />Saídas: <strong>{number(outgoing)}</strong></span><span>Saldo das movimentações: <strong>{number(incoming - outgoing)}</strong></span></div>
      {!incoming && !outgoing && <p className="report-description" role="status">Nenhuma movimentação encontrada para os filtros selecionados.</p>}
      <div className="monthly-chart-scroll">
        <svg className="monthly-chart" viewBox="0 0 960 320" role="img" aria-labelledby="monthly-chart-title monthly-chart-desc">
          <title id="monthly-chart-title">Entradas e saídas mensais em {year}</title><desc id="monthly-chart-desc">Comparação em unidades. Os valores exatos estão disponíveis na tabela abaixo.</desc>
          {[0, 1, 2, 3, 4].map(tick => <g key={tick}><line x1="100" x2="940" y1={265 - tick * 55} y2={265 - tick * 55} className="chart-grid" /><text x="90" y={269 - tick * 55} textAnchor="end">{number(axisMax * tick / 4)}</text></g>)}
          {totals.map((month, index) => <g key={months[index]}>
            <rect x={113 + index * 70} y={265 - month.incoming / axisMax * 220} width="20" height={month.incoming / axisMax * 220} fill="#2563eb"><title>{months[index]}: {number(month.incoming)} entradas</title></rect>
            <rect x={137 + index * 70} y={265 - month.outgoing / axisMax * 220} width="20" height={month.outgoing / axisMax * 220} fill="#c2410c"><title>{months[index]}: {number(month.outgoing)} saídas</title></rect>
            <text x={135 + index * 70} y="290" textAnchor="middle">{months[index]}</text>
          </g>)}
          <text x="100" y="22">Unidades</text>
        </svg>
      </div>
      <details className="report-table"><summary>Ver valores mensais em tabela</summary><div className="table-scroll"><table><caption>Movimentações em {year} para os filtros selecionados (unidades)</caption><thead><tr><th scope="col">Mês</th><th scope="col">Entradas</th><th scope="col">Saídas</th><th scope="col">Saldo</th></tr></thead><tbody>{totals.map((month, index) => <tr key={months[index]}><th scope="row">{months[index]}</th><td>{number(month.incoming)}</td><td>{number(month.outgoing)}</td><td>{number(month.incoming - month.outgoing)}</td></tr>)}</tbody></table></div></details>
      <p className="report-description">Considera as entradas e saídas registradas no histórico, no horário de Brasília. Saldo inicial e edições manuais de quantidade não entram no cálculo. A categoria utilizada é a categoria atual do produto.</p>
    </section>
  </main>
}
