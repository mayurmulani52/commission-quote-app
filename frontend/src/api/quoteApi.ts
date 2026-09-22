import type { ApiErrorPayload, QuoteRequest, QuoteResponse } from '../types/quote';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8081';

// Covers both transport failures and 4xx/5xx responses so the UI has one shape to render.
export class QuoteApiError extends Error {
  readonly code: string;
  readonly details: string[];

  constructor(payload: ApiErrorPayload) {
    super(payload.message);
    this.name = 'QuoteApiError';
    this.code = payload.code;
    this.details = payload.details ?? [];
  }
}

export async function generateQuote(request: QuoteRequest): Promise<QuoteResponse> {
  let response: Response;
  try {
    response = await fetch(`${API_BASE_URL}/api/quotes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
  } catch {
    throw new QuoteApiError({
      code: 'NETWORK_ERROR',
      message: 'Unable to reach the server. Check your connection and try again.',
    });
  }

  if (!response.ok) {
    throw new QuoteApiError(await parseErrorPayload(response));
  }

  return (await response.json()) as QuoteResponse;
}

async function parseErrorPayload(response: Response): Promise<ApiErrorPayload> {
  try {
    return (await response.json()) as ApiErrorPayload;
  } catch {
    return { code: 'UNKNOWN_ERROR', message: 'Something went wrong. Please try again.' };
  }
}
