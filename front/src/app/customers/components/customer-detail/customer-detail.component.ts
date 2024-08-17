import {Component, OnInit} from '@angular/core';
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";
import {TabViewModule} from "primeng/tabview";
import {CustomerModel} from "../../model/customer.model";
import {CustomerService} from "../../service/customer.service";
import {ActivatedRoute} from "@angular/router";
import {InputTextModule} from "primeng/inputtext";
import {TableModule} from "primeng/table";
import {TagModule} from "primeng/tag";
import {BillModel} from "../../model/bill.model";
import {BillService} from "../../service/bill.service";
import {MessageService} from "primeng/api";
import {PaymentModel} from "../../model/payment.model";
import {StatusService} from "../../../_shared/status.service";
import {PaymentService} from "../../service/payment.service";
import {PaymentTableComponent} from "../_shared/payment-table/payment-table.component";
import {BillTableComponent} from "../_shared/bill-table/bill-table.component";

@Component({
  selector: 'app-customer-detail',
  standalone: true,
  imports: [
    Button,
    CardModule,
    TabViewModule,
    InputTextModule,
    TableModule,
    TagModule,
    PaymentTableComponent,
    BillTableComponent
  ],
  templateUrl: './customer-detail.component.html',
  styleUrl: './customer-detail.component.css'
})
export class CustomerDetailComponent implements OnInit {
  customer: CustomerModel | undefined;
  bills!: BillModel[];
  bill!: BillModel;

  payments!: PaymentModel[];
  selectedPayments!: PaymentModel[];
  updateBillDialog: boolean = false;

  constructor(private customerService: CustomerService, private route: ActivatedRoute,
              private billService: BillService, private messageService: MessageService,
              protected statusService: StatusService,
              private paymentService: PaymentService,
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = params['id'];
      this.getCustomerById(id);
      this.getCustomerBills(id);
      this.getCustomerPayment(id);
    })


  }

  getCustomerById(id: number) {
    this.customerService.getCustomerById(id).subscribe({
      next: result => {
        console.log(result.data);
        this.customer = result.data;
      },
      error: error => {
        console.log(error);
      }
    })
  }

  getCustomerBills(id: number): void {
    this.billService.getBillsForCustomer(id).subscribe({
      next: response => {
        this.bills = response.content;
      },
      error: err => {
        console.log(err);
      }
    })
  }

  getCustomerPayment(id: number) {
    this.paymentService.getPaymentsForCustomer(id).subscribe({
      next: response => {
        this.payments = response.content;
      },
      error:err => {
        console.log(err);
      }
    })
  }

  onSelectionChange(event: any) {
    this.selectedPayments = event;
  }


  openEdit(bill: BillModel) {
    this.bill = {...bill};
    this.updateBillDialog = true;
    console.log(bill);
  }

  deleteBill(bill: BillModel) {

  }
}
