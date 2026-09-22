import { useState } from 'react';
import type { FormEvent } from 'react';
import type { QuoteRequest, RiskBand } from '../types/quote';

interface LoanQuoteFormProps {
  isLoading: boolean;
  onSubmit: (request: QuoteRequest) => void;
}

interface FieldErrors {
  loanAmount?: string;
  loanTermInMonths?: string;
}

const RISK_BANDS: RiskBand[] = ['LOW', 'MEDIUM', 'HIGH'];

function validate(loanAmount: string, loanTermInMonths: string): FieldErrors {
  const errors: FieldErrors = {};

  const amount = Number(loanAmount);
  if (loanAmount.trim() === '' || Number.isNaN(amount)) {
    errors.loanAmount = 'Enter a loan amount.';
  } else if (amount <= 0) {
    errors.loanAmount = 'Loan amount must be greater than zero.';
  }

  const term = Number(loanTermInMonths);
  if (loanTermInMonths.trim() === '' || Number.isNaN(term) || !Number.isInteger(term)) {
    errors.loanTermInMonths = 'Enter a loan term in whole months.';
  } else if (term < 1 || term > 480) {
    errors.loanTermInMonths = 'Loan term must be between 1 and 480 months.';
  }

  return errors;
}

export function LoanQuoteForm({ isLoading, onSubmit }: LoanQuoteFormProps) {
  const [loanAmount, setLoanAmount] = useState('');
  const [loanTermInMonths, setLoanTermInMonths] = useState('');
  const [riskBand, setRiskBand] = useState<RiskBand>('MEDIUM');
  const [errors, setErrors] = useState<FieldErrors>({});

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const fieldErrors = validate(loanAmount, loanTermInMonths);
    setErrors(fieldErrors);

    if (Object.keys(fieldErrors).length > 0) {
      return;
    }

    onSubmit({
      loanAmount: Number(loanAmount),
      loanTermInMonths: Number(loanTermInMonths),
      riskBand,
    });
  }

  return (
    <form className="loan-quote-form" onSubmit={handleSubmit} noValidate>
      <div className="form-field">
        <label htmlFor="loanAmount">Loan amount ($)</label>
        <input
          id="loanAmount"
          name="loanAmount"
          type="number"
          inputMode="decimal"
          step="0.01"
          value={loanAmount}
          onChange={(e) => setLoanAmount(e.target.value)}
          aria-invalid={Boolean(errors.loanAmount)}
          aria-describedby={errors.loanAmount ? 'loanAmount-error' : undefined}
          disabled={isLoading}
        />
        {errors.loanAmount && (
          <p className="field-error" id="loanAmount-error" role="alert">
            {errors.loanAmount}
          </p>
        )}
      </div>

      <div className="form-field">
        <label htmlFor="loanTermInMonths">Loan term (months)</label>
        <input
          id="loanTermInMonths"
          name="loanTermInMonths"
          type="number"
          inputMode="numeric"
          step="1"
          value={loanTermInMonths}
          onChange={(e) => setLoanTermInMonths(e.target.value)}
          aria-invalid={Boolean(errors.loanTermInMonths)}
          aria-describedby={errors.loanTermInMonths ? 'loanTermInMonths-error' : undefined}
          disabled={isLoading}
        />
        {errors.loanTermInMonths && (
          <p className="field-error" id="loanTermInMonths-error" role="alert">
            {errors.loanTermInMonths}
          </p>
        )}
      </div>

      <div className="form-field">
        <label htmlFor="riskBand">Risk band</label>
        <select
          id="riskBand"
          name="riskBand"
          value={riskBand}
          onChange={(e) => setRiskBand(e.target.value as RiskBand)}
          disabled={isLoading}
        >
          {RISK_BANDS.map((band) => (
            <option key={band} value={band}>
              {band}
            </option>
          ))}
        </select>
      </div>

      <button type="submit" disabled={isLoading}>
        {isLoading ? 'Generating quote…' : 'Generate Quote'}
      </button>
    </form>
  );
}
