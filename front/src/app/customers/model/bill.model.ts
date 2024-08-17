export interface BillModel {
  id?: number;
  month?: string;
  year?: string;
  deadLine?: string;
  observation?: string;
  depositDate?: string;
  amount?: number;
  penalties?: number;
  debt?: number;
  netToPay?: number;
  customerName?: string;
  customerId?: number;
  status?: string;
  remainingBalance?: number;
  paidAmount?: number;
}
