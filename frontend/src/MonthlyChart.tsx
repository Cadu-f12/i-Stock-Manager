const months = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez']
const number = (value: number) => value.toLocaleString('pt-BR')

export default function MonthlyChart({ totals, year }: { totals: { incoming: number; outgoing: number }[]; year: number | string }) {
  const incoming = totals.reduce((sum, month) => sum + month.incoming, 0)
  const outgoing = totals.reduce((sum, month) => sum + month.outgoing, 0)
  const maximum = Math.max(1, ...totals.flatMap(month => [month.incoming, month.outgoing]))
  const axisMax = Math.ceil(maximum / 4) * 4
  return <>
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
  </>
}
