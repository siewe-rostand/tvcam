import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

// PrimeNG Components
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { MultiSelectModule } from 'primeng/multiselect';
import { ProgressBarModule } from 'primeng/progressbar';
import { TableModule } from 'primeng/table';
import { ToastModule } from 'primeng/toast';
import { ToolbarModule } from 'primeng/toolbar';
import { CheckboxModule } from 'primeng/checkbox';
import { CalendarModule } from 'primeng/calendar';
import { InputNumberModule } from 'primeng/inputnumber';
import { MessageService, ConfirmationService } from 'primeng/api';
import { ConfirmDialogModule } from 'primeng/confirmdialog';

// Services
import { BillManagementService } from '../../service/bill-management.service';
import { BillService } from '../../service/bill.service';
import { NotificationService } from '../../../_shared/services/notification.service';

// Models
import { BillModel } from '../../model/bill.model';

// Components
import { NavbarComponent } from '../../../_shared/components/navbar/navbar.component';
import { BillTemplatePreviewComponent } from './bill-template-preview/bill-template-preview.component';

interface Customer {
  id: number;
  name: string;
  address?: string;
  telephone?: string;
  lastBillDate?: string;
  status?: string;
}

interface BillGenerationRequest {
  customerIds: number[];
  month?: string;
  year?: string;
  monthlyPayment?: number;
  observation?: string;
  shouldGenerate: boolean;
}

@Component({
  selector: 'app-enhanced-bill-generation',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ButtonModule,
    CardModule,
    DialogModule,
    DropdownModule,
    InputTextModule,
    MultiSelectModule,
    ProgressBarModule,
    TableModule,
    ToastModule,
    ToolbarModule,
    CheckboxModule,
    CalendarModule,
    InputNumberModule,
    ConfirmDialogModule,
    NavbarComponent,
    BillTemplatePreviewComponent
  ],
  providers: [MessageService, ConfirmationService],
  template: `
    <app-navbar></app-navbar>
    
    <div class="container mx-auto p-4">
      <p-card header="Génération Avancée de Factures" styleClass="mb-4">
        
        <!-- Statistiques -->
        <div class="grid mb-4">
          <div class="col-12 md:col-3">
            <p-card>
              <div class="text-center">
                <h3 class="text-2xl font-bold text-blue-600">{{statistics.total}}</h3>
                <p class="text-gray-600">Total Factures</p>
              </div>
            </p-card>
          </div>
          <div class="col-12 md:col-3">
            <p-card>
              <div class="text-center">
                <h3 class="text-2xl font-bold text-green-600">{{statistics.paid}}</h3>
                <p class="text-gray-600">Payées</p>
              </div>
            </p-card>
          </div>
          <div class="col-12 md:col-3">
            <p-card>
              <div class="text-center">
                <h3 class="text-2xl font-bold text-red-600">{{statistics.unpaid}}</h3>
                <p class="text-gray-600">Impayées</p>
              </div>
            </p-card>
          </div>
          <div class="col-12 md:col-3">
            <p-card>
              <div class="text-center">
                <h3 class="text-2xl font-bold text-orange-600">{{statistics.paymentRate | number:'1.1-1'}}%</h3>
                <p class="text-gray-600">Taux de Paiement</p>
              </div>
            </p-card>
          </div>
        </div>

        <!-- Formulaire de génération -->
        <p-card header="Paramètres de Génération" styleClass="mb-4">
          <div class="flex justify-content-between align-items-center mb-3">
            <h5 class="m-0">Configuration des Factures</h5>
            <app-bill-template-preview></app-bill-template-preview>
          </div>
          
          <form (ngSubmit)="generateBills()" #billForm="ngForm">
            <div class="grid">
              <div class="col-12 md:col-6">
                <label for="month" class="block mb-2">Mois</label>
                <p-dropdown 
                  id="month"
                  [options]="months" 
                  [(ngModel)]="billRequest.month" 
                  name="month"
                  optionLabel="label" 
                  optionValue="value"
                  placeholder="Sélectionnez le mois"
                  styleClass="w-full">
                </p-dropdown>
              </div>
              
              <div class="col-12 md:col-6">
                <label for="year" class="block mb-2">Année</label>
                <p-dropdown 
                  id="year"
                  [options]="years" 
                  [(ngModel)]="billRequest.year" 
                  name="year"
                  optionLabel="label" 
                  optionValue="value"
                  placeholder="Sélectionnez l'année"
                  styleClass="w-full">
                </p-dropdown>
              </div>
              
              <div class="col-12 md:col-6">
                <label for="monthlyPayment" class="block mb-2">Montant Mensuel (FCFA)</label>
                <p-inputNumber 
                  id="monthlyPayment"
                  [(ngModel)]="billRequest.monthlyPayment" 
                  name="monthlyPayment"
                  mode="currency" 
                  currency="XAF" 
                  locale="fr-FR"
                  styleClass="w-full">
                </p-inputNumber>
              </div>
              
              <div class="col-12 md:col-6">
                <label for="observation" class="block mb-2">Observation</label>
                <input 
                  id="observation"
                  type="text" 
                  pInputText 
                  [(ngModel)]="billRequest.observation" 
                  name="observation"
                  placeholder="Observation (optionnel)"
                  class="w-full">
              </div>
              
              <div class="col-12">
                <div class="flex align-items-center">
                  <p-checkbox 
                    [(ngModel)]="billRequest.shouldGenerate" 
                    name="shouldGenerate"
                    binary="true"
                    inputId="shouldGenerate">
                  </p-checkbox>
                  <label for="shouldGenerate" class="ml-2">
                    Forcer la génération (même si déjà générée ce mois)
                  </label>
                </div>
              </div>
            </div>
          </form>
        </p-card>

        <!-- Sélection des clients -->
        <p-card header="Sélection des Clients">
          <p-toolbar styleClass="mb-4">
            <ng-template pTemplate="left">
              <p-button 
                label="Sélectionner Tout" 
                icon="pi pi-check-square" 
                (onClick)="selectAllCustomers()"
                [disabled]="loading">
              </p-button>
              <p-button 
                label="Désélectionner Tout" 
                icon="pi pi-square" 
                (onClick)="unselectAllCustomers()"
                [disabled]="loading"
                styleClass="ml-2">
              </p-button>
            </ng-template>
            
            <ng-template pTemplate="right">
              <p-button 
                label="Générer les Factures" 
                icon="pi pi-file-plus" 
                (onClick)="confirmGenerateBills()"
                [disabled]="loading || selectedCustomers.length === 0"
                severity="success">
              </p-button>
            </ng-template>
          </p-toolbar>

          <p-table 
            [value]="customers" 
            [loading]="loading"
            [rows]="10" 
            [paginator]="true"
            [rowsPerPageOptions]="[10, 25, 50]"
            [(selection)]="selectedCustomers"
            dataKey="id"
            styleClass="p-datatable-customers">
            
            <ng-template pTemplate="header">
              <tr>
                <th style="width: 3rem">
                  <p-tableHeaderCheckbox></p-tableHeaderCheckbox>
                </th>
                <th>Nom du Client</th>
                <th>Adresse</th>
                <th>Téléphone</th>
                <th>Dernière Facture</th>
                <th>Statut</th>
              </tr>
            </ng-template>
            
            <ng-template pTemplate="body" let-customer>
              <tr>
                <td>
                  <p-tableCheckbox [value]="customer"></p-tableCheckbox>
                </td>
                <td>{{ customer.name }}</td>
                <td>{{ customer.address || 'N/A' }}</td>
                <td>{{ customer.telephone || 'N/A' }}</td>
                <td>{{ customer.lastBillDate ? (customer.lastBillDate | date:'dd/MM/yyyy') : 'Jamais' }}</td>
                <td>
                  <span 
                    [class]="'p-badge ' + (customer.status === 'ACTIVE' ? 'p-badge-success' : 'p-badge-warning')">
                    {{ customer.status }}
                  </span>
                </td>
              </tr>
            </ng-template>
          </p-table>
        </p-card>

        <!-- Barre de progression -->
        <p-progressBar 
          *ngIf="generationProgress > 0" 
          [value]="generationProgress" 
          styleClass="mt-3">
        </p-progressBar>
      </p-card>

      <!-- Résultats -->
      <p-card 
        *ngIf="generationResults.length > 0" 
        header="Résultats de la Génération" 
        styleClass="mt-4">
        
        <div class="mb-3">
          <p class="text-lg">
            <strong>{{ generationResults.length }}</strong> facture(s) générée(s) avec succès
          </p>
        </div>

        <p-table [value]="generationResults" [rows]="5" [paginator]="true">
          <ng-template pTemplate="header">
            <tr>
              <th>Client</th>
              <th>Mois</th>
              <th>Année</th>
              <th>Montant</th>
              <th>Statut</th>
            </tr>
          </ng-template>
          
          <ng-template pTemplate="body" let-result>
            <tr>
              <td>{{ result.customerName }}</td>
              <td>{{ result.month }}</td>
              <td>{{ result.year }}</td>
              <td>{{ result.netToPay | currency:'XAF':'symbol':'1.0-0' }}</td>
              <td>
                <span class="p-badge p-badge-success">Générée</span>
              </td>
            </tr>
          </ng-template>
        </p-table>

        <div class="mt-3">
          <p-button 
            label="Retour aux Factures" 
            icon="pi pi-arrow-left" 
            (onClick)="goBackToBills()"
            severity="secondary">
          </p-button>
        </div>
      </p-card>
    </div>

    <p-toast></p-toast>
    <p-confirmDialog></p-confirmDialog>
  `
})
export class EnhancedBillGenerationComponent implements OnInit {
  customers: Customer[] = [];
  selectedCustomers: Customer[] = [];
  loading = false;
  generationProgress = 0;
  generationResults: BillModel[] = [];

  billRequest: BillGenerationRequest = {
    customerIds: [],
    shouldGenerate: false
  };

  statistics = {
    total: 0,
    paid: 0,
    unpaid: 0,
    partiallyPaid: 0,
    paymentRate: 0
  };

  months = [
    { label: 'Janvier', value: 'january' },
    { label: 'Février', value: 'february' },
    { label: 'Mars', value: 'march' },
    { label: 'Avril', value: 'april' },
    { label: 'Mai', value: 'may' },
    { label: 'Juin', value: 'june' },
    { label: 'Juillet', value: 'july' },
    { label: 'Août', value: 'august' },
    { label: 'Septembre', value: 'september' },
    { label: 'Octobre', value: 'october' },
    { label: 'Novembre', value: 'november' },
    { label: 'Décembre', value: 'december' }
  ];

  years: { label: string; value: string }[] = [];

  constructor(
    private billManagementService: BillManagementService,
    private billService: BillService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private notificationService: NotificationService,
    private router: Router
  ) {
    this.initializeYears();
    this.setDefaultValues();
  }

  ngOnInit(): void {
    this.loadStatistics();
    this.loadCustomers();
  }

  private initializeYears(): void {
    const currentYear = new Date().getFullYear();
    for (let i = currentYear - 2; i <= currentYear + 1; i++) {
      this.years.push({ label: i.toString(), value: i.toString() });
    }
  }

  private setDefaultValues(): void {
    const now = new Date();
    this.billRequest.month = this.months[now.getMonth()].value;
    this.billRequest.year = now.getFullYear().toString();
    this.billRequest.monthlyPayment = 2000;
  }

  loadStatistics(): void {
    this.billManagementService.getBillsStatistics().subscribe({
      next: (stats) => {
        this.statistics = stats;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des statistiques:', error);
      }
    });
  }

  loadCustomers(): void {
    this.loading = true;
    // Simuler le chargement des clients - à remplacer par un vrai service
    setTimeout(() => {
      this.customers = [
        { id: 1, name: 'Client 1', address: 'Adresse 1', telephone: '123456789', status: 'ACTIVE' },
        { id: 2, name: 'Client 2', address: 'Adresse 2', telephone: '987654321', status: 'ACTIVE' },
        // Ajouter plus de clients...
      ];
      this.loading = false;
    }, 1000);
  }

  selectAllCustomers(): void {
    this.selectedCustomers = [...this.customers];
  }

  unselectAllCustomers(): void {
    this.selectedCustomers = [];
  }

  confirmGenerateBills(): void {
    if (!this.billRequest.month || !this.billRequest.year) {
      this.notificationService.showWarning('Veuillez sélectionner le mois et l\'année');
      return;
    }

    if (this.selectedCustomers.length === 0) {
      this.notificationService.showWarning('Veuillez sélectionner au moins un client');
      return;
    }

    // Vérifier d'abord s'il y a des factures existantes pour ce mois
    this.loading = true;
    this.billManagementService.checkExistingBillsForMonth(
      this.selectedCustomers.map(c => c.id),
      this.billRequest.month!,
      this.billRequest.year!
    ).subscribe({
      next: (response) => {
        this.loading = false;

        if (response.hasExistingBills && !this.billRequest.shouldGenerate) {
          // Des factures existent déjà pour ce mois
          const monthLabel = this.getMonthLabel(this.billRequest.month!);
          this.notificationService.showBillAlreadyGenerated(monthLabel, this.billRequest.year!);

          this.confirmationService.confirm({
            message: `Des factures ont déjà été générées pour ${monthLabel} ${this.billRequest.year} pour certains clients sélectionnés.\n\nCliquez sur "Oui" pour régénérer les factures (cela remplacera les factures existantes) ou sur "Non" pour annuler.`,
            header: 'Factures déjà générées ce mois',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Oui, régénérer',
            rejectLabel: 'Non, annuler',
            accept: () => {
              this.billRequest.shouldGenerate = true;
              this.performBillGeneration();
            }
          });
        } else {
          // Pas de factures existantes ou génération forcée
          const monthLabel = this.getMonthLabel(this.billRequest.month!);
          this.confirmationService.confirm({
            message: `Êtes-vous sûr de vouloir générer les factures pour ${this.selectedCustomers.length} client(s) pour ${monthLabel} ${this.billRequest.year} ?`,
            header: 'Confirmation de génération',
            icon: 'pi pi-exclamation-triangle',
            accept: () => {
              this.performBillGeneration();
            }
          });
        }
      },
      error: (error) => {
        this.loading = false;
        console.error('Erreur lors de la vérification:', error);
        this.notificationService.showError('Erreur lors de la vérification des factures existantes');
      }
    });
  }

  private getMonthLabel(monthValue: string): string {
    const month = this.months.find(m => m.value === monthValue);
    return month ? month.label : monthValue;
  }

  private performBillGeneration(): void {
    this.generateBills();
  }

  generateBills(): void {
    if (this.selectedCustomers.length === 0) {
      this.notificationService.showWarning('Veuillez sélectionner au moins un client');
      return;
    }

    this.loading = true;
    this.generationProgress = 0;
    this.billRequest.customerIds = this.selectedCustomers.map(c => c.id);

    // Afficher le message de progression
    this.notificationService.showBillGenerationInProgress(this.selectedCustomers.length);

    // Simuler la progression
    const progressInterval = setInterval(() => {
      this.generationProgress += 10;
      if (this.generationProgress >= 100) {
        clearInterval(progressInterval);
      }
    }, 200);

    this.billManagementService.generateBillsForCustomers(
      this.billRequest.customerIds,
      this.billRequest.shouldGenerate
    ).subscribe({
      next: (results) => {
        this.loading = false;
        this.generationProgress = 100;
        this.generationResults = Array.isArray(results) ? results : [results];

        const monthLabel = this.getMonthLabel(this.billRequest.month!);
        this.notificationService.showBillGenerationSuccess(
          this.generationResults.length,
          monthLabel,
          this.billRequest.year!
        );

        // Réinitialiser la sélection
        this.selectedCustomers = [];
      },
      error: (error) => {
        this.loading = false;
        this.generationProgress = 0;
        clearInterval(progressInterval);

        console.error('Erreur lors de la génération:', error);

        // Afficher un message d'erreur approprié selon le type d'erreur
        let errorMessage = 'Erreur lors de la génération des factures';
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        } else if (error.message) {
          errorMessage = error.message;
        }

        this.notificationService.showBillGenerationError(errorMessage);
      }
    });
  }

  goBackToBills(): void {
    this.router.navigate(['/receipts']);
  }
}
