// riskBand values aren't pinned down by a real vendor contract yet -
// LOW/MEDIUM/HIGH mirrors the assumption made in backend RiskBand.java.
export type RiskBand = 'LOW' | 'MEDIUM' | 'HIGH';

export interface QuoteRequest {
  loanAmount: number;
  loanTermInMonths: number;
  riskBand: RiskBand;
}

export interface QuoteResponse {
  quoteId: string;
  commissionRate: number;
  totalCommission: number;
}

export interface ApiErrorPayload {
  code: string;
  message: string;
  details?: string[];
}
