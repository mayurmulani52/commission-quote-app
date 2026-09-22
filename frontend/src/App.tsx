import { useState } from 'react';
import { generateQuote, QuoteApiError } from './api/quoteApi';
import { LoanQuoteForm } from './components/LoanQuoteForm';
import { QuoteResult } from './components/QuoteResult';
import { ErrorMessage } from './components/ErrorMessage';
import type { QuoteRequest, QuoteResponse } from './types/quote';
import './App.css';

type RequestState =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'success'; quote: QuoteResponse }
  | { status: 'error'; message: string; details: string[] };

export function App() {
  const [state, setState] = useState<RequestState>({ status: 'idle' });

  async function handleSubmit(request: QuoteRequest) {
    setState({ status: 'loading' });
    try {
      const quote = await generateQuote(request);
      setState({ status: 'success', quote });
    } catch (error) {
      if (error instanceof QuoteApiError) {
        setState({ status: 'error', message: error.message, details: error.details });
      } else {
        setState({ status: 'error', message: 'An unexpected error occurred. Please try again.', details: [] });
      }
    }
  }

  return (
    <main className="app">
      <h1>Commission Quote</h1>
      <p className="app-subtitle">Enter loan details to generate a commission quote.</p>

      <LoanQuoteForm isLoading={state.status === 'loading'} onSubmit={handleSubmit} />

      {state.status === 'error' && <ErrorMessage message={state.message} details={state.details} />}
      {state.status === 'success' && <QuoteResult quote={state.quote} />}
    </main>
  );
}
