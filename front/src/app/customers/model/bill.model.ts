export interface BillModel {
  monthlyPayment?: number;
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
  
  // Informations supplémentaires pour la facture
  zoneName?: string;
  zoneAddress?: string;
  responsibleName?: string;
  responsiblePhone?: string;
  collectorName?: string;
  collectorPhone?: string;
  companyPhone?: string;
  companyAddress?: string;
  billReference?: string;
}
