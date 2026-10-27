import type { Product } from './inventoryData'

const localDate = new Intl.DateTimeFormat('en-CA', { timeZone: 'America/Sao_Paulo', year: 'numeric', month: '2-digit' })
export function reportDate(at: string) {
  const date = new Date(at)
  if (!Number.isFinite(date.getTime())) return null
  const parts = localDate.formatToParts(date)
  return { year: Number(parts.find(part => part.type === 'year')?.value), month: Number(parts.find(part => part.type === 'month')?.value) - 1 }
}

// InventoryService stores movements as descriptions; only explicit movements count.
export function movementAmount(description: string) {
  const match = /^(Entrada|Saída) de ([\d.E+-]+) unidade\(s\): /.exec(description)
  if (!match) return null
  const quantity = Number(match[2])
  return Number.isSafeInteger(quantity) && quantity > 0 ? { direction: match[1] === 'Entrada' ? 'incoming' as const : 'outgoing' as const, quantity } : null
}

export function categoryTotals(products: Product[]) {
  const totals = new Map<string, number>()
  for (const product of products) totals.set(product.category, (totals.get(product.category) ?? 0) + 1)
  return [...totals].map(([name, count]) => ({ name, count })).sort((a, b) => b.count - a.count || a.name.localeCompare(b.name, 'pt-BR'))
}

export function movementYears(products: Product[], currentYear: number) {
  const years = new Set([currentYear])
  for (const product of products) for (const entry of product.history ?? []) {
    const date = reportDate(entry.at)
    if (date && entry.changes.some(change => movementAmount(change))) years.add(date.year)
  }
  return [...years].sort((a, b) => b - a)
}

export function monthlyTotals(products: Product[], year: number, category = '', productId = '') {
  const months = Array.from({ length: 12 }, () => ({ incoming: 0, outgoing: 0 }))
  for (const product of products) {
    if ((category && product.category !== category) || (productId && String(product.id) !== productId)) continue
    for (const entry of product.history ?? []) {
      const date = reportDate(entry.at)
      if (!date || date.year !== year) continue
      for (const change of entry.changes) {
        const movement = movementAmount(change)
        if (movement) months[date.month][movement.direction] += movement.quantity
      }
    }
  }
  return months
}
