import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Button} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {ConfirmationService, MessageService} from "primeng/api";
import {TableModule} from "primeng/table";
import {TagModule} from "primeng/tag";
import {BillModel} from "../../../model/bill.model";
import {PaymentStatusComponent} from "../../_shared/payment-table/payment-status/payment-status.component";
import {DialogModule} from "primeng/dialog";
import {DividerModule} from "primeng/divider";
import {DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {PAYMENT_METHODS, PaymentModel} from "../../../model/payment.model";
import {PaymentService} from "../../../service/payment.service";
import {ToastModule} from "primeng/toast";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {BillService} from "../../../service/bill.service";
import {BillPrintService} from "../../../service/bill-print.service";
import {Router} from "@angular/router";
import {InputNumberModule} from "primeng/inputnumber";
import {DropdownOptionModel} from "../../../../_shared/model/api-response";
import {BillUtils} from "../../../../_shared/utils/bill.utils";
import {InputTextareaModule} from "primeng/inputtextarea";
import {AuthStateService} from "../../../../auth/services/auth-state.service";

@Component({
  selector: 'app-bill-table',
  standalone: true,
  imports: [
    CommonModule,
    Button,
    InputTextModule,
    TableModule,
    TagModule,
    InputNumberModule,
    PaymentStatusComponent,
    DialogModule,
    DividerModule,
    DropdownModule,
    FormsModule,
    ToastModule,
    ConfirmDialogModule,
    InputTextareaModule
  ],
  templateUrl: './bill-table.component.html',
  styleUrl: './bill-table.component.css',
  providers: [MessageService, ConfirmationService],
})
export class BillTableComponent {
  @Input() bills: BillModel[] = [];
  @Output() selectedBillsChange = new EventEmitter<BillModel[]>();

  selectedBills: BillModel[] = [];
  makePaymentDialog: boolean = false;
  bill: BillModel = {};
  amount: number = 0;
  submitted: boolean = false;
  commentaire: string = '';
  paymentMethod: DropdownOptionModel[] = PAYMENT_METHODS;
  selectedPaymentMethod: DropdownOptionModel = PAYMENT_METHODS[0];
  payment: PaymentModel = {};


  constructor(private paymentService: PaymentService, private billService: BillService,
              private messageService: MessageService, private confirmationService: ConfirmationService,
              private billPrintService: BillPrintService, private router: Router, private readonly authState: AuthStateService) {
  }

  onSelectionChange(event: BillModel) {
    this.selectedBillsChange.emit(this.selectedBills);
  }

  openEdit(bill: BillModel) {
    this.bill = {...bill};
    this.makePaymentDialog = true;
    console.log(bill);
  }


  deleteBill(bill: BillModel) {
    this.confirmationService.confirm({
      message: `Êtes-vous sûr de vouloir supprimer la facture de <b>${bill.customerName?.toUpperCase()}</b>
      d'un montant <b>${bill.paidAmount?.toString().toUpperCase()}</b> et tous les paiements liés à cette facture?`,
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      dismissableMask: false,
      accept: () => {
        this.billService.deleteBill(bill.id!).subscribe({
          next: (res) => {
            console.log(res);
            this.messageService.add({
              severity: 'success',
              summary: 'Succès',
              detail: 'Facture supprimée avec succès',
              life: 3000,
            });
            // Rafraîchir la liste après suppression
            this.refreshBills();
          },
          error: (err) => {
            console.error('Erreur lors de la suppression:', err);
            this.messageService.add({
              severity: 'error',
              summary: 'Erreur',
              detail: 'Erreur lors de la suppression de la facture',
              life: 3000,
            });
          }
        });
      },
    });
  }

  hideUpdateBill() {
    this.makePaymentDialog = false;
  }

  getMonthLabel(monthValue?: string): string {
    return BillUtils.getMonthLabelFr(monthValue);
  }

  getDateFromMonth(month: number, year: number): Date {
    return new Date(year, month - 1, 1);
  }

  getMonthColor(monthValue?: string): string {
    const readableColors = [
      '#2563eb',
      '#dc2626',
      '#059669',
      '#7c3aed',
      '#ea580c',
      '#0891b2',
      '#be123c',
      '#4338ca',
      '#0d9488',
      '#9333ea',
      '#f59e0b',
      '#ef4444'
    ];

    if (!monthValue) {
      return readableColors[0];
    }

    const monthNum = parseInt(monthValue) || 1;
    return readableColors[(monthNum - 1) % readableColors.length];
  }


  makePayment() {
    this.submitted = true;

    // Validation du montant
    if (!this.amount || this.amount <= 0) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Attention',
        detail: 'Veuillez entrer un montant valide supérieur à 0',
        life: 4000,
      });
      return;
    }

    // Calculer le reste à payer
    const remainingBalance = (this.bill.netToPay || 0) - (this.bill.paidAmount || 0);

    // Vérifier si le montant ne dépasse pas le reste à payer
    if (this.amount > remainingBalance) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Montant trop élevé',
        detail: `Le montant ne peut pas dépasser le reste à payer: ${remainingBalance} FCFA`,
        life: 4000,
      });
      return;
    }
    console.log('connected user', this.authState.getConnectedUser().id);

    this.payment = {
      amount: this.amount,
      customerId: this.bill.customerId,
      billId: this.bill.id,
      userId: 1,
      observation: this.commentaire,
      paymentMethod: this.selectedPaymentMethod.label,
    };

    this.paymentService.makePayment(this.payment).subscribe({
      next: data => {
        this.submitted = false;
        this.hideUpdateBill();
        this.resetForm();
        console.log(data);
        this.messageService.add({
          severity: 'success',
          summary: 'Succès',
          detail: 'Le paiement a été effectué avec succès',
          life: 4000,
        });
        // Émettre un événement pour rafraîchir la liste
        this.refreshBills();
      },
      error: err => {
        this.submitted = false;
        console.error('Erreur lors du paiement:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: 'Erreur lors du traitement du paiement',
          life: 4000,
        });
      }
    });
  }

  private resetForm() {
    this.amount = 0;
    this.commentaire = '';
    this.selectedPaymentMethod = PAYMENT_METHODS[0];
  }

  private refreshBills() {
    window.location.reload();
  }

  /**
   * Ouvre l'aperçu d'impression pour les factures sélectionnées
   */
  openPrintPreview() {
    if (this.selectedBills.length === 0) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Aucune sélection',
        detail: 'Veuillez sélectionner au moins une facture pour l\'impression',
        life: 3000,
      });
      return;
    }

    // Enregistrer les factures sélectionnées dans le service
    this.billPrintService.setSelectedBills(this.selectedBills);

    // Naviguer vers la page d'aperçu d'impression
    this.router.navigate(['/receipts/print-preview']).then(r => true);

    this.messageService.add({
      severity: 'info',
      summary: 'Aperçu d\'impression',
      detail: `${this.selectedBills.length} facture(s) sélectionnée(s) pour l'impression`,
      life: 3000,
    });
  }
}
