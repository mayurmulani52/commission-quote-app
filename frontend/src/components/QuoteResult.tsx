import type { QuoteResponse } from '../types/quote';

interface QuoteResultProps {
  quote: QuoteResponse;
}

const currencyFormatter = new Intl.NumberFormat('en-AU', {
  style: 'currency',
  currency: 'AUD',
});

const percentFormatter = new Intl.NumberFormat('en-AU', {
  style: 'percent',
  minimumFractionDigits: 2,
});

export function QuoteResult({ quote }: QuoteResultProps) {
  return (
    <section className="quote-result" aria-label="Commission quote result">
      <h2>Commission Quote</h2>
      <dl>
        <dt>Quote ID</dt>
        <dd>{quote.quoteId}</dd>

        <dt>Commission rate</dt>
        <dd>{percentFormatter.format(quote.commissionRate)}</dd>

        <dt>Total commission</dt>
        <dd>{currencyFormatter.format(quote.totalCommission)}</dd>
      </dl>
    </section>
  );
}
