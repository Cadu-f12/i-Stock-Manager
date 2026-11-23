import { useEffect, useId, useRef, useState } from 'react'
import type { SelectHTMLAttributes } from 'react'

export default function Select(props: SelectHTMLAttributes<HTMLSelectElement>) {
  const field = useRef<HTMLSelectElement>(null)
  const menu = useRef<HTMLDivElement>(null)
  const listId = useId()
  const [open, setOpen] = useState(false)
  const [active, setActive] = useState(-1)
  const options = Array.from(field.current?.options ?? [])

  useEffect(() => {
    if (!open) return
    function dismiss(event: Event) {
      if (event.target instanceof Node && menu.current?.contains(event.target)) return
      menu.current?.hidePopover()
    }
    window.addEventListener('resize', dismiss)
    window.addEventListener('scroll', dismiss, true)
    return () => {
      window.removeEventListener('resize', dismiss)
      window.removeEventListener('scroll', dismiss, true)
    }
  }, [open])

  function show() {
    const select = field.current!
    const popup = menu.current!
    const rect = select.getBoundingClientRect()
    const below = window.innerHeight - rect.bottom - 10
    const above = rect.top - 10
    const upwards = below < 180 && above > below
    Object.assign(popup.style, {
      left: rect.left + 'px', width: rect.width + 'px',
      top: upwards ? 'auto' : rect.bottom + 4 + 'px',
      bottom: upwards ? window.innerHeight - rect.top + 4 + 'px' : 'auto',
      maxHeight: Math.max(32, Math.min(240, upwards ? above : below)) + 'px',
    })
    setActive(select.selectedIndex)
    setOpen(true)
    popup.showPopover()
    requestAnimationFrame(() => popup.children[select.selectedIndex]?.scrollIntoView({ block: 'nearest' }))
  }

  function choose(index: number) {
    const select = field.current!
    if (index < 0 || !select.options[index] || select.options[index].disabled) return
    select.selectedIndex = index
    select.dispatchEvent(new Event('change', { bubbles: true }))
    menu.current?.hidePopover()
    select.focus()
  }

  return <>
    <select {...props} ref={field} aria-expanded={open} aria-controls={open ? listId : undefined}
      aria-activedescendant={open && active >= 0 ? listId + '-' + active : undefined}
      onPointerDown={event => {
        if (event.button !== 0 || !menu.current?.showPopover) return
        event.preventDefault()
        event.currentTarget.focus()
      }}
      onClick={event => {
        if (!menu.current?.showPopover) return
        event.preventDefault()
        if (menu.current.matches(':popover-open')) menu.current.hidePopover()
        else show()
      }}
      onBlur={() => menu.current?.hidePopover?.()}
      onKeyDown={event => {
        if (!menu.current?.showPopover) return
        if (['Enter', ' ', 'F4'].includes(event.key) || (event.altKey && event.key === 'ArrowDown')) {
          event.preventDefault()
          if (open) choose(active)
          else show()
        } else if (open && event.key === 'Escape') {
          event.preventDefault()
          event.stopPropagation()
          menu.current.hidePopover()
        } else if (open && ['ArrowDown', 'ArrowUp', 'Home', 'End'].includes(event.key)) {
          event.preventDefault()
          const last = options.length - 1
          const next = event.key === 'Home' ? 0 : event.key === 'End' ? last :
            Math.max(0, Math.min(last, active + (event.key === 'ArrowDown' ? 1 : -1)))
          setActive(next)
          menu.current.children[next]?.scrollIntoView({ block: 'nearest' })
        } else if (open && event.key.length === 1) {
          menu.current.hidePopover()
        }
      }}
    />
    <div popover="auto" ref={menu} id={listId} className="site-options" role="listbox"
      aria-label={field.current?.labels?.[0]?.firstChild?.textContent || 'Opções'}
      onToggle={event => setOpen(event.newState === 'open')}
      onClick={event => event.preventDefault()}
      onPointerDown={event => event.preventDefault()}>
      {open && options.map((option, index) => <div key={index} id={listId + '-' + index} role="option"
        aria-selected={option.selected} data-active={index === active || undefined}
        onPointerMove={() => setActive(index)} onClick={() => choose(index)}>{option.label}</div>)}
    </div>
  </>
}
