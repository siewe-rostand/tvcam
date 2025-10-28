import {DropdownOptionModel} from "../../_shared/model/api-response";

export interface PaymentModel {
  id?: number;
  billId?: number;
  customerId?: number;
  userId?: number;
  amount?: number;
  paymentMethod?: string;
  observation?: string;
  customerPaymentFrequency?: string;
  paymentDate?: string;
  status?: string;
  reference?: string;
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

const PAYMENT_METHOD_VALUES = [
  'CASH',
  'MOBILE_MONEY',
  'BANK_TRANSFER',
  'CHECK',
  'ORANGE_MONEY'
] as const;

export type PaymentMethod = (typeof PAYMENT_METHOD_VALUES)[number];

export const PaymentMethods: Record<PaymentMethod, PaymentMethod> = {
  CASH: 'CASH',
  MOBILE_MONEY: 'MOBILE_MONEY',
  ORANGE_MONEY: 'ORANGE_MONEY',
  BANK_TRANSFER: 'BANK_TRANSFER',
  CHECK: 'CHECK',
};

export const PAYMENT_METHODS: DropdownOptionModel[] = [
  {id: PaymentMethods.CASH, label: 'Cash', icon: 'pi pi-wallet'},
  {id: PaymentMethods.MOBILE_MONEY, label: 'Mobile Money', icon: 'pi pi-mobile'},
  {id: PaymentMethods.ORANGE_MONEY, label: 'Orange Money', icon: 'pi pi-mobile'},
  {id: PaymentMethods.BANK_TRANSFER, label: 'Bank Transfer', icon: 'pi pi-bank'},
  {id: PaymentMethods.CHECK, label: 'Check', icon: 'pi pi-file'},
];
