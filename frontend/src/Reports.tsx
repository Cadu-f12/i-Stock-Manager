import CategoryReport from './CategoryReport'
import MonthlyChart from './MonthlyChart'
import PastilleReport from './PastilleReport'
import type { ReactNode } from 'react'
import Select from './Select'
import { useMemo, useState } from 'react'
import type { Product } from './inventoryData'
import { monthlyTotals, movementYears, reportDate } from './reportData'
import './Reports.css'

export default function Reports({ products, categories, archiveStatus }: { products: Product[]; categories: string[]; archiveStatus?: ReactNode }) {
  const [source, setSource] = useState('pastilles')
  const currentYear = reportDate(new Date().toISOString())!.year
  const [year, setYear] = useState(currentYear)
  const [category, setCategory] = useState('')
  const [productId, setProductId] = useState('')
  const years = useMemo(() => movementYears(products, currentYear), [products, currentYear])
  const totals = useMemo(() => monthlyTotals(products, year, category, productId), [products, year, category, productId])
  const availableProducts = products.filter(product => !category || product.category === category)
  const sourceField = <label>Estoque<Select value={source} onChange={event => setSource(event.target.value)}>
          <option value="pastilles">Pastilhas</option><option value="archive">Estoque anterior</option>
        </Select></label>

  return <>
      <CategoryReport />
      <details className="report-card chart-panel" open>
        <summary><h2>Entradas e saídas por mês</h2></summary>

        {source === 'pastilles' ? <PastilleReport>{sourceField}</PastilleReport> : archiveStatus ? <>
          <div className="report-filters">{sourceField}</div>{archiveStatus}
        </> : <>
        <div className="report-filters">
          {sourceField}
          <label>Ano<Select value={year} onChange={event => setYear(Number(event.target.value))}>{years.map(value => <option key={value}>{value}</option>)}</Select></label>
          <label>Categoria<Select value={category} onChange={event => { setCategory(event.target.value); setProductId('') }}><option value="">Todas as categorias</option>{categories.map(value => <option key={value}>{value}</option>)}</Select></label>
          <label>Produto<Select value={productId} onChange={event => setProductId(event.target.value)}><option value="">Todos os produtos</option>{availableProducts.map(product => <option key={product.id} value={product.id}>{product.name} (#{product.id})</option>)}</Select></label>
          <button className="secondary-button" onClick={() => { setCategory(''); setProductId(''); setYear(currentYear) }}>Limpar filtros</button>
        </div>
          <MonthlyChart totals={totals} year={year} />
        <p className="report-description">Considera as entradas e saídas registradas no histórico, no horário de Brasília. Saldo inicial e edições manuais de quantidade não entram no cálculo. A categoria utilizada é a categoria atual do produto.</p>
        </>}
      </details>
  </>
}
