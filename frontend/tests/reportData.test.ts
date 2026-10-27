import { test } from 'node:test'
import assert from 'node:assert/strict'
import { categoryTotals, monthlyTotals, movementYears, movementAmount } from '../src/reportData.ts'

const products = [
  { id: 1, version: 0, name: 'Parafuso', category: 'Ferragens', quantity: 97, updatedAt: '2026-02-01', history: [
    { author: 'Admin', at: '2026-02-01T02:30:00Z', changes: ['Entrada de 10 unidade(s): 90 → 100'] },
    { author: 'Admin', at: '2026-02-01T03:30:00Z', changes: ['Saída de 3 unidade(s): 100 → 97'] },
    { author: 'Admin', at: '2026-01-01T02:00:00Z', changes: ['Entrada de 2 unidade(s): 88 → 90'] },
    { author: 'Admin', at: '2026-01-12T12:00:00Z', changes: ['Produto cadastrado com 50 unidade(s).', 'Quantidade: 50 → 90', 'Nome: Entrada de 50 unidade(s): → Outro'] },
  ] },
  { id: 2, version: 0, name: 'Tinta', category: 'Pintura', quantity: 1000, updatedAt: '2026-01-01', history: [
    { author: 'Admin', at: '2026-01-10T12:00:00Z', changes: ['Entrada de 7 unidade(s): 0 → 7', 'Saída de 2 unidade(s): 7 → 5'] },
  ] },
  { id: 3, version: 0, name: 'Prego', category: 'Ferragens', quantity: 0, updatedAt: '2026-01-01' },
]

test('pizza conta produtos e inclui produtos sem estoque', () => {
  assert.deepEqual(categoryTotals(products), [{ name: 'Ferragens', count: 2 }, { name: 'Pintura', count: 1 }])
  assert.deepEqual(categoryTotals([]), [])
})
test('soma unidades em Brasília e ignora cadastro e edições', () => {
  const totals = monthlyTotals(products, 2026)
  assert.equal(totals.length, 12)
  assert.deepEqual(totals[0], { incoming: 17, outgoing: 2 })
  assert.deepEqual(totals[1], { incoming: 0, outgoing: 3 })
  assert.deepEqual(totals[2], { incoming: 0, outgoing: 0 })
  assert.deepEqual(monthlyTotals(products, 2025)[11], { incoming: 2, outgoing: 0 })
  assert.deepEqual(movementYears(products, 2027), [2027, 2026, 2025])
})
test('combina filtros de categoria e produto e retorna zero sem resultados', () => {
  assert.deepEqual(monthlyTotals(products, 2026, 'Ferragens')[0], { incoming: 10, outgoing: 0 })
  assert.deepEqual(monthlyTotals(products, 2026, '', '2')[0], { incoming: 7, outgoing: 2 })
  assert.deepEqual(monthlyTotals(products, 2026, 'Ferragens', '2')[0], { incoming: 0, outgoing: 0 })
  assert.ok(monthlyTotals([], 2026).every(month => month.incoming === 0 && month.outgoing === 0))
})
test('interpreta quantidades BigDecimal e rejeita valores inválidos', () => {
  assert.deepEqual(movementAmount('Entrada de 1E+2 unidade(s): 0 → 100'), { direction: 'incoming', quantity: 100 })
  assert.equal(movementAmount('Entrada de -2 unidade(s): 2 → 0'), null)
  assert.equal(movementAmount('Entrada de 1.5 unidade(s): 0 → 1.5'), null)
})
