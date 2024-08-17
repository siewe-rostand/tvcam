import {Component, OnInit, ViewChild} from '@angular/core';
import {ButtonModule} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {ConfirmationService, MessageService} from "primeng/api";
import {TableModule} from "primeng/table";
import {NavbarComponent} from "../../../_shared/components/navbar/navbar.component";
import {BillService} from "../../service/bill.service";
import {BillModel} from "../../model/bill.model";
import {CommonModule} from "@angular/common";
import {TagModule} from "primeng/tag";
import {DialogModule} from "primeng/dialog";
import {PaginatorModule} from "primeng/paginator";
import {PaymentModel} from "../../model/payment.model";
import {ToolbarModule} from "primeng/toolbar";
import {FormsModule} from "@angular/forms";
import {RippleModule} from "primeng/ripple";
import {ToastModule} from "primeng/toast";
import {SplitterModule} from "primeng/splitter";
import {DividerModule} from "primeng/divider";
import {BillTableComponent} from "../_shared/bill-table/bill-table.component";

@Component({
  selector: 'app-customer-bill',
  standalone: true,
  imports: [
    RippleModule,
    ButtonModule,
    FormsModule,
    InputTextModule,
    ToolbarModule,
    TableModule,
    ToastModule,
    NavbarComponent, CommonModule, TagModule,
    DialogModule, PaginatorModule, ToolbarModule, SplitterModule, DividerModule, BillTableComponent,
  ],
  templateUrl: './bill.component.html',
  styleUrl: './bill.component.css',
  providers: [MessageService, ConfirmationService],
})
export class BillComponent implements OnInit {
  @ViewChild(BillTableComponent) billTable!: BillTableComponent;
  bills: BillModel[] = [];
  bill: BillModel = {};
  payment: PaymentModel = {};
  savePaymentDialog: boolean = false;
  submitted: boolean = false;
  selectedBills!: BillModel[] | null;
  amount: number | undefined;
  paymentMethod: any[] | undefined;

  constructor(private billService: BillService) {
  }

  ngOnInit(): void {
    this.getBills();
    this.paymentMethod = [
      {name: 'CASH', value:'CASH'},
      {name: 'MTN MONEY', value:'MTN_MONEY'},
      {name: 'ORANGE MONEY', value:'ORANGE_MONEY'},
    ]
  }


  showMakePayment() {
    this.savePaymentDialog = true;
  }

  getBills(): void {
    this.billService.fetchBills().subscribe({
      next: data => {
        console.log(data);
        this.bills = data.data;
      },
      error: err => {
        console.log(err);
      }
    })
  }
  handleSelectedBillsChange(newSelection: any[]): void {
    this.selectedBills = newSelection;
    console.log('Selected payments updated:', this.selectedBills);
  }
/*
 printBills() {
    // Implement printing logic here
    // You could open a new window with formatted bill data
    const printWindow = window.open('', '_blank');
    if (printWindow) {
      printWindow.document.write('<html><head><title>Bills</title></head><body>');
      // @ts-ignore
      this.selectedBills.forEach(bill => {
        printWindow.document.write(`
          <div style="page-break-after: always;">
            <h2>Bill for ${bill.customerName}</h2>
            <p>Amount: $${bill.amount}</p>
            <p>Due Date: ${bill.deadLine}</p>
          </div>
        `);
      });
      printWindow.document.write('</body></html>');
      printWindow.document.close();
      printWindow.print();
    }
  }
 */
  printBills() {
    const printWindow = window.open('', '_blank');
    if (printWindow) {
      printWindow.document.write(`
      <html>
        <head>
          <title>Bills</title>
          <style>
            body { font-family: Arial, sans-serif; }
            .page { page-break-after: always; display: flex; }
            .bill { width: 50%; padding: 10px; box-sizing: border-box; }
          </style>
        </head>
        <body>
    `);

      if (this.selectedBills != null){
        for (let i = 0; i < this.selectedBills.length; i += 2) {
          printWindow.document.write('<div class="page">');

          // First bill on the page
          this.writeBillToDocument(printWindow, this.selectedBills[i]);

          // Second bill on the page (if it exists)
          if (i + 1 < this.selectedBills.length) {
            this.writeBillToDocument(printWindow, this.selectedBills[i + 1]);
          }

          printWindow.document.write('</div>');
        }
      }else {
        console.log(this.selectedBills)
      }

      printWindow.document.write('</body></html>');
      printWindow.document.close();
      printWindow.print();
    }
  }

  private writeBillToDocument(printWindow: Window, bill: any) {
    printWindow.document.write(`
    <div class="bill">
      <h2>Bill for ${bill.customerName}</h2>
      <p>Amount: $${bill.amount.toFixed(2)}</p>
      <p>Due Date: ${new Date(bill.deadLine).toLocaleDateString()}</p>
    </div>
    <br>
  `);
  }

  deleteBills() {

  }

  hidePaymentDialog() {
    console.log('hidePaymentDialog');
  }

  makePayment() {
    this.billTable.makePayment();
  }
}
