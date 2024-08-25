import {Component, Input} from '@angular/core';
import {PaymentFrequencyService} from "../../../../_shared/services/payment-frequency.service";
import {TagModule} from "primeng/tag";

@Component({
  selector: 'app-customer-payment-frequency',
  standalone: true,
  imports: [
    TagModule
  ],
  templateUrl: './customer-payment-frequency.component.html',
  styleUrl: './customer-payment-frequency.component.css'
})
export class CustomerPaymentFrequencyComponent {
  @Input() frequency: string = '';

  constructor(private paymentFrequencyService: PaymentFrequencyService) {
  }

  get customerPaymentFrequency() {
    return this.paymentFrequencyService.getPaymentFrequency(this.frequency);
  }
}
