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
  
  // Informations de zone
  zoneId?: number;
  zoneName?: string;
  zoneAddress?: string;
  zoneCode?: string;
  
  // Informations de paiement
  paymentFrequency?: string;
  ref?: string;
}
