export type ProductChange = { author: string; userId?: number; at: string; changes: string[] }

const timestamp = new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short', timeZone: 'America/Sao_Paulo' })

export default function ProductHistory({ entries }: { entries: ProductChange[] }) {
  return <section className="product-history" aria-labelledby="product-history-title">
    <h3 id="product-history-title">Histórico de alterações</h3>
    {entries.length ? <ol>{entries.map((entry, index) => <li key={`${entry.at}-${index}`}>
      <div><strong>{entry.author}</strong><time dateTime={entry.at}>{timestamp.format(new Date(entry.at))}</time></div>
      {entry.changes.map((change, changeIndex) => <p key={changeIndex}>{change}</p>)}
    </li>)}</ol> : <p>Nenhuma alteração registrada para este produto.</p>}
  </section>
}
