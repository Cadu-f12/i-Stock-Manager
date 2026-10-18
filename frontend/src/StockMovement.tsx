import { useEffect, useRef, useState } from 'react'
import type { FormEvent } from 'react'
import type { Product } from './inventoryData'
import Icon from './Icon'

export type StockDirection = 'in' | 'out'
export const MAX_STOCK = 999999999

export default function StockMovement({ product, direction, onClose, onConfirm }: {
  product: Product
  direction: StockDirection
  onClose: () => void
  onConfirm: (quantity: number) => void
}) {
  const dialog = useRef<HTMLDialogElement>(null)
  const [amount, setAmount] = useState('1')
  const incoming = direction === 'in'
  const limit = incoming ? MAX_STOCK - product.quantity : product.quantity
  const quantity = Number(amount)
  const valid = amount.trim() !== '' && Number.isSafeInteger(quantity) && quantity > 0 && quantity <= limit
  const balance = product.quantity + (incoming ? quantity : -quantity)
  useEffect(() => { dialog.current?.showModal() }, [])

  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (valid) onConfirm(quantity)
  }

  return <dialog ref={dialog} onCancel={onClose} onClose={onClose} aria-labelledby="stock-movement-title" aria-describedby="stock-movement-product">
    <div className="dialog-heading"><h2 id="stock-movement-title">{incoming ? 'Adicionar ao estoque' : 'Retirar do estoque'}</h2><button type="button" className="icon-button" aria-label="Fechar" onClick={onClose}><Icon name="close" /></button></div>
    <p className="movement-product" id="stock-movement-product"><strong>{product.name}</strong><span>#{String(product.id).padStart(3, '0')} · {product.category}</span></p>
    <form onSubmit={submit}>
      <label htmlFor="movement-quantity">{incoming ? 'Quantas unidades deseja adicionar?' : 'Quantas unidades deseja retirar?'}</label>
      <div className="movement-stepper">
        <button type="button" aria-label="Diminuir quantidade em uma unidade" disabled={!Number.isSafeInteger(quantity) || quantity <= 1} onClick={() => setAmount(String(quantity - 1))}>−</button>
        <input id="movement-quantity" name="quantity" type="number" inputMode="numeric" min="1" max={limit} step="1" required autoFocus value={amount} onFocus={event => event.target.select()} onChange={event => setAmount(event.target.value)} aria-describedby="movement-feedback" aria-invalid={!valid} />
        <button type="button" aria-label="Aumentar quantidade em uma unidade" disabled={!Number.isSafeInteger(quantity) || quantity >= limit} onClick={() => setAmount(String(quantity + 1))}>+</button>
      </div>
      <div className="movement-preview" aria-live="polite" aria-atomic="true">
        <div><span>Estoque atual</span><strong>{product.quantity.toLocaleString('pt-BR')}</strong></div>
        <span className="movement-arrow" aria-hidden="true">→</span>
        <div><span>Após {incoming ? 'a entrada' : 'a saída'}</span><strong className={valid ? 'movement-balance' : ''}>{valid ? balance.toLocaleString('pt-BR') : '—'}</strong></div>
      </div>
      <p id="movement-feedback" className={valid ? 'form-hint' : 'form-error'}>{valid ? `${incoming ? '+' : '−'}${quantity.toLocaleString('pt-BR')} ${quantity === 1 ? 'unidade' : 'unidades'} no estoque.${balance === 0 ? ' O produto ficará sem estoque.' : ''}` : !incoming && quantity > limit ? `Estoque insuficiente. Você pode retirar até ${limit.toLocaleString('pt-BR')} unidades.` : `Informe uma quantidade inteira entre 1 e ${limit.toLocaleString('pt-BR')}.`}</p>
      <p className="form-hint">Dados de demonstração. As alterações são mantidas até recarregar a página.</p>
      <div className="dialog-actions"><button type="button" className="secondary-button" onClick={onClose}>Cancelar</button><button type="submit" className="primary-button" disabled={!valid}>Confirmar {incoming ? 'entrada' : 'saída'}</button></div>
    </form>
  </dialog>
}
