import {Component, OnInit} from '@angular/core';
import {ToastModule} from 'primeng/toast';
import {MessageService} from 'primeng/api';
import {NavbarComponent} from '../../../_shared/components/navbar/navbar.component';
import {CommonModule} from '@angular/common';
import {DataTableComponent, TableColumn, TableAction, ToolbarAction} from '../../../_shared/components/data-table/data-table.component';

// Interface pour les factures (exemple)
interface Bill {
  id: number;
  customerName: string;
  amount: number;
  status: 'paid' | 'unpaid' | 'overdue';
  dueDate: string;
  issueDate: string;
  billNumber: string;
}

@Component({
  selector: 'app-bill-list',
  standalone: true,
  imports: [
    CommonModule,
    ToastModule,
    NavbarComponent,
    DataTableComponent
  ],
  template: `
    <app-navbar></app-navbar>

    <app-data-table
      title="Gestion des Factures"
      subtitle="Suivez et gérez toutes vos factures clients"
      [data]="bills"
      [columns]="tableColumns"
      [actions]="rowActions"
      [toolbarActions]="toolbarActions"
      [selectedItems]="selectedBills"
      (selectedItemsChange)="selectedBills = $event"
      (toolbarActionClick)="onToolbarAction($event)"
      (rowActionClick)="onRowAction($event)"
      searchPlaceholder="Rechercher une facture..."
      [globalFilterFields]="['billNumber', 'customerName']"
      emptyMessage="Aucune facture trouvée"
      emptySubMessage="Les factures apparaîtront ici une fois générées">

      <!-- Contenu personnalisé pour les statuts de facture -->
      <div slot="status" class="status-badge"
           [ngClass]="{
             'success': getBillStatus() === 'paid',
             'danger': getBillStatus() === 'overdue',
             'warning': getBillStatus() === 'unpaid'
           }">
        {{ getStatusLabel() }}
      </div>

    </app-data-table>

    <p-toast key="br"/>
  `,
  providers: [MessageService]
})
export class BillListComponent implements OnInit {
  bills: Bill[] = [];
  selectedBills: Bill[] = [];

  // Configuration des colonnes pour les factures
  tableColumns: TableColumn[] = [
    {
      field: 'billNumber',
      header: 'N° Facture',
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
      field: 'amount',
      header: 'Montant',
      sortable: true,
      icon: 'pi-money-bill',
      type: 'text'
    },
    {
      field: 'issueDate',
      header: 'Date d\'émission',
      sortable: true,
      icon: 'pi-calendar',
      type: 'text'
    },
    {
      field: 'dueDate',
      header: 'Date d\'échéance',
      sortable: true,
      icon: 'pi-clock',
      type: 'text'
    },
    {
      field: 'status',
      header: 'Statut',
      sortable: true,
      icon: 'pi-info-circle',
      type: 'status',
      statusConfig: {
        trueLabel: 'Payée',
        falseLabel: 'Impayée',
        trueIcon: 'pi-check-circle',
        falseIcon: 'pi-times-circle'
      }
    }
  ];

  // Actions pour chaque facture
  rowActions: TableAction[] = [
    {
      label: 'Voir',
      icon: 'pi pi-eye',
      severity: 'info',
      tooltip: 'Voir la facture',
      action: 'view'
    },
    {
      label: 'Télécharger',
      icon: 'pi pi-download',
      severity: 'success',
      tooltip: 'Télécharger PDF',
      action: 'download'
    },
    {
      label: 'Envoyer',
      icon: 'pi pi-send',
      severity: 'secondary',
      tooltip: 'Envoyer par email',
      action: 'send',
      disabled: (bill: Bill) => bill.status === 'paid'
    },
    {
      label: 'Supprimer',
      icon: 'pi pi-trash',
      severity: 'danger',
      tooltip: 'Supprimer la facture',
      action: 'delete'
    }
  ];

  // Actions de la barre d'outils pour les factures
  toolbarActions: ToolbarAction[] = [
    {
      label: 'Nouvelle Facture',
      icon: 'pi pi-plus',
      severity: 'success',
      action: 'new'
    },
    {
      label: 'Exporter PDF',
      icon: 'pi pi-file-pdf',
      severity: 'info',
      action: 'exportPdf',
      requiresSelection: true
    },
    {
      label: 'Marquer Payées',
      icon: 'pi pi-check',
      severity: 'success',
      action: 'markAsPaid',
      requiresSelection: true
    },
    {
      label: 'Supprimer',
      icon: 'pi pi-trash',
      severity: 'danger',
      action: 'deleteSelected',
      requiresSelection: true
    }
  ];

  constructor(private messageService: MessageService) {}

  ngOnInit(): void {
    this.loadBills();
  }

  loadBills() {
    // Simulation de données de factures
    this.bills = [
      {
        id: 1,
        billNumber: 'FACT-2024-001',
        customerName: 'Jean Dupont',
        amount: 15000,
        status: 'paid',
        issueDate: '2024-01-15',
        dueDate: '2024-02-15'
      },
      {
        id: 2,
        billNumber: 'FACT-2024-002',
        customerName: 'Marie Martin',
        amount: 8500,
        status: 'unpaid',
        issueDate: '2024-01-20',
        dueDate: '2024-02-20'
      },
      {
        id: 3,
        billNumber: 'FACT-2024-003',
        customerName: 'Pierre Durand',
        amount: 12000,
        status: 'overdue',
        issueDate: '2024-01-10',
        dueDate: '2024-02-10'
      }
    ];
  }

  onToolbarAction(event: {action: string, event?: Event}) {
    switch (event.action) {
      case 'new':
        this.messageService.add({
          severity: 'info',
          summary: 'Nouvelle Facture',
          detail: 'Fonctionnalité à implémenter'
        });
        break;
      case 'exportPdf':
        this.messageService.add({
          severity: 'success',
          summary: 'Export PDF',
          detail: `${this.selectedBills.length} facture(s) exportée(s)`
        });
        break;
      case 'markAsPaid':
        this.markSelectedAsPaid();
        break;
      case 'deleteSelected':
        this.deleteSelectedBills();
        break;
    }
  }

  onRowAction(event: {action: string, item: any, index: number}) {
    const bill = event.item as Bill;

    switch (event.action) {
      case 'view':
        this.messageService.add({
          severity: 'info',
          summary: 'Voir Facture',
          detail: `Ouverture de la facture ${bill.billNumber}`
        });
        break;
      case 'download':
        this.messageService.add({
          severity: 'success',
          summary: 'Téléchargement',
          detail: `Téléchargement de ${bill.billNumber}`
        });
        break;
      case 'send':
        this.messageService.add({
          severity: 'info',
          summary: 'Envoi Email',
          detail: `Facture ${bill.billNumber} envoyée à ${bill.customerName}`
        });
        break;
      case 'delete':
        this.deleteBill(bill);
        break;
    }
  }

  markSelectedAsPaid() {
    this.selectedBills.forEach(bill => {
      const index = this.bills.findIndex(b => b.id === bill.id);
      if (index !== -1) {
        this.bills[index].status = 'paid';
      }
    });

    this.messageService.add({
      severity: 'success',
      summary: 'Statut Mis à Jour',
      detail: `${this.selectedBills.length} facture(s) marquée(s) comme payée(s)`,
      key: 'br'
    });

    this.selectedBills = [];
  }

  deleteSelectedBills() {
    this.bills = this.bills.filter(bill => !this.selectedBills.includes(bill));

    this.messageService.add({
      severity: 'success',
      summary: 'Suppression',
      detail: `${this.selectedBills.length} facture(s) supprimée(s)`,
      key: 'br'
    });

    this.selectedBills = [];
  }

  deleteBill(bill: Bill) {
    const index = this.bills.findIndex(b => b.id === bill.id);
    if (index !== -1) {
      this.bills.splice(index, 1);

      this.messageService.add({
        severity: 'success',
        summary: 'Suppression',
        detail: `Facture ${bill.billNumber} supprimée`,
        key: 'br'
      });
    }
  }

  // Méthodes helper pour le template personnalisé
  getBillStatus(): string {
    // Cette méthode serait utilisée dans un template personnalisé
    return 'paid';
  }

  getStatusLabel(): string {
    // Cette méthode serait utilisée dans un template personnalisé
    return 'Payée';
  }
}
