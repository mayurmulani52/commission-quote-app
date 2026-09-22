import { afterEach, describe, expect, it, vi } from 'vitest';
import { generateQuote, QuoteApiError } from '../api/quoteApi';
import type { QuoteRequest } from '../types/quote';

const validRequest: QuoteRequest = {
  loanAmount: 100000,
  loanTermInMonths: 60,
  riskBand: 'MEDIUM',
};

describe('generateQuote', () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it('returns the parsed quote on a successful response', async () => {
    const mockQuote = { quoteId: 'Q-1', commissionRate: 0.02, totalCommission: 2000 };
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        new Response(JSON.stringify(mockQuote), {
          status: 200,
          headers: { 'Content-Type': 'application/json' },
        }),
      ),
    );

    const result = await generateQuote(validRequest);

    expect(result).toEqual(mockQuote);
  });

  it('sends the request as JSON to /api/quotes', async () => {
    const fetchMock = vi.fn().mockResolvedValue(
      new Response(JSON.stringify({ quoteId: 'Q-1', commissionRate: 0.02, totalCommission: 2000 }), {
        status: 200,
      }),
    );
    vi.stubGlobal('fetch', fetchMock);

    await generateQuote(validRequest);

    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/api/quotes'),
      expect.objectContaining({
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(validRequest),
      }),
    );
  });

  it('throws a QuoteApiError with the backend-provided code and message on a 4xx/5xx response', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        new Response(JSON.stringify({ code: 'VENDOR_UNAVAILABLE', message: 'Vendor is down' }), {
          status: 503,
        }),
      ),
    );

    await expect(generateQuote(validRequest)).rejects.toMatchObject({
      code: 'VENDOR_UNAVAILABLE',
      message: 'Vendor is down',
    });
    await expect(generateQuote(validRequest)).rejects.toBeInstanceOf(QuoteApiError);
  });

  it('throws a network-error QuoteApiError when fetch itself fails', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('Failed to fetch')));

    await expect(generateQuote(validRequest)).rejects.toMatchObject({
      code: 'NETWORK_ERROR',
    });
  });

  it('falls back to a generic error when the error response body is not valid JSON', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response('not json', { status: 500 })));

    await expect(generateQuote(validRequest)).rejects.toMatchObject({
      code: 'UNKNOWN_ERROR',
    });
  });
});
