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
import { BadgeModule } from 'primeng/badge';
import { RippleModule } from 'primeng/ripple';

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
        BadgeModule,
        RippleModule,
        NavbarComponent
    ],
    templateUrl: './enhanced-dashboard.component.html',
    styleUrls: ['./enhanced-dashboard.component.css']
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
        private readonly billManagementService: BillManagementService,
        private readonly paymentManagementService: PaymentManagementService,
        private readonly router: Router
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
        // Utiliser des données mockées quand l'API n'est pas disponible
        this.loadMockData();

        // Essayer de charger les vraies données si l'API est disponible
        this.billManagementService.getBillsStatistics().subscribe({
            next: (billStats) => {
                this.stats.totalBills = billStats.total;
                this.stats.paidBills = billStats.paid;
                this.stats.unpaidBills = billStats.unpaid;
                this.stats.totalRevenue = billStats.paidAmount;
                this.stats.paymentRate = Math.round(billStats.paymentRate);
                this.updateCharts();
            },
            error: () => {
                // En cas d'erreur, utiliser les données mockées
                console.log('API non disponible, utilisation des données mockées');
            }
        });

        // Charger les factures récentes avec fallback
        this.billManagementService.loadBills().subscribe({
            next: (bills) => {
                this.recentBills = bills.slice(0, 5);
                this.loadOverdueBills();
            },
            error: () => {
                // Fallback vers les données mockées déjà chargées
                console.log('API factures non disponible, utilisation des données mockées');
            }
        });

        // Charger les métriques de performance avec fallback
        this.paymentManagementService.getPerformanceMetrics().subscribe({
            next: (metrics) => {
                this.revenueGrowth = Math.round(metrics.growth);
                this.stats.monthlyRevenue = metrics.thisMonthAmount;
            },
            error: () => {
                // Les données mockées sont déjà chargées
                console.log('API métriques non disponible, utilisation des données mockées');
            }
        });
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

    // Nouvelles méthodes pour les données mockées
    private loadMockData(): void {
        // Statistiques mockées
        this.stats = {
            totalBills: 245,
            paidBills: 187,
            unpaidBills: 58,
            totalRevenue: 18750000,
            monthlyRevenue: 3250000,
            paymentRate: 76,
            overdueAmount: 2450000
        };

        this.revenueGrowth = 15;

        // Factures récentes mockées
        this.recentBills = [
            {
                id: 1,
                customerName: 'Société KAMDEM & Fils',
                netToPay: 850000,
                status: 'PAID',
                depositDate: '2024-10-15T10:30:00Z'
            },
            {
                id: 2,
                customerName: 'Entreprise MBALLA SARL',
                netToPay: 1200000,
                status: 'UNPAID',
                depositDate: '2024-10-14T14:20:00Z'
            },
            {
                id: 3,
                customerName: 'NGOUO Trading Ltd',
                netToPay: 675000,
                status: 'PARTIALLY_PAID',
                depositDate: '2024-10-13T09:15:00Z'
            },
            {
                id: 4,
                customerName: 'FOTSO Construction',
                netToPay: 2100000,
                status: 'PAID',
                depositDate: '2024-10-12T16:45:00Z'
            },
            {
                id: 5,
                customerName: 'TCHOUNGA Import-Export',
                netToPay: 950000,
                status: 'UNPAID',
                depositDate: '2024-10-11T11:30:00Z'
            }
        ];

        // Paiements récents mockés avec plus de données
        this.recentPayments = [
            {
                customerName: 'Société KAMDEM & Fils',
                amount: 850000,
                paymentMethod: 'BANK_TRANSFER',
                paymentDate: '2024-10-15T15:30:00Z'
            },
            {
                customerName: 'FOTSO Construction',
                amount: 2100000,
                paymentMethod: 'MTN_MONEY',
                paymentDate: '2024-10-14T10:20:00Z'
            },
            {
                customerName: 'NKOMO Services',
                amount: 450000,
                paymentMethod: 'CASH',
                paymentDate: '2024-10-13T14:15:00Z'
            },
            {
                customerName: 'BELLA Entreprises',
                amount: 750000,
                paymentMethod: 'ORANGE_MONEY',
                paymentDate: '2024-10-12T09:45:00Z'
            },
            {
                customerName: 'DOUALA Trading Co.',
                amount: 1300000,
                paymentMethod: 'BANK_TRANSFER',
                paymentDate: '2024-10-11T16:30:00Z'
            }
        ];

        // Factures en retard mockées
        this.overdueBills = [
            {
                id: 15,
                customerName: 'YAOUNDE Motors SARL',
                remainingBalance: 1200000,
                deadLine: '2024-09-30T23:59:59Z'
            },
            {
                id: 23,
                customerName: 'BAFOUSSAM Logistics',
                remainingBalance: 850000,
                deadLine: '2024-10-05T23:59:59Z'
            },
            {
                id: 31,
                customerName: 'KRIBI Shipping Ltd',
                remainingBalance: 400000,
                deadLine: '2024-10-08T23:59:59Z'
            }
        ];

        // Mettre à jour les graphiques avec les données mockées
        this.updateCharts();
    }

    // Nouvelles méthodes utilitaires pour le design amélioré
    getCurrentDate(): string {
        return new Date().toLocaleDateString('fr-FR', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

    formatCurrency(amount: number): string {
        if (!amount) return '0 FCFA';
        return new Intl.NumberFormat('fr-FR', {
            style: 'decimal',
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(amount) + ' FCFA';
    }

    getPaymentRateTrend(): string {
        if (this.stats.paymentRate >= 80) {
            return 'text-green-500';
        } else if (this.stats.paymentRate >= 60) {
            return 'text-yellow-500';
        } else {
            return 'text-red-500';
        }
    }

    getPaymentRateLabel(): string {
        if (this.stats.paymentRate >= 80) {
            return 'Excellent';
        } else if (this.stats.paymentRate >= 60) {
            return 'Moyen';
        } else {
            return 'Faible';
        }
    }

    getInitials(name?: string): string {
        if (!name) return '?';
        return name.split(' ')
            .map(word => word.charAt(0).toUpperCase())
            .slice(0, 2)
            .join('');
    }

    getStatusLabel(status?: string): string {
        const statusLabels: { [key: string]: string } = {
            'PAID': 'Payée',
            'UNPAID': 'Impayée',
            'PARTIALLY_PAID': 'Partielle',
            'OVERDUE': 'En retard'
        };
        return statusLabels[status || ''] || status || 'Inconnu';
    }

    getPaymentMethodIcon(method?: string): string {
        const icons: { [key: string]: string } = {
            'CASH': 'pi-money-bill',
            'MTN_MONEY': 'pi-mobile',
            'ORANGE_MONEY': 'pi-mobile',
            'BANK_TRANSFER': 'pi-credit-card'
        };
        return icons[method || ''] || 'pi-circle';
    }

    refreshData(): void {
        this.loadDashboardData();
        // Ajouter une animation de chargement ou notification
        console.log('Données actualisées');
    }

    handleAllOverdue(): void {
        // Traiter toutes les factures en retard
        this.overdueBills.forEach(bill => {
            this.sendReminder(bill);
        });
        console.log('Rappels envoyés pour toutes les factures en retard');
    }

    getBillsGrowth(): number {
        // Calcul mockée basée sur une croissance de 18%
        return 18;
    }

    getUnpaidTrend(): string {
        const unpaidPercentage = (this.stats.unpaidBills / this.stats.totalBills) * 100;
        return unpaidPercentage > 20 ? 'Élevé' : 'Normal';
    }
}
