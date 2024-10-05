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
import {BillTableComponent} from "./bill-table/bill-table.component";
import {BillPrintComponent} from "./bill-print/bill-print.component";
import {RouterLink} from "@angular/router";

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
    DialogModule, PaginatorModule, ToolbarModule,
    SplitterModule, DividerModule, BillTableComponent,
    BillPrintComponent, RouterLink,
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
  submitted: boolean = false;
  selectedBills: BillModel[] = [];
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
    this.billService.setSelectedBills(this.selectedBills);
  }

  deleteBills() {

  }

}
