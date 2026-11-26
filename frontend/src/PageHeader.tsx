import type { ReactNode } from 'react'

export default function PageHeader({ section, title, description, children }: {
  section: string; title: string; description: string; children?: ReactNode
}) {
  return <header className="page-header">
    <div>
      <div className="eyebrow">GERENCIAMENTO <span>/</span> {section.toLocaleUpperCase('pt-BR')}</div>
      <h1>{title}<span>.</span></h1>
      <p>{description}</p>
    </div>
    {children}
  </header>
}
