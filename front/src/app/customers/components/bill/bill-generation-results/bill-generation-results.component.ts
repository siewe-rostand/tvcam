import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { BillModel } from '../../../model/bill.model';

@Component({
    selector: 'app-bill-generation-results',
    standalone: true,
    imports: [CommonModule, CardModule, ButtonModule, TableModule, TagModule, TooltipModule],
    template: `
    <p-card 
      *ngIf="results && results.length > 0" 
      header="Résultats de la Génération" 
      styleClass="mt-4 bill-results-card">
      
      <div class="mb-4">
        <div class="flex align-items-center justify-content-between">
          <div class="flex align-items-center gap-3">
            <i class="pi pi-check-circle text-green-500 text-2xl"></i>
            <div>
              <h4 class="m-0 text-green-700">Génération réussie!</h4>
              <p class="m-0 text-gray-600">
                <strong>{{ results.length }}</strong> facture(s) {{ actionLabel }} avec succès
              </p>
            </div>
          </div>
          <div class="flex gap-2">
            <p-button 
              label="Imprimer" 
              icon="pi pi-print" 
              (onClick)="onPrint()"
              severity="info"
              [outlined]="true"
              size="small">
            </p-button>
            <p-button 
              label="Voir toutes les factures" 
              icon="pi pi-eye" 
              (onClick)="onViewAll()"
              severity="primary"
              size="small">
            </p-button>
          </div>
        </div>
      </div>

      <p-table 
        [value]="results" 
        [rows]="10" 
        [paginator]="results.length > 10"
        [responsive]="true"
        styleClass="p-datatable-sm">
        
        <ng-template pTemplate="header">
          <tr>
            <th>Client</th>
            <th>Mois</th>
            <th>Année</th>
            <th>Montant (FCFA)</th>
            <th>Statut</th>
            <th>Date de création</th>
            <th>Actions</th>
          </tr>
        </ng-template>
        
        <ng-template pTemplate="body" let-bill>
          <tr>
            <td>
              <div class="font-medium">{{ bill.customerName }}</div>
              <div class="text-sm text-gray-500">{{ bill.customerId || 'N/A' }}</div>
            </td>
            <td>
              <span class="capitalize">{{ formatMonth(bill.month) }}</span>
            </td>
            <td>{{ bill.year }}</td>
            <td>
              <div class="font-medium">{{ bill.netToPay | currency:'XAF':'symbol':'1.0-0' }}</div>
              <div class="text-sm text-gray-500" *ngIf="bill.debt && bill.debt > 0">
                Dette: {{ bill.debt | currency:'XAF':'symbol':'1.0-0' }}
              </div>
            </td>
            <td>
              <p-tag 
                [value]="getStatusLabel(bill.paymentStatus)" 
                [severity]="getStatusSeverity(bill.paymentStatus)">
              </p-tag>
            </td>
            <td>
              <div class="text-sm">{{ bill.createdAt | date:'dd/MM/yyyy' }}</div>
              <div class="text-xs text-gray-500">{{ bill.createdAt | date:'HH:mm' }}</div>
            </td>
            <td>
              <div class="flex gap-1">
                <p-button 
                  icon="pi pi-eye" 
                  (onClick)="onViewBill(bill)"
                  [outlined]="true"
                  size="small"
                  severity="info"
                  pTooltip="Voir détails">
                </p-button>
                <p-button 
                  icon="pi pi-print" 
                  (onClick)="onPrintBill(bill)"
                  [outlined]="true"
                  size="small"
                  severity="secondary"
                  pTooltip="Imprimer">
                </p-button>
              </div>
            </td>
          </tr>
        </ng-template>

        <ng-template pTemplate="emptymessage">
          <tr>
            <td colspan="7" class="text-center py-4">
              <i class="pi pi-info-circle text-gray-400 text-2xl mb-2"></i>
              <p class="text-gray-500">Aucun résultat à afficher</p>
            </td>
          </tr>
        </ng-template>
      </p-table>

      <div class="mt-4 pt-3 border-top-1 border-gray-200">
        <div class="flex justify-content-between align-items-center">
          <div class="text-sm text-gray-600">
            Total généré: <strong>{{ getTotalAmount() | currency:'XAF':'symbol':'1.0-0' }}</strong>
          </div>
          <p-button 
            label="Continuer" 
            icon="pi pi-arrow-right" 
            (onClick)="onContinue()"
            severity="success"
            iconPos="right">
          </p-button>
        </div>
      </div>
    </p-card>
  `,
    styles: [`
    .bill-results-card {
      border-left: 4px solid #22c55e;
    }
    
    .bill-results-card .p-card-header {
      background: linear-gradient(90deg, #dcfce7 0%, #bbf7d0 100%);
      color: #15803d;
      font-weight: 600;
    }
  `]
})
export class BillGenerationResultsComponent {
    @Input() results: BillModel[] = [];
    @Input() actionType: 'generated' | 'updated' = 'generated';

    @Output() print = new EventEmitter<BillModel[]>();
    @Output() printSingle = new EventEmitter<BillModel>();
    @Output() viewAll = new EventEmitter<void>();
    @Output() viewBill = new EventEmitter<BillModel>();
    @Output() continue = new EventEmitter<void>();

    get actionLabel(): string {
        return this.actionType === 'updated' ? 'mise(s) à jour' : 'générée(s)';
    }

    onPrint(): void {
        this.print.emit(this.results);
    }

    onPrintBill(bill: BillModel): void {
        this.printSingle.emit(bill);
    }

    onViewAll(): void {
        this.viewAll.emit();
    }

    onViewBill(bill: BillModel): void {
        this.viewBill.emit(bill);
    }

    onContinue(): void {
        this.continue.emit();
    }

    getTotalAmount(): number {
        return this.results.reduce((total, bill) => total + (bill.netToPay || 0), 0);
    }

    formatMonth(month: string): string {
        const months: { [key: string]: string } = {
            'january': 'Janvier',
            'february': 'Février',
            'march': 'Mars',
            'april': 'Avril',
            'may': 'Mai',
            'june': 'Juin',
            'july': 'Juillet',
            'august': 'Août',
            'september': 'Septembre',
            'october': 'Octobre',
            'november': 'Novembre',
            'december': 'Décembre'
        };
        return months[month?.toLowerCase()] || month;
    }

    getStatusLabel(status: string): string {
        const labels: { [key: string]: string } = {
            'PAID': 'Payée',
            'UNPAID': 'Impayée',
            'PARTIALLY_PAID': 'Partiellement payée'
        };
        return labels[status] || status;
    }

    getStatusSeverity(status: string): 'success' | 'danger' | 'warning' | 'info' {
        const severities: { [key: string]: 'success' | 'danger' | 'warning' | 'info' } = {
            'PAID': 'success',
            'UNPAID': 'danger',
            'PARTIALLY_PAID': 'warning'
        };
        return severities[status] || 'info';
    }
}
