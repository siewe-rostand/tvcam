import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Button} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {PrimeTemplate} from "primeng/api";
import {TableModule} from "primeng/table";
import {TagModule} from "primeng/tag";
import {PaymentStatusComponent} from "./payment-status/payment-status.component";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-payment-table',
  standalone: true,
  imports: [
    Button,
    InputTextModule,
    PrimeTemplate,
    TableModule,
    TagModule,
    PaymentStatusComponent,
    NgForOf
  ],
  templateUrl: './payment-table.component.html',
  styleUrl: './payment-table.component.css'
})
export class PaymentTableComponent {
  @Input() payments: any[] = [];
  @Input() caption: string = 'Liste des Paiements';
  @Output() selectedPaymentsChange = new EventEmitter<any[]>();

  selectedPayments: any[] = [];

  onSelectionChange(event: any) {
    this.selectedPaymentsChange.emit(this.selectedPayments);
  }
}
