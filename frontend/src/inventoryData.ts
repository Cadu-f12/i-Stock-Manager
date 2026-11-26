import type { ProductChange } from './ProductHistory'
export type Product = { id: number; version: number; name: string; category: string; quantity: number; updatedAt: string; history?: ProductChange[] }
export type Inventory = { products: Product[]; categories: string[] }
export const uncategorized = 'Sem categoria'
