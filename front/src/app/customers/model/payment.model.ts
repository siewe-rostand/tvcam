export interface PaymentModel {
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
}
