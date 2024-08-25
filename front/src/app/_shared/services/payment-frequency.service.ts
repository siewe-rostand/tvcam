import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PaymentFrequencyService {

  constructor() {
  }

  getPaymentFrequency(status: string) {
    switch (status) {
      case 'MONTHLY':
        return 'Mensuel';
      case 'QUARTERLY':
        return 'Trimestriel';
      case 'SEMI_ANNUALLY':
        return 'Semestriel';
      case 'ANNUALLY':
        return 'Annuel';
      default:
        return 'Mensuel';
    }
  }
}
