import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

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
import { BillDuplicateCheckService } from '../../service/bill-duplicate-check.service';
import { NotificationService } from '../../../_shared/services/notification.service';
import { MonthlyBillGenerationService } from '../../service/monthly-bill-generation.service';

// Models
import { BillModel } from '../../model/bill.model';

// Components
import { NavbarComponent } from '../../../_shared/components/navbar/navbar.component';
import { BillTemplatePreviewComponent } from './bill-template-preview/bill-template-preview.component';
import { MonthlyGenerationConfigComponent } from './monthly-generation-config/monthly-generation-config.component';
import { BillGenerationResultsComponent } from './bill-generation-results/bill-generation-results.component';

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
    BillTemplatePreviewComponent,
    MonthlyGenerationConfigComponent,
    BillGenerationResultsComponent
  ],
  providers: [MessageService, ConfirmationService, BillDuplicateCheckService],
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

        <!-- Configuration de la génération automatique -->
        <app-monthly-generation-config></app-monthly-generation-config>

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

      <!-- Résultats avec nouveau composant -->
      <app-bill-generation-results
        [results]="generationResults"
        [actionType]="generationActionType"
        (continue)="goBackToBills()"
        (viewAll)="goBackToBills()"
        (print)="onPrintResults($event)"
        (printSingle)="onPrintSingleBill($event)"
        (viewBill)="onViewBillDetails($event)">
      </app-bill-generation-results>
    </div>

    <p-toast></p-toast>
    <p-confirmDialog></p-confirmDialog>
  `
})
export class EnhancedBillGenerationComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  customers: Customer[] = [];
  selectedCustomers: Customer[] = [];
  loading = false;
  generationProgress = 0;
  generationResults: BillModel[] = [];
  generationActionType: 'generated' | 'updated' = 'generated';

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
    private billDuplicateCheckService: BillDuplicateCheckService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private notificationService: NotificationService,
    private monthlyGenerationService: MonthlyBillGenerationService,
    private router: Router
  ) {
    this.initializeYears();
    this.setDefaultValues();
  }

  ngOnInit(): void {
    this.loadStatistics();
    this.loadCustomers();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
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
    this.billManagementService.getBillsStatistics()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
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
    // Charger les vrais clients depuis le service
    this.billManagementService.getCustomers()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          console.log('Response from getCustomers:', response); // Debug log

          // Handle different response structures
          let customerArray = response;
          if (response && response.data && Array.isArray(response.data)) {
            customerArray = response.data;
          } else if (response && Array.isArray(response)) {
            customerArray = response;
          } else {
            console.error('Unexpected response structure:', response);
            customerArray = [];
          }

          this.customers = customerArray.map((customer: any) => ({
            id: customer.customerId || customer.id,
            name: customer.name,
            address: customer.address,
            telephone: customer.telephone,
            lastBillDate: customer.lastBillGenerationDate,
            status: customer.isActive ? 'ACTIVE' : 'INACTIVE',
            zoneName: customer.zone?.name,
            zoneId: customer.zone?.id
          }));
          this.loading = false;
        },
        error: (error) => {
          console.error('Erreur lors du chargement des clients:', error);
          this.loading = false;
          this.notificationService.showError('Erreur lors du chargement des clients');
        }
      });
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

    const monthLabel = this.getMonthLabel(this.billRequest.month!);

    // Show initial confirmation
    this.confirmationService.confirm({
      message: `Êtes-vous sûr de vouloir générer les factures pour ${this.selectedCustomers.length} client(s) pour ${monthLabel} ${this.billRequest.year} ?`,
      header: 'Confirmation de génération',
      icon: 'pi pi-question-circle',
      acceptLabel: 'Oui, générer',
      rejectLabel: 'Annuler',
      accept: () => {
        this.performBillGenerationWithDuplicateCheck();
      }
    });
  }

  private performBillGenerationWithDuplicateCheck(): void {
    this.loading = true;
    this.generationProgress = 0;
    this.billRequest.customerIds = this.selectedCustomers.map(c => c.id);

    // Show progress indicator
    const progressInterval = setInterval(() => {
      if (this.generationProgress < 90) {
        this.generationProgress += 10;
      }
    }, 300);

    this.billDuplicateCheckService.handleBillGeneration(
      this.billRequest.customerIds,
      this.billRequest.shouldGenerate
    )
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.loading = false;
          this.generationProgress = 100;
          clearInterval(progressInterval);

          if (result.success && (result.action === 'generated' || result.action === 'updated')) {
            // Success - extract bills from response data
            if (result.data && result.data.generatedBills) {
              this.generationResults = result.data.generatedBills;
            } else if (result.data && Array.isArray(result.data)) {
              this.generationResults = result.data;
            }

            // Set action type for UI display
            this.generationActionType = result.action;

            // Show success message
            this.billDuplicateCheckService.showSuccessMessage(result);

            // Reset selection
            this.selectedCustomers = [];

            // Reload statistics
            this.loadStatistics();
          } else if (result.action === 'cancelled') {
            // User cancelled - show info message
            this.billDuplicateCheckService.showSuccessMessage(result);
            this.generationProgress = 0;
          } else {
            // Error case
            this.billDuplicateCheckService.showErrorMessage(result.message);
            this.generationProgress = 0;
          }
        },
        error: (error) => {
          this.loading = false;
          this.generationProgress = 0;
          clearInterval(progressInterval);

          console.error('Erreur lors de la génération:', error);

          let errorMessage = 'Erreur lors de la génération des factures';
          if (error.error && error.error.message) {
            errorMessage = error.error.message;
          } else if (error.message) {
            errorMessage = error.message;
          }

          this.billDuplicateCheckService.showErrorMessage(errorMessage);
        }
      });
  }

  private getMonthLabel(monthValue: string): string {
    const month = this.months.find(m => m.value === monthValue);
    return month ? month.label : monthValue;
  }

  generateBills(): void {
    // This method is now handled by performBillGenerationWithDuplicateCheck
    // Keep for backward compatibility if needed
    this.performBillGenerationWithDuplicateCheck();
  }

  goBackToBills(): void {
    this.router.navigate(['/receipts']);
  }

  onPrintResults(bills: BillModel[]): void {
    // Navigate to print view with selected bills
    console.log('Printing bills:', bills);
    // TODO: Implement print functionality
    this.messageService.add({
      severity: 'info',
      summary: 'Impression',
      detail: `Préparation de l'impression de ${bills.length} facture(s)...`
    });
  }

  onPrintSingleBill(bill: BillModel): void {
    // Print single bill
    console.log('Printing single bill:', bill);
    // TODO: Implement single bill print
    this.messageService.add({
      severity: 'info',
      summary: 'Impression',
      detail: `Impression de la facture de ${bill.customerName}...`
    });
  }

  onViewBillDetails(bill: BillModel): void {
    // Navigate to bill details view
    console.log('Viewing bill details:', bill);
    // TODO: Implement navigation to bill details
    this.messageService.add({
      severity: 'info',
      summary: 'Détails',
      detail: `Affichage des détails de la facture de ${bill.customerName}`
    });
  }
}
