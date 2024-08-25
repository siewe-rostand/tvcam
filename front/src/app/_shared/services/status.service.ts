import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class StatusService {

  constructor() { }

  getStatusSeverity(status: string) {
    switch (status) {
      case 'UNPAID':
        return 'danger';
      case 'PAID':
        return 'success';
      case 'PARTIALLY_PAID':
        return 'warning';
      default:
        return 'danger';
    }
  }


  getStatus(status: string) {
    switch (status) {
      case 'UNPAID':
        return 'non payé';
      case 'PAID':
        return 'payé';
      case 'PARTIALLY_PAID':
        return 'partiellement payé';
      default:
        return 'non payé';
    }
  }
}
