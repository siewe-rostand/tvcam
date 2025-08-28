import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

// PrimeNG Components
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ChartModule } from 'primeng/chart';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ProgressBarModule } from 'primeng/progressbar';
import { TooltipModule } from 'primeng/tooltip';

// Services
import { BillManagementService } from '../customers/service/bill-management.service';
import { PaymentManagementService } from '../customers/service/payment-management.service';

// Models
import { BillModel } from '../customers/model/bill.model';
import { PaymentModel } from '../customers/model/payment.model';

// Components
import { NavbarComponent } from '../_shared/components/navbar/navbar.component';

interface DashboardStats {
    totalBills: number;
    paidBills: number;
    unpaidBills: number;
    totalRevenue: number;
    monthlyRevenue: number;
    paymentRate: number;
    overdueAmount: number;
}

@Component({
    selector: 'app-enhanced-dashboard',
    standalone: true,
    imports: [
        CommonModule,
        ButtonModule,
        CardModule,
        ChartModule,
        TableModule,
        TagModule,
        ProgressBarModule,
        TooltipModule,
        NavbarComponent
    ],
    template: `
    <app-navbar></app-navbar>
    
    <div class="container mx-auto p-4">
      <div class="mb-4">
        <h1 class="text-3xl font-bold text-gray-800">Tableau de Bord</h1>
        <p class="text-gray-600">Vue d'ensemble de votre activité</p>
      </div>

      <!-- Statistiques principales -->
      <div class="grid mb-6">
        <div class="col-12 md:col-3">
          <p-card styleClass="border-l-4 border-blue-500">
            <div class="flex align-items-center">
              <div class="flex-1">
                <h3 class="text-2xl font-bold text-blue-600 m-0">{{ stats.totalBills }}</h3>
                <p class="text-gray-600 m-0">Total Factures</p>
              </div>
              <i class="pi pi-file text-blue-500 text-3xl"></i>
            </div>
            <div class="mt-3">
              <p-progressBar 
                [value]="(stats.paidBills / stats.totalBills) * 100" 
                [showValue]="false"
                styleClass="h-2">
              </p-progressBar>
              <small class="text-gray-500">{{ stats.paidBills }} payées / {{ stats.totalBills }} total</small>
            </div>
          </p-card>
        </div>

        <div class="col-12 md:col-3">
          <p-card styleClass="border-l-4 border-green-500">
            <div class="flex align-items-center">
              <div class="flex-1">
                <h3 class="text-2xl font-bold text-green-600 m-0">
                  {{ stats.totalRevenue | currency:'XAF':'symbol':'1.0-0' }}
                </h3>
                <p class="text-gray-600 m-0">Revenus Total</p>
              </div>
              <i class="pi pi-dollar text-green-500 text-3xl"></i>
            </div>
            <div class="mt-3">
              <small class="text-green-600">
                +{{ revenueGrowth }}% ce mois
              </small>
            </div>
          </p-card>
        </div>

        <div class="col-12 md:col-3">
          <p-card styleClass="border-l-4 border-orange-500">
            <div class="flex align-items-center">
              <div class="flex-1">
                <h3 class="text-2xl font-bold text-orange-600 m-0">{{ stats.unpaidBills }}</h3>
                <p class="text-gray-600 m-0">Factures Impayées</p>
              </div>
              <i class="pi pi-exclamation-triangle text-orange-500 text-3xl"></i>
            </div>
            <div class="mt-3">
              <small class="text-orange-600">
                {{ stats.overdueAmount | currency:'XAF':'symbol':'1.0-0' }} en retard
              </small>
            </div>
          </p-card>
        </div>

        <div class="col-12 md:col-3">
          <p-card styleClass="border-l-4 border-purple-500">
            <div class="flex align-items-center">
              <div class="flex-1">
                <h3 class="text-2xl font-bold text-purple-600 m-0">{{ stats.paymentRate }}%</h3>
                <p class="text-gray-600 m-0">Taux de Paiement</p>
              </div>
              <i class="pi pi-chart-line text-purple-500 text-3xl"></i>
            </div>
            <div class="mt-3">
              <p-progressBar 
                [value]="stats.paymentRate" 
                [showValue]="false"
                styleClass="h-2">
              </p-progressBar>
            </div>
          </p-card>
        </div>
      </div>

      <!-- Graphiques -->
      <div class="grid mb-6">
        <div class="col-12 md:col-8">
          <p-card header="Évolution des Revenus" styleClass="h-full">
            <p-chart 
              type="line" 
              [data]="revenueChart" 
              [options]="chartOptions"
              height="300px">
            </p-chart>
          </p-card>
        </div>
        
        <div class="col-12 md:col-4">
          <p-card header="Répartition des Paiements" styleClass="h-full">
            <p-chart 
              type="doughnut" 
              [data]="paymentStatusChart" 
              [options]="doughnutOptions"
              height="300px">
            </p-chart>
          </p-card>
        </div>
      </div>

      <!-- Actions rapides -->
      <div class="grid mb-6">
        <div class="col-12">
          <p-card header="Actions Rapides">
            <div class="flex flex-wrap gap-3">
              <p-button 
                label="Générer Factures" 
                icon="pi pi-file-plus" 
                (onClick)="navigateToGeneration()"
                severity="success">
              </p-button>
              <p-button 
                label="Nouveau Paiement" 
                icon="pi pi-dollar" 
                (onClick)="navigateToPayments()"
                severity="info">
              </p-button>
              <p-button 
                label="Voir Factures" 
                icon="pi pi-list" 
                (onClick)="navigateToBills()"
                severity="secondary">
              </p-button>
              <p-button 
                label="Gérer Clients" 
                icon="pi pi-users" 
                (onClick)="navigateToCustomers()"
                severity="help">
              </p-button>
            </div>
          </p-card>
        </div>
      </div>

      <!-- Tables récentes -->
      <div class="grid">
        <div class="col-12 md:col-6">
          <p-card header="Factures Récentes">
            <p-table [value]="recentBills" [rows]="5" styleClass="p-datatable-sm">
              <ng-template pTemplate="header">
                <tr>
                  <th>Client</th>
                  <th>Montant</th>
                  <th>Statut</th>
                  <th>Date</th>
                </tr>
              </ng-template>
              <ng-template pTemplate="body" let-bill>
                <tr>
                  <td>{{ bill.customerName }}</td>
                  <td>{{ bill.netToPay | currency:'XAF':'symbol':'1.0-0' }}</td>
                  <td>
                    <p-tag 
                      [value]="bill.status" 
                      [severity]="getBillStatusSeverity(bill.status)">
                    </p-tag>
                  </td>
                  <td>{{ bill.depositDate | date:'dd/MM/yyyy' }}</td>
                </tr>
              </ng-template>
            </p-table>
            
            <div class="mt-3 text-center">
              <p-button 
                label="Voir Toutes" 
                icon="pi pi-arrow-right" 
                [text]="true"
                (onClick)="navigateToBills()">
              </p-button>
            </div>
          </p-card>
        </div>

        <div class="col-12 md:col-6">
          <p-card header="Paiements Récents">
            <p-table [value]="recentPayments" [rows]="5" styleClass="p-datatable-sm">
              <ng-template pTemplate="header">
                <tr>
                  <th>Client</th>
                  <th>Montant</th>
                  <th>Méthode</th>
                  <th>Date</th>
                </tr>
              </ng-template>
              <ng-template pTemplate="body" let-payment>
                <tr>
                  <td>{{ payment.customerName }}</td>
                  <td>{{ payment.amount | currency:'XAF':'symbol':'1.0-0' }}</td>
                  <td>
                    <p-tag 
                      [value]="formatPaymentMethod(payment.paymentMethod)" 
                      severity="info">
                    </p-tag>
                  </td>
                  <td>{{ payment.paymentDate | date:'dd/MM/yyyy' }}</td>
                </tr>
              </ng-template>
            </p-table>
            
            <div class="mt-3 text-center">
              <p-button 
                label="Voir Tous" 
                icon="pi pi-arrow-right" 
                [text]="true"
                (onClick)="navigateToPayments()">
              </p-button>
            </div>
          </p-card>
        </div>
      </div>

      <!-- Alertes -->
      <div class="grid mt-4" *ngIf="overdueBills.length > 0">
        <div class="col-12">
          <p-card 
            header="⚠️ Factures en Retard" 
            styleClass="border-l-4 border-red-500 bg-red-50">
            
            <p class="text-red-700 mb-3">
              Vous avez {{ overdueBills.length }} facture(s) en retard nécessitant une attention immédiate.
            </p>
            
            <p-table [value]="overdueBills" [rows]="3" styleClass="p-datatable-sm">
              <ng-template pTemplate="header">
                <tr>
                  <th>Client</th>
                  <th>Montant</th>
                  <th>Date Limite</th>
                  <th>Jours de Retard</th>
                  <th>Action</th>
                </tr>
              </ng-template>
              <ng-template pTemplate="body" let-bill>
                <tr>
                  <td>{{ bill.customerName }}</td>
                  <td>{{ bill.remainingBalance | currency:'XAF':'symbol':'1.0-0' }}</td>
                  <td>{{ bill.deadLine | date:'dd/MM/yyyy' }}</td>
                  <td>
                    <span class="text-red-600 font-bold">
                      {{ getDaysOverdue(bill.deadLine) }}
                    </span>
                  </td>
                  <td>
                    <p-button 
                      label="Relancer" 
                      icon="pi pi-send" 
                      size="small"
                      severity="danger"
                      (onClick)="sendReminder(bill)">
                    </p-button>
                  </td>
                </tr>
              </ng-template>
            </p-table>
          </p-card>
        </div>
      </div>
    </div>
  `
})
export class EnhancedDashboardComponent implements OnInit {
    stats: DashboardStats = {
        totalBills: 0,
        paidBills: 0,
        unpaidBills: 0,
        totalRevenue: 0,
        monthlyRevenue: 0,
        paymentRate: 0,
        overdueAmount: 0
    };

    recentBills: BillModel[] = [];
    recentPayments: PaymentModel[] = [];
    overdueBills: BillModel[] = [];

    revenueGrowth = 0;

    revenueChart: any = {};
    paymentStatusChart: any = {};
    chartOptions: any = {};
    doughnutOptions: any = {};

    constructor(
        private billManagementService: BillManagementService,
        private paymentManagementService: PaymentManagementService,
        private router: Router
    ) {
        this.initializeChartOptions();
    }

    ngOnInit(): void {
        this.loadDashboardData();
    }

    private initializeChartOptions(): void {
        this.chartOptions = {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function (value: any) {
                            return value.toLocaleString('fr-FR') + ' FCFA';
                        }
                    }
                }
            },
            plugins: {
                legend: {
                    position: 'bottom'
                },
                tooltip: {
                    callbacks: {
                        label: function (context: any) {
                            return context.parsed.y.toLocaleString('fr-FR') + ' FCFA';
                        }
                    }
                }
            }
        };

        this.doughnutOptions = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        };
    }

    loadDashboardData(): void {
        // Charger les statistiques des factures
        this.billManagementService.getBillsStatistics().subscribe({
            next: (billStats) => {
                this.stats.totalBills = billStats.total;
                this.stats.paidBills = billStats.paid;
                this.stats.unpaidBills = billStats.unpaid;
                this.stats.totalRevenue = billStats.paidAmount;
                this.stats.paymentRate = Math.round(billStats.paymentRate);
                this.updateCharts();
            }
        });

        // Charger les factures récentes
        this.billManagementService.loadBills().subscribe({
            next: (bills) => {
                this.recentBills = bills.slice(0, 5);
                this.loadOverdueBills();
            }
        });

        // Charger les métriques de performance des paiements
        this.paymentManagementService.getPerformanceMetrics().subscribe({
            next: (metrics) => {
                this.revenueGrowth = Math.round(metrics.growth);
                this.stats.monthlyRevenue = metrics.thisMonthAmount;
            }
        });

        // Simuler les paiements récents
        this.loadRecentPayments();
    }

    loadOverdueBills(): void {
        this.billManagementService.getOverdueBills().subscribe({
            next: (overdue) => {
                this.overdueBills = overdue.slice(0, 3);
                this.stats.overdueAmount = overdue.reduce((sum, bill) =>
                    sum + (bill.remainingBalance || 0), 0);
            }
        });
    }

    loadRecentPayments(): void {
        // Simuler les paiements récents - remplacer par un vrai appel API
        this.recentPayments = [
            {
                customerName: 'Client 1',
                amount: 2000,
                paymentMethod: 'CASH',
                paymentDate: new Date().toISOString()
            },
            // Ajouter plus de paiements simulés...
        ];
    }

    updateCharts(): void {
        // Graphique des revenus
        this.revenueChart = {
            labels: ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Jun'],
            datasets: [{
                label: 'Revenus Mensuels',
                data: [65000, 59000, 80000, 81000, 56000, 75000],
                borderColor: '#42A5F5',
                backgroundColor: 'rgba(66, 165, 245, 0.1)',
                fill: true
            }]
        };

        // Graphique des statuts de paiement
        this.paymentStatusChart = {
            labels: ['Payées', 'Impayées', 'Partiellement Payées'],
            datasets: [{
                data: [this.stats.paidBills, this.stats.unpaidBills,
                this.stats.totalBills - this.stats.paidBills - this.stats.unpaidBills],
                backgroundColor: ['#4CAF50', '#F44336', '#FF9800'],
                hoverBackgroundColor: ['#66BB6A', '#EF5350', '#FFB74D']
            }]
        };
    }

    // Navigation methods
    navigateToGeneration(): void {
        this.router.navigate(['/receipts/generate']);
    }

    navigateToPayments(): void {
        this.router.navigate(['/payment']);
    }

    navigateToBills(): void {
        this.router.navigate(['/receipts']);
    }

    navigateToCustomers(): void {
        this.router.navigate(['/customers']);
    }

    // Utility methods
    getBillStatusSeverity(status?: string): 'success' | 'info' | 'warning' | 'danger' {
        switch (status) {
            case 'PAID': return 'success';
            case 'PARTIALLY_PAID': return 'warning';
            case 'UNPAID': return 'danger';
            default: return 'info';
        }
    }

    formatPaymentMethod(method?: string): string {
        const methods: { [key: string]: string } = {
            'CASH': 'Espèces',
            'MTN_MONEY': 'MTN',
            'ORANGE_MONEY': 'Orange',
            'BANK_TRANSFER': 'Virement'
        };
        return methods[method || ''] || method || 'N/A';
    }

    getDaysOverdue(deadline?: string): number {
        if (!deadline) return 0;
        const deadlineDate = new Date(deadline);
        const today = new Date();
        const diffTime = today.getTime() - deadlineDate.getTime();
        return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    }

    sendReminder(bill: BillModel): void {
        // Simuler l'envoi de rappel
        console.log('Envoi de rappel pour la facture:', bill);
        // Ici, vous pouvez implémenter l'envoi d'email ou SMS
    }
}
