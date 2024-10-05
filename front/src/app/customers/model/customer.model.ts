export interface CustomerModel {
  id?: number;
  name?: string;
  telephone?: string;
  address?: string;
  created_at?: string;
  active?: boolean;
  hasDebt?: boolean;
  hasPaid?: boolean;
  isActive?: boolean;
  isSuspended?: boolean;
  lastBillGenerationDate?: string;
}
