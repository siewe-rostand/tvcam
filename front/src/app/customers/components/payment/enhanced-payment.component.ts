import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// PrimeNG Components
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ChartModule } from 'primeng/chart';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { ToastModule } from 'primeng/toast';
import { ToolbarModule } from 'primeng/toolbar';
import { TagModule } from 'primeng/tag';
import { CalendarModule } from 'primeng/calendar';
import { InputNumberModule } from 'primeng/inputnumber';
import { MessageService } from 'primeng/api';

// Services
import { PaymentManagementService, PaymentSummary } from '../../service/payment-management.service';

// Models
import {PaymentFilter, PaymentModel} from '../../model/payment.model';

// Components
import { NavbarComponent } from '../../../_shared/components/navbar/navbar.component';

@Component({
    selector: 'app-enhanced-payment',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        ButtonModule,
        CardModule,
        ChartModule,
        DialogModule,
        DropdownModule,
        InputTextModule,
        TableModule,
        ToastModule,
        ToolbarModule,
        TagModule,
        CalendarModule,
        InputNumberModule,
        NavbarComponent
    ],
    providers: [MessageService],
    template: `
      <app-navbar></app-navbar>

      <div class="container mx-auto p-4">
        <!-- Tableau de bord des paiements -->
        <div class="grid mb-4">
          <div class="col-12 md:col-3">
            <p-card>
              <div class="text-center">
                <h3 class="text-2xl font-bold text-blue-600">{{ summary.totalPayments }}</h3>
                <p class="text-gray-600">Total Paiements</p>
              </div>
            </p-card>
          </div>
          <div class="col-12 md:col-3">
            <p-card>
              <div class="text-center">
                <h3
                  class="text-2xl font-bold text-green-600">{{ summary.totalAmount | currency:'XAF':'symbol':'1.0-0' }}</h3>
                <p class="text-gray-600">Montant Total</p>
              </div>
            </p-card>
          </div>
          <div class="col-12 md:col-3">
            <p-card>
              <div class="text-center">
                <h3 class="text-2xl font-bold text-purple-600">{{ performance.thisMonthCount }}</h3>
                <p class="text-gray-600">Ce Mois</p>
              </div>
            </p-card>
          </div>
          <div class="col-12 md:col-3">
            <p-card>
              <div class="text-center">
                <h3 class="text-2xl font-bold"
                    [class]="performance.growth >= 0 ? 'text-green-600' : 'text-red-600'">
                  {{ performance.growth }}%
                </h3>
                <p class="text-gray-600">Croissance</p>
              </div>
            </p-card>
          </div>
        </div>

        <!-- Graphiques -->
        <div class="grid mb-4">
          <div class="col-12 md:col-6">
            <p-card header="Paiements par Méthode">
              <p-chart type="doughnut" [data]="paymentMethodChart" [options]="chartOptions"></p-chart>
            </p-card>
          </div>
          <div class="col-12 md:col-6">
            <p-card header="Tendance Mensuelle">
              <p-chart type="line" [data]="monthlyTrendChart" [options]="chartOptions"></p-chart>
            </p-card>
          </div>
        </div>

        <!-- Filtres et actions -->
        <p-card header="Gestion des Paiements" styleClass="mb-4">
          <p-toolbar styleClass="mb-4">
            <ng-template pTemplate="left">
              <p-dropdown
                [options]="paymentMethods"
                [(ngModel)]="filters.paymentMethod"
                optionLabel="label"
                optionValue="value"
                placeholder="Méthode de paiement"
                [showClear]="true"
                (onChange)="applyFilters()"
                styleClass="mr-2">
              </p-dropdown>

              <p-calendar
                [(ngModel)]="filters.dateFrom"
                placeholder="Date début"
                [showIcon]="true"
                dateFormat="dd/mm/yy"
                (onSelect)="applyFilters()"
                styleClass="mr-2">
              </p-calendar>

              <p-calendar
                [(ngModel)]="filters.dateTo"
                placeholder="Date fin"
                [showIcon]="true"
                dateFormat="dd/mm/yy"
                (onSelect)="applyFilters()"
                styleClass="mr-2">
              </p-calendar>
            </ng-template>

            <ng-template pTemplate="right">
              <p-button
                label="Effacer Filtres"
                icon="pi pi-times"
                (onClick)="clearFilters()"
                severity="secondary"
                styleClass="mr-2">
              </p-button>
              <p-button
                label="Exporter"
                icon="pi pi-download"
                (onClick)="exportPayments()"
                severity="help">
              </p-button>
            </ng-template>
          </p-toolbar>

          <!-- Table des paiements -->
          <p-table
            [value]="filteredPayments"
            [loading]="loading"
            [rows]="10"
            [paginator]="true"
            [rowsPerPageOptions]="[10, 25, 50]"
            [globalFilterFields]="['customerName', 'paymentReference']"
            styleClass="p-datatable-payments">

            <ng-template pTemplate="caption">
              <div class="flex align-items-center justify-content-between">
                <h5 class="m-0">Liste des Paiements</h5>
                <span class="p-input-icon-left">
                <i class="pi pi-search"></i>
                <input
                  pInputText
                  type="text"
                  [(ngModel)]="globalFilter"
                  placeholder="Rechercher..."/>
              </span>
              </div>
            </ng-template>

            <ng-template pTemplate="header">
              <tr>
                <th pSortableColumn="paymentDate">
                  Date
                  <p-sortIcon field="paymentDate"></p-sortIcon>
                </th>
                <th pSortableColumn="paymentReference">
                  Référence
                  <p-sortIcon field="paymentReference"></p-sortIcon>
                </th>
                <th pSortableColumn="customerName">
                  Client
                  <p-sortIcon field="customerName"></p-sortIcon>
                </th>
                <th pSortableColumn="amount">
                  Montant
                  <p-sortIcon field="amount"></p-sortIcon>
                </th>
                <th pSortableColumn="paymentMethod">
                  Méthode
                  <p-sortIcon field="paymentMethod"></p-sortIcon>
                </th>
                <th pSortableColumn="paymentStatus">
                  Statut
                  <p-sortIcon field="paymentStatus"></p-sortIcon>
                </th>
                <th>Actions</th>
              </tr>
            </ng-template>

            <ng-template pTemplate="body" let-payment>
              <tr>
                <td>{{ payment.paymentDate | date:'dd/MM/yyyy HH:mm' }}</td>
                <td>
                  <span class="font-mono text-sm">{{ payment.paymentReference }}</span>
                </td>
                <td>{{ payment.customerName }}</td>
                <td>
                  <span class="font-bold">{{ payment.amount | currency:'XAF':'symbol':'1.0-0' }}</span>
                </td>
                <td>
                <span class="p-badge" [class]="getPaymentMethodBadgeClass(payment.paymentMethod)">
                  {{ formatPaymentMethod(payment.paymentMethod) }}
                </span>
                </td>
                <td>
                <span class="p-badge" [class]="getStatusBadgeClass(payment.paymentStatus)">
                  {{ payment.paymentStatus }}
                </span>
                </td>
                <td>
                  <p-button
                    icon="pi pi-eye"
                    [rounded]="true"
                    [outlined]="true"
                    severity="info"
                    (onClick)="viewPaymentDetails(payment)"
                    styleClass="mr-1">
                  </p-button>
                  <p-button
                    icon="pi pi-print"
                    [rounded]="true"
                    [outlined]="true"
                    severity="secondary"
                    (onClick)="printReceipt(payment)"
                    styleClass="mr-1">
                  </p-button>
                  <p-button
                    *ngIf="canCancelPayment(payment)"
                    icon="pi pi-times"
                    [rounded]="true"
                    [outlined]="true"
                    severity="danger"
                    (onClick)="cancelPayment(payment)">
                  </p-button>
                </td>
              </tr>
            </ng-template>
          </p-table>
        </p-card>

        <!-- Dialog de détails du paiement -->
        <p-dialog
          [(visible)]="paymentDetailsDialog"
          [style]="{ width: '50vw' }"
          header="Détails du Paiement"
          [modal]="true">

          <div *ngIf="selectedPayment" class="payment-details">
            <div class="grid">
              <div class="col-6">
                <p><strong>Référence:</strong> {{ selectedPayment.reference }}</p>
                <p><strong>Date:</strong> {{ selectedPayment.paymentDate | date:'dd/MM/yyyy HH:mm' }}</p>
                <p><strong>Client:</strong> {{ selectedPayment.customerName }}</p>
                <p><strong>Montant:</strong> {{ selectedPayment.amount | currency:'XAF':'symbol':'1.0-0' }}
                </p>
              </div>
              <div class="col-6">
                <p><strong>Méthode:</strong> {{ formatPaymentMethod(selectedPayment.paymentMethod) }}</p>
                <p><strong>Statut:</strong> {{ selectedPayment.status }}</p>
                <p><strong>Utilisateur:</strong> {{ selectedPayment.user || 'N/A' }}</p>
                <p><strong>Observation:</strong> {{ selectedPayment.observation || 'Aucune' }}</p>
              </div>
            </div>
          </div>

          <ng-template pTemplate="footer">
            <p-button
              label="Fermer"
              icon="pi pi-times"
              (onClick)="paymentDetailsDialog = false">
            </p-button>
            <p-button
              label="Imprimer Reçu"
              icon="pi pi-print"
              (onClick)="printReceipt(selectedPayment!)"
              severity="secondary">
            </p-button>
          </ng-template>
        </p-dialog>
      </div>

      <p-toast></p-toast>
    `
})
export class EnhancedPaymentComponent implements OnInit {
    payments: PaymentModel[] = [];
    filteredPayments: PaymentModel[] = [];
    selectedPayment: PaymentModel | null = null;
    loading = false;
    globalFilter = '';
    paymentDetailsDialog = false;

    summary: PaymentSummary = {
        totalPayments: 0,
        totalAmount: 0,
        paymentsByMethod: {},
        paymentsByStatus: {},
        monthlyTrend: []
    };

    performance = {
        thisMonthCount: 0,
        thisMonthAmount: 0,
        lastMonthCount: 0,
        lastMonthAmount: 0,
        growth: 0,
        averagePayment: 0
    };

    filters: PaymentFilter = {
        paymentMethod: null,
        dateFrom: null,
        dateTo: null,
        customerId: null,
        minAmount: null,
        maxAmount: null
    };

    paymentMethods = [
        { label: 'Espèces', value: 'CASH' },
        { label: 'MTN Money', value: 'MTN_MONEY' },
        { label: 'Orange Money', value: 'ORANGE_MONEY' },
        { label: 'Virement bancaire', value: 'BANK_TRANSFER' }
    ];

    paymentMethodChart: any = {};
    monthlyTrendChart: any = {};
    chartOptions: any = {};

    constructor(
        private paymentManagementService: PaymentManagementService,
        private messageService: MessageService
    ) {
        this.initializeChartOptions();
    }

    ngOnInit(): void {
        this.loadPayments();
        this.loadPerformanceMetrics();
    }

    private initializeChartOptions(): void {
        this.chartOptions = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        };
    }

    loadPayments(): void {
        this.loading = true;
        // Simuler le chargement des paiements - remplacer par un vrai appel API
        setTimeout(() => {
            this.payments = [
                {
                    id: 1,
                    reference: 'PAY-001',
                    customerName: 'Client 1',
                    amount: 2000,
                    paymentMethod: 'CASH',
                    status: 'SUCCESS',
                    paymentDate: new Date().toISOString(),
                    observation: 'Paiement mensuel'
                },
                // Ajouter plus de paiements simulés...
            ];

            this.filteredPayments = [...this.payments];
            this.loadSummary();
            this.updateCharts();
            this.loading = false;
        }, 1000);
    }

    loadSummary(): void {
        this.paymentManagementService.getPaymentsSummary().subscribe({
            next: (summary) => {
                this.summary = summary;
                this.updateCharts();
            },
            error: (error) => {
                console.error('Erreur lors du chargement du résumé:', error);
            }
        });
    }

    loadPerformanceMetrics(): void {
        this.paymentManagementService.getPerformanceMetrics().subscribe({
            next: (metrics) => {
                this.performance = metrics;
            },
            error: (error) => {
                console.error('Erreur lors du chargement des métriques:', error);
            }
        });
    }

    updateCharts(): void {
        // Graphique des méthodes de paiement
        const methodLabels = Object.keys(this.summary.paymentsByMethod);
        const methodData = Object.values(this.summary.paymentsByMethod);

        this.paymentMethodChart = {
            labels: methodLabels.map(method => this.formatPaymentMethod(method)),
            datasets: [{
                data: methodData,
                backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0'],
                hoverBackgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0']
            }]
        };

        // Graphique de tendance mensuelle
        this.monthlyTrendChart = {
            labels: this.summary.monthlyTrend.map(item => item.month),
            datasets: [{
                label: 'Montant des paiements',
                data: this.summary.monthlyTrend.map(item => item.amount),
                borderColor: '#36A2EB',
                backgroundColor: 'rgba(54, 162, 235, 0.1)',
                fill: true
            }]
        };
    }

    applyFilters(): void {
        this.paymentManagementService.filterPayments(this.filters).subscribe({
            next: (filtered) => {
                this.filteredPayments = filtered;
            },
            error: (error) => {
                console.error('Erreur lors du filtrage:', error);
            }
        });
    }

    clearFilters(): void {
        this.filters = {
            paymentMethod: null,
            dateFrom: null,
            dateTo: null,
            customerId: null,
            minAmount: null,
            maxAmount: null
        };
        this.filteredPayments = [...this.payments];
    }

    viewPaymentDetails(payment: PaymentModel): void {
        this.selectedPayment = payment;
        this.paymentDetailsDialog = true;
    }

    printReceipt(payment: PaymentModel): void {
        const receipt = this.paymentManagementService.generatePaymentReceipt(payment);

        // Créer une nouvelle fenêtre pour l'impression
        const printWindow = window.open('', '_blank');
        if (printWindow) {
            printWindow.document.write(`
        <html>
          <head>
            <title>Reçu de Paiement</title>
            <style>
              body { font-family: monospace; padding: 20px; }
              .receipt { max-width: 400px; margin: 0 auto; }
            </style>
          </head>
          <body>
            <div class="receipt">
              <pre>${receipt}</pre>
            </div>
            <script>window.print(); window.close();</script>
          </body>
        </html>
      `);
        }
    }

    canCancelPayment(payment: PaymentModel): boolean {
        return this.paymentManagementService.canCancelPayment(payment);
    }

    cancelPayment(payment: PaymentModel): void {
        this.messageService.add({
            severity: 'info',
            summary: 'Information',
            detail: 'Fonctionnalité d\'annulation en cours de développement',
            life: 3000
        });
    }

    exportPayments(): void {
        // Créer un CSV des paiements filtrés
        const headers = ['Date', 'Référence', 'Client', 'Montant', 'Méthode', 'Statut'];
        const csvData = this.filteredPayments.map(payment => [
            payment.paymentDate,
            payment.reference,
            payment.customerName,
            payment.amount,
            payment.paymentMethod,
            payment.status
        ]);

        let csvContent = headers.join(',') + '\\n';
        csvData.forEach(row => {
            csvContent += row.join(',') + '\\n';
        });

        const blob = new Blob([csvContent], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'paiements.csv';
        link.click();
        window.URL.revokeObjectURL(url);
    }

    formatPaymentMethod(method?: string): string {
        const methods: { [key: string]: string } = {
            'CASH': 'Espèces',
            'MTN_MONEY': 'MTN Money',
            'ORANGE_MONEY': 'Orange Money',
            'BANK_TRANSFER': 'Virement bancaire'
        };
        return methods[method || ''] || method || 'Non spécifié';
    }

    getPaymentMethodBadgeClass(method?: string): string {
        const classes: { [key: string]: string } = {
            'CASH': 'p-badge-success',
            'MTN_MONEY': 'p-badge-warning',
            'ORANGE_MONEY': 'p-badge-info',
            'BANK_TRANSFER': 'p-badge-secondary'
        };
        return classes[method || ''] || 'p-badge-secondary';
    }

    getStatusBadgeClass(status?: string): string {
        const classes: { [key: string]: string } = {
            'SUCCESS': 'p-badge-success',
            'PENDING': 'p-badge-warning',
            'FAILED': 'p-badge-danger',
            'CANCELLED': 'p-badge-secondary'
        };
        return classes[status || ''] || 'p-badge-secondary';
    }
}
