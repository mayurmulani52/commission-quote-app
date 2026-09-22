import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { LoanQuoteForm } from '../components/LoanQuoteForm';

function fillAndSubmit(loanAmount: string, loanTermInMonths: string) {
  return async () => {
    const user = userEvent.setup();
    if (loanAmount) await user.type(screen.getByLabelText(/loan amount/i), loanAmount);
    if (loanTermInMonths) await user.type(screen.getByLabelText(/loan term/i), loanTermInMonths);
    await user.click(screen.getByRole('button', { name: /generate quote/i }));
  };
}

describe('LoanQuoteForm', () => {
  it('renders the loan amount, loan term and risk band fields', () => {
    render(<LoanQuoteForm isLoading={false} onSubmit={vi.fn()} />);

    expect(screen.getByLabelText(/loan amount/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/loan term/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/risk band/i)).toBeInTheDocument();
  });

  it('submits parsed numeric values with the selected risk band', async () => {
    const onSubmit = vi.fn();
    render(<LoanQuoteForm isLoading={false} onSubmit={onSubmit} />);
    const user = userEvent.setup();

    await user.type(screen.getByLabelText(/loan amount/i), '150000');
    await user.type(screen.getByLabelText(/loan term/i), '36');
    await user.selectOptions(screen.getByLabelText(/risk band/i), 'HIGH');
    await user.click(screen.getByRole('button', { name: /generate quote/i }));

    expect(onSubmit).toHaveBeenCalledWith({
      loanAmount: 150000,
      loanTermInMonths: 36,
      riskBand: 'HIGH',
    });
  });

  it('shows a validation error and does not submit when loan amount is empty', async () => {
    const onSubmit = vi.fn();
    render(<LoanQuoteForm isLoading={false} onSubmit={onSubmit} />);

    await fillAndSubmit('', '36')();

    expect(await screen.findByText(/enter a loan amount/i)).toBeInTheDocument();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('shows a validation error and does not submit when loan amount is zero or negative', async () => {
    const onSubmit = vi.fn();
    render(<LoanQuoteForm isLoading={false} onSubmit={onSubmit} />);

    await fillAndSubmit('-500', '36')();

    expect(await screen.findByText(/greater than zero/i)).toBeInTheDocument();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('shows a validation error and does not submit when loan term is out of range', async () => {
    const onSubmit = vi.fn();
    render(<LoanQuoteForm isLoading={false} onSubmit={onSubmit} />);

    await fillAndSubmit('100000', '600')();

    expect(await screen.findByText(/between 1 and 480 months/i)).toBeInTheDocument();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('disables the submit button and shows a loading label while isLoading is true', () => {
    render(<LoanQuoteForm isLoading={true} onSubmit={vi.fn()} />);

    const button = screen.getByRole('button', { name: /generating quote/i });
    expect(button).toBeDisabled();
  });
});
