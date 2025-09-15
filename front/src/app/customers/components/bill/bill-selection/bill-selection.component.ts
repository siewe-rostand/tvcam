import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';

// PrimeNG Components
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { CheckboxModule } from 'primeng/checkbox';
import { TableModule } from 'primeng/table';
import { ToolbarModule } from 'primeng/toolbar';
import { TagModule } from 'primeng/tag';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { BillPrintService } from '../../../service/bill-print.service';
import { BillManagementService } from '../../../service/bill-management.service';
import { BillModel } from '../../../model/bill.model';


@Component({
  selector: 'app-bill-selection',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ButtonModule,
    CardModule,
    CheckboxModule,
    TableModule,
    ToolbarModule,
    TagModule,
    ToastModule
  ],
  providers: [MessageService],
  template: `
    <p-card header="Sélection de Factures pour Impression">
      
      <!-- Statistiques de sélection -->
      <div class="selection-stats mb-4">
        <div class="grid">
          <div class="col-12 md:col-3">
            <div class="stat-card">
              <div class="stat-number">{{ selectedBills.length }}</div>
              <div class="stat-label">Factures sélectionnées</div>
            </div>
          </div>
          <div class="col-12 md:col-3">
            <div class="stat-card">
              <div class="stat-number">{{ totalPages }}</div>
              <div class="stat-label">Pages à imprimer</div>
            </div>
          </div>
          <div class="col-12 md:col-3">
            <div class="stat-card">
              <div class="stat-number">{{ totalBills }}</div>
              <div class="stat-label">Total factures</div>
            </div>
          </div>
          <div class="col-12 md:col-3">
            <div class="stat-card">
              <div class="stat-number">{{ formatCurrency(totalAmount) }}</div>
              <div class="stat-label">Montant total</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Actions de sélection -->
      <p-toolbar styleClass="mb-4">
        <ng-template pTemplate="left">
          <p-button 
            label="Sélectionner Tout" 
            icon="pi pi-check-square" 
            (onClick)="selectAllBills()"
            [disabled]="loading || bills.length === 0"
            size="small">
          </p-button>
          <p-button 
            label="Désélectionner Tout" 
            icon="pi pi-square" 
            (onClick)="clearSelection()"
            [disabled]="loading || selectedBills.length === 0"
            size="small"
            styleClass="ml-2">
          </p-button>
        </ng-template>
        
        <ng-template pTemplate="right">
          <p-button 
            label="Imprimer Sélection" 
            icon="pi pi-print" 
            (onClick)="printSelectedBills()"
            [disabled]="loading || selectedBills.length === 0"
            severity="success">
          </p-button>
        </ng-template>
      </p-toolbar>

      <!-- Tableau des factures -->
      <p-table 
        [value]="bills" 
        [loading]="loading"
        [rows]="10" 
        [paginator]="true"
        [rowsPerPageOptions]="[10, 25, 50]"
        styleClass="p-datatable-bills">
        
        <ng-template pTemplate="header">
          <tr>
            <th style="width: 3rem">
              <p-checkbox 
                [ngModel]="isAllSelected()"
                (onChange)="toggleAllBills($event.checked)">
              </p-checkbox>
            </th>
            <th>Client</th>
            <th>Mois/Année</th>
            <th>Montant</th>
            <th>Statut</th>
            <th>Date de création</th>
            <th>Actions</th>
          </tr>
        </ng-template>
        
        <ng-template pTemplate="body" let-bill>
          <tr [class.selected-row]="bill.id !== undefined && isBillSelected(bill.id)">
            <td>
              <p-checkbox 
                [(ngModel)]="bill.selected"
                (onChange)="toggleBillSelection(bill)">
              </p-checkbox>
            </td>
            <td>{{ bill.customerName }}</td>
            <td>{{ getMonthLabel(bill.month) }} {{ bill.year }}</td>
            <td>{{ formatAmount(bill.netToPay) }}</td>
            <td>
              <p-tag 
                [value]="getStatusLabel(bill.status)" 
                [severity]="getStatusSeverity(bill.status)">
              </p-tag>
            </td>
            <td>{{ formatDate(bill.created_at) }}</td>
            <td>
              <p-button 
                icon="pi pi-print" 
                (onClick)="printSingleBill(bill)"
                size="small"
                severity="info"
                [text]="true">
              </p-button>
            </td>
          </tr>
        </ng-template>
      </p-table>

      <!-- Actions de sélection rapide -->
      <div class="quick-actions mt-4">
        <h5>Actions rapides</h5>
        <div class="flex flex-wrap gap-2">
          <p-button 
            label="Factures impayées" 
            icon="pi pi-exclamation-triangle" 
            (onClick)="selectUnpaidBills()"
            severity="warning"
            size="small">
          </p-button>
          <p-button 
            label="Factures du mois" 
            icon="pi pi-calendar" 
            (onClick)="selectCurrentMonthBills()"
            severity="info"
            size="small">
          </p-button>
          <p-button 
            label="Factures en retard" 
            icon="pi pi-clock" 
            (onClick)="selectOverdueBills()"
            severity="danger"
            size="small">
          </p-button>
        </div>
      </div>

    </p-card>

    <p-toast></p-toast>
  `,
  styles: [`
    .selection-stats {
      background: #f8f9fa;
      padding: 1rem;
      border-radius: 8px;
    }

    .stat-card {
      text-align: center;
      padding: 1rem;
      background: white;
      border-radius: 6px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .stat-number {
      font-size: 2rem;
      font-weight: bold;
      color: #007bff;
      margin-bottom: 0.5rem;
    }

    .stat-label {
      font-size: 0.9rem;
      color: #666;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .selected-row {
      background-color: #e3f2fd !important;
    }

    .quick-actions {
      background: #f8f9fa;
      padding: 1rem;
      border-radius: 8px;
    }

    .quick-actions h5 {
      margin-bottom: 1rem;
      color: #333;
    }
  `]
})
export class BillSelectionComponent implements OnInit, OnDestroy {
  bills: BillModel[] = [];
  selectedBills: BillModel[] = [];
  loading = false;
  totalBills = 0;
  totalPages = 0;
  totalAmount = 0;

  private destroy$ = new Subject<void>();

  constructor(
    private billPrintService: BillPrintService,
    private billManagementService: BillManagementService,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
    this.loadBills();
    this.subscribeToSelectedBills();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadBills(): void {
    this.loading = true;
    this.billManagementService.loadBills().subscribe({
      next: (bills) => {
        this.bills = bills;
        this.totalBills = bills.length;
        this.calculateTotals();
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des factures:', error);
        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: 'Erreur lors du chargement des factures'
        });
      }
    });
  }

  private subscribeToSelectedBills(): void {
    this.billPrintService.selectedBills$
      .pipe(takeUntil(this.destroy$))
      .subscribe(bills => {
        this.selectedBills = bills;
        this.calculateTotals();
      });
  }

  private calculateTotals(): void {
    this.totalPages = this.billPrintService.calculatePagesCount(this.selectedBills);
    this.totalAmount = this.selectedBills.reduce((sum, bill) => sum + (bill.netToPay || 0), 0);
  }

  selectAllBills(): void {
    this.billPrintService.setSelectedBills([...this.bills]);
    this.messageService.add({
      severity: 'success',
      summary: 'Sélection',
      detail: `${this.bills.length} factures sélectionnées`
    });
  }

  clearSelection(): void {
    this.billPrintService.clearSelection();
    this.messageService.add({
      severity: 'info',
      summary: 'Sélection',
      detail: 'Sélection vidée'
    });
  }

  toggleBillSelection(bill: BillModel): void {
    if (bill.id !== undefined && this.isBillSelected(bill.id)) {
      this.billPrintService.removeBillFromSelection(bill.id);
    } else if (bill.id !== undefined) {
      this.billPrintService.addBillToSelection(bill);
    }
  }

  toggleAllBills(checked: boolean): void {
    if (checked) {
      this.selectAllBills();
    } else {
      this.clearSelection();
    }
  }

  isBillSelected(billId: number): boolean {
    return this.billPrintService.isBillSelected(billId);
  }

  isAllSelected(): boolean {
    return this.bills.length > 0 && this.selectedBills.length === this.bills.length;
  }

  isPartiallySelected(): boolean {
    return this.selectedBills.length > 0 && this.selectedBills.length < this.bills.length;
  }

  printSelectedBills(): void {
    if (this.selectedBills.length === 0) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Avertissement',
        detail: 'Aucune facture sélectionnée'
      });
      return;
    }

    const billIds = this.selectedBills.map(bill => bill.id!);
    this.billPrintService.getBillsForPrint(billIds).subscribe({
      next: (printData) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Impression',
          detail: `${printData.totalBills} factures préparées pour impression`
        });
        // Ici vous pouvez ouvrir la fenêtre d'impression ou rediriger
        window.print();
      },
      error: (error) => {
        console.error('Erreur lors de la préparation de l\'impression:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: 'Erreur lors de la préparation de l\'impression'
        });
      }
    });
  }

  printSingleBill(bill: BillModel): void {
    this.billPrintService.setSelectedBills([bill]);
    this.printSelectedBills();
  }

  selectUnpaidBills(): void {
    const unpaidBills = this.bills.filter(bill => bill.status === 'UNPAID');
    this.billPrintService.setSelectedBills(unpaidBills);
    this.messageService.add({
      severity: 'info',
      summary: 'Sélection',
      detail: `${unpaidBills.length} factures impayées sélectionnées`
    });
  }

  selectCurrentMonthBills(): void {
    const currentMonth = new Date().toLocaleString('fr-FR', { month: 'long' });
    const currentYear = new Date().getFullYear().toString();
    const currentMonthBills = this.bills.filter(bill =>
      bill.month === currentMonth.toLowerCase() && bill.year === currentYear
    );
    this.billPrintService.setSelectedBills(currentMonthBills);
    this.messageService.add({
      severity: 'info',
      summary: 'Sélection',
      detail: `${currentMonthBills.length} factures du mois sélectionnées`
    });
  }

  selectOverdueBills(): void {
    const today = new Date();
    const overdueBills = this.bills.filter(bill => {
      if (bill.status === 'PAID' || !bill.deadLine) return false;
      const deadline = new Date(bill.deadLine);
      return deadline < today;
    });
    this.billPrintService.setSelectedBills(overdueBills);
    this.messageService.add({
      severity: 'info',
      summary: 'Sélection',
      detail: `${overdueBills.length} factures en retard sélectionnées`
    });
  }

  getMonthLabel(month?: string): string {
    if (!month) return '';
    const months: { [key: string]: string } = {
      'january': 'Janvier', 'february': 'Février', 'march': 'Mars', 'april': 'Avril',
      'may': 'Mai', 'june': 'Juin', 'july': 'Juillet', 'august': 'Août',
      'september': 'Septembre', 'october': 'Octobre', 'november': 'Novembre', 'december': 'Décembre'
    };
    return months[month.toLowerCase()] || month;
  }

  formatAmount(amount?: number): string {
    if (!amount) return '0 FCFA';
    return new Intl.NumberFormat('fr-FR', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(amount) + ' FCFA';
  }

  formatCurrency(amount: number): string {
    return this.formatAmount(amount);
  }

  formatDate(date?: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('fr-FR');
  }

  getStatusLabel(status?: string): string {
    const statusLabels: { [key: string]: string } = {
      'PAID': 'Payée',
      'UNPAID': 'Impayée',
      'PARTIALLY_PAID': 'Partiellement payée',
      'OVERDUE': 'En retard'
    };
    return statusLabels[status || ''] || status || 'Inconnu';
  }

  getStatusSeverity(
    status?: string
  ): 'success' | 'info' | 'warning' | 'danger' | 'secondary' | 'contrast' | undefined {
    const statusSeverity: { [key: string]: 'success' | 'info' | 'warning' | 'danger' | 'secondary' | 'contrast' } = {
      'PAID': 'success',
      'UNPAID': 'warning',
      'PARTIALLY_PAID': 'info',
      'OVERDUE': 'danger'
    };
    return statusSeverity[status || ''] || 'secondary';
  }
}
