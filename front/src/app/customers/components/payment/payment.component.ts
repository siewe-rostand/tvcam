import {Component, OnInit} from '@angular/core';
import {NavbarComponent} from "../../../_shared/components/navbar/navbar.component";
import {ButtonModule} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {TableModule} from "primeng/table";
import {PaymentModel} from "../../model/payment.model";
import {CommonModule} from "@angular/common";
import {PaymentService} from "../../service/payment.service";
import {TagModule} from "primeng/tag";
import {ToolbarModule} from "primeng/toolbar";

@Component({
  selector: 'app-bill-payment',
  standalone: true,
  imports: [
    NavbarComponent,
    ButtonModule,
    InputTextModule,
    TableModule,
    CommonModule,
    TagModule,
    ToolbarModule
  ],
  templateUrl: './payment.component.html',
  styleUrl: './payment.component.css'
})
export class PaymentComponent implements OnInit {
  payment!: PaymentModel;
  payments!: PaymentModel[];
  selectedPayments!: PaymentModel[];
  currentDate = new Date();
  currentMonth = this.currentDate.getMonth() + 1;


  constructor(private paymentService: PaymentService,) {
  }

  ngOnInit(): void {
    console.log(this.currentMonth);
    this.getAllPayments();
  }

  onSelectionChange(event: any) {
    this.selectedPayments = event;
  }

  getAllPayments() {
    this.paymentService.getMonthlyPayment('AUGUST').subscribe({
      next: (response) => {
        console.log(response.content)
        this.payments = response.content;
      },
      error: (error) => {
        console.log(error)
      }
    })
  }

  getStatusSeverity(status: string) {
    switch (status) {
      case 'UNPAID':
        return 'danger';
      case 'PAID':
        return 'success';
      case 'PARTIALLY_PAID':
        return 'warning';
      default:
        return 'danger'
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
