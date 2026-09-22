// NOTE: the challenge brief doesn't define the allowed riskBand values -
// LOW/MEDIUM/HIGH was chosen as a self-explanatory default (see backend
// RiskBand.java for the same assumption documented on the other side).
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
