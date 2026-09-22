import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { App } from '../App';
import { generateQuote, QuoteApiError } from '../api/quoteApi';

vi.mock('../api/quoteApi', async () => {
  const actual = await vi.importActual<typeof import('../api/quoteApi')>('../api/quoteApi');
  return {
    ...actual,
    generateQuote: vi.fn(),
  };
});

const mockedGenerateQuote = vi.mocked(generateQuote);

async function fillValidFormAndSubmit() {
  const user = userEvent.setup();
  await user.type(screen.getByLabelText(/loan amount/i), '100000');
  await user.type(screen.getByLabelText(/loan term/i), '60');
  await user.click(screen.getByRole('button', { name: /generate quote/i }));
}

describe('App', () => {
  beforeEach(() => {
    mockedGenerateQuote.mockReset();
  });

  it('displays the quote result on a successful submission', async () => {
    mockedGenerateQuote.mockResolvedValue({
      quoteId: 'Q-ABC123',
      commissionRate: 0.02,
      totalCommission: 2000,
    });

    render(<App />);
    await fillValidFormAndSubmit();

    expect(await screen.findByText('Q-ABC123')).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: /commission quote/i, level: 2 })).toBeInTheDocument();
  });

  it('shows a loading state while the request is in flight', async () => {
    let resolveRequest!: (value: { quoteId: string; commissionRate: number; totalCommission: number }) => void;
    mockedGenerateQuote.mockReturnValue(
      new Promise((resolve) => {
        resolveRequest = resolve;
      }),
    );

    render(<App />);
    await fillValidFormAndSubmit();

    expect(screen.getByRole('button', { name: /generating quote/i })).toBeDisabled();

    resolveRequest({ quoteId: 'Q-1', commissionRate: 0.02, totalCommission: 2000 });
    await waitFor(() => expect(screen.getByRole('button', { name: /generate quote/i })).not.toBeDisabled());
  });

  it('displays a friendly error message when the vendor is unavailable', async () => {
    mockedGenerateQuote.mockRejectedValue(
      new QuoteApiError({
        code: 'VENDOR_UNAVAILABLE',
        message: 'Commission quote service is temporarily unavailable. Please try again.',
      }),
    );

    render(<App />);
    await fillValidFormAndSubmit();

    expect(
      await screen.findByText(/commission quote service is temporarily unavailable/i),
    ).toBeInTheDocument();
  });

  it('does not call the API when client-side validation fails', async () => {
    const user = userEvent.setup();
    render(<App />);

    await user.click(screen.getByRole('button', { name: /generate quote/i }));

    expect(await screen.findByText(/enter a loan amount/i)).toBeInTheDocument();
    expect(mockedGenerateQuote).not.toHaveBeenCalled();
  });
});
