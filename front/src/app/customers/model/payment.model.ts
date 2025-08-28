export interface PaymentModel {
  id?: number;
  billId?: number;
  customerId?: number;
  amount?: number;
  paymentMethod?: string;
  observation?: string;
  customerPaymentFrequency?: string;
  paymentDate?: string;
  paymentStatus?: string;
  paymentReference?: string;
  paymentAmount?: number;
  customerName?: string;
  month?: string;
  user?: string;
}

export type PaymentFilter = {
  paymentMethod?: string | null;
  dateFrom?: Date | null;
  dateTo?: Date | null;
  customerId?: number | null;
  minAmount?: number | null;
  maxAmount?: number | null;
}
