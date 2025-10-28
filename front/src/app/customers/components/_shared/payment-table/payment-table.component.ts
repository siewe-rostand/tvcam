import {AfterViewInit, Component, EventEmitter, Input, Output, TemplateRef, ViewChild} from '@angular/core';
import {Button} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {PrimeTemplate} from "primeng/api";
import {TagModule} from "primeng/tag";
import {PaymentStatusComponent} from "./payment-status/payment-status.component";
import {DialogModule} from "primeng/dialog";
import {DividerModule} from "primeng/divider";
import {DropdownModule} from "primeng/dropdown";
import {ReactiveFormsModule} from "@angular/forms";
import {PaymentModel} from "../../../model/payment.model";
import {
  CustomerPaymentFrequencyComponent
} from "../../customer-list/customer-payment-frequency/customer-payment-frequency.component";
import {
  DataTableComponent,
  TableAction,
  TableColumn
} from "../../../../_shared/components/data-table/data-table.component";

@Component({
  selector: 'app-payment-table',
  standalone: true,
  imports: [
    Button,
    InputTextModule,
    PrimeTemplate,
    TagModule,
    PaymentStatusComponent,
    DialogModule,
    DividerModule,
    DropdownModule,
    ReactiveFormsModule,
    CustomerPaymentFrequencyComponent,
    DataTableComponent
  ],
  templateUrl: './payment-table.component.html',
  styleUrl: './payment-table.component.css'
})
export class PaymentTableComponent implements AfterViewInit {
  @Input({ transform: (value: PaymentModel[] | undefined) => value || [] })
  payments: PaymentModel[] = [];
  @Input() caption: string = 'Liste des Paiements';
  @Output() selectedPaymentsChange = new EventEmitter<any[]>();

  @ViewChild('statusTemplate') statusTemplate!: TemplateRef<any>;

  selectedPayments: PaymentModel[] = [];
  detailDialog: boolean = false;
  payment: PaymentModel = {};
  cellTemplates: { [key: string]: TemplateRef<any> } = {};

  onSelectionChange(event: any) {
    this.selectedPaymentsChange.emit(this.selectedPayments);
  }

  getSelectedPayment() {
    console.log(this.selectedPayments);
  }

  hideUpdateBill() {
    this.detailDialog = false;
  }

  openDetail(payment: PaymentModel) {
    this.payment = {...payment};
    this.detailDialog = true;
  }

  ngAfterViewInit() {
    // Map the template to the field name
    this.cellTemplates = {
      'status': this.statusTemplate
    };
  }

  tableColumns: TableColumn[] = [
    {
      field: 'reference',
      header: 'Reference',
      sortable: true,
      icon: 'pi-file-o',
      type: 'text'
    },
    {
      field: 'customerName',
      header: 'Client',
      sortable: true,
      icon: 'pi-user',
      type: 'text'
    },
    {
      field: 'month',
      header: 'Mois',
      sortable: true,
      icon: 'pi-calendar',
      type: 'text'
    },
    {
      field: 'amount',
      header: 'Montant',
      sortable: true,
      icon: 'pi-money-bill',
      type: 'text'
    },
    {
      field: 'status',
      header: 'Statut',
      type: 'custom'
    },
    {
      field: 'paymentDate',
      header: 'Date paiement',
      sortable: true,
      icon: 'pi-clock',
      type: 'text'
    }
  ];

  tableActions: TableAction[] = [
    {
      label: 'View',
      icon: 'pi pi-eye',
      severity: 'info',
      tooltip: 'Voir les détails',
      action: 'view'
    }
  ];

  onRowAction(event: { action: string, item: any, index: number }) {
    if (event.action === 'view') {
      this.openDetail(event.item);
    }
  }
}
