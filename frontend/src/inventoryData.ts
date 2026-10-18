import type { ProductChange } from './ProductHistory'
export type Product = { id: number; name: string; category: string; quantity: number; updatedAt: string; history?: ProductChange[] }
export const uncategorized = 'Sem categoria'
export const initialCategories = [uncategorized, 'Ferramentas', 'Elétrica', 'Hidráulica', 'Fixadores', 'Pintura', 'Segurança']
export const initialProducts: Product[] = [
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
