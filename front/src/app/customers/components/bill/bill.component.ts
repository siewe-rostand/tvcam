import { Component, OnInit, ViewChild } from '@angular/core';
import { ButtonModule } from "primeng/button";
import { InputTextModule } from "primeng/inputtext";
import { ConfirmationService, MessageService } from "primeng/api";
import { TableModule } from "primeng/table";
import { NavbarComponent } from "../../../_shared/components/navbar/navbar.component";
import { BillService } from "../../service/bill.service";
import { BillModel } from "../../model/bill.model";
import { CommonModule } from "@angular/common";
import { TagModule } from "primeng/tag";
import { DialogModule } from "primeng/dialog";
import { PaginatorModule } from "primeng/paginator";
import { PaymentModel } from "../../model/payment.model";
import { ToolbarModule } from "primeng/toolbar";
import { FormsModule } from "@angular/forms";
import { RippleModule } from "primeng/ripple";
import { ToastModule } from "primeng/toast";
import { SplitterModule } from "primeng/splitter";
import { DividerModule } from "primeng/divider";
import { BillTableComponent } from "./bill-table/bill-table.component";
import { BillPrintComponent } from "./bill-print/bill-print.component";
import { RouterLink } from "@angular/router";

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

  constructor(private billService: BillService, private messageService: MessageService,
    private confirmationService: ConfirmationService) {
  }

  ngOnInit(): void {
    this.getBills();
    this.paymentMethod = [
      { name: 'CASH', value: 'CASH' },
      { name: 'MTN MONEY', value: 'MTN_MONEY' },
      { name: 'ORANGE MONEY', value: 'ORANGE_MONEY' },
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
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: 'Erreur lors du chargement des factures',
          life: 3000,
        });
      }
    });
  }
  handleSelectedBillsChange(newSelection: any[]): void {
    this.selectedBills = newSelection;
    this.billService.setSelectedBills(this.selectedBills);
  }

  deleteBills() {
    if (!this.selectedBills || this.selectedBills.length === 0) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Attention',
        detail: 'Veuillez sélectionner au moins une facture à supprimer',
        life: 3000,
      });
      return;
    }

    const billIds = this.selectedBills.map(bill => bill.id).filter(id => id !== undefined) as number[];
    const billNames = this.selectedBills.map(bill => bill.customerName).join(', ');

    this.confirmationService.confirm({
      message: `Êtes-vous sûr de vouloir supprimer ${this.selectedBills.length} facture(s) pour: <b>${billNames}</b>?`,
      header: 'Confirmation de suppression multiple',
      icon: 'pi pi-exclamation-triangle',
      dismissableMask: false,
      accept: () => {
        this.billService.deleteBills(billIds).subscribe({
          next: (res) => {
            console.log(res);
            this.messageService.add({
              severity: 'success',
              summary: 'Succès',
              detail: `${this.selectedBills.length} facture(s) supprimée(s) avec succès`,
              life: 3000,
            });
            this.selectedBills = [];
            this.getBills(); // Rafraîchir la liste
          },
          error: (err) => {
            console.error('Erreur lors de la suppression multiple:', err);
            this.messageService.add({
              severity: 'error',
              summary: 'Erreur',
              detail: 'Erreur lors de la suppression des factures',
              life: 3000,
            });
          }
        });
      },
    });
  }

}
