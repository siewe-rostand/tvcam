import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// PrimeNG Components
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { CheckboxModule } from 'primeng/checkbox';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

// Services
import { MonthlyBillGenerationService, MonthlyGenerationConfig } from '../../../service/monthly-bill-generation.service';

@Component({
  selector: 'app-monthly-generation-config',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ButtonModule,
    CardModule,
    CheckboxModule,
    InputNumberModule,
    InputTextModule,
    ToastModule
  ],
  providers: [MessageService],
  template: `
    <p-card header="Configuration de la Génération Automatique Mensuelle">
      
      <!-- Statut de la génération automatique -->
      <div class="mb-4">
        <div class="flex align-items-center gap-3">
          <p-checkbox 
            [(ngModel)]="config.enabled" 
            binary="true" 
            inputId="autoGenerationEnabled">
          </p-checkbox>
          <label for="autoGenerationEnabled" class="font-semibold">
            Activer la génération automatique mensuelle
          </label>
        </div>
        <small class="text-gray-600">
          Les factures seront générées automatiquement chaque mois selon la configuration ci-dessous
        </small>
      </div>

      <div class="grid" [class.disabled]="!config.enabled">
        
        <!-- Jour de génération -->
        <div class="col-12 md:col-6">
          <label for="dayOfMonth" class="block mb-2">Jour du mois</label>
          <p-inputNumber 
            id="dayOfMonth"
            [(ngModel)]="config.dayOfMonth" 
            [min]="1" 
            [max]="31"
            [disabled]="!config.enabled"
            styleClass="w-full">
          </p-inputNumber>
          <small class="text-gray-600">Jour du mois où générer les factures (1-31)</small>
        </div>

        <!-- Heure de génération -->
        <div class="col-12 md:col-6">
          <label for="generationTime" class="block mb-2">Heure de génération</label>
          <input 
            id="generationTime"
            type="time" 
            pInputText 
            [(ngModel)]="config.time"
            [disabled]="!config.enabled"
            class="w-full">
          <small class="text-gray-600">Heure à laquelle générer les factures</small>
        </div>

        <!-- Montant mensuel par défaut -->
        <div class="col-12 md:col-6">
          <label for="defaultPayment" class="block mb-2">Montant mensuel par défaut (FCFA)</label>
          <p-inputNumber 
            id="defaultPayment"
            [(ngModel)]="config.defaultMonthlyPayment" 
            mode="currency" 
            currency="XAF" 
            locale="fr-FR"
            [disabled]="!config.enabled"
            styleClass="w-full">
          </p-inputNumber>
        </div>

        <!-- Sélection automatique des clients -->
        <div class="col-12 md:col-6">
          <div class="flex align-items-center gap-3">
            <p-checkbox 
              [(ngModel)]="config.autoSelectAllCustomers" 
              binary="true" 
              inputId="autoSelectAll"
              [disabled]="!config.enabled">
            </p-checkbox>
            <label for="autoSelectAll" class="font-medium">
              Sélectionner automatiquement tous les clients actifs
            </label>
          </div>
        </div>

        <!-- Notifications -->
        <div class="col-12">
          <div class="flex align-items-center gap-3">
            <p-checkbox 
              [(ngModel)]="config.notificationEnabled" 
              binary="true" 
              inputId="notificationsEnabled"
              [disabled]="!config.enabled">
            </p-checkbox>
            <label for="notificationsEnabled" class="font-medium">
              Activer les notifications de génération
            </label>
          </div>
          <small class="text-gray-600">
            Recevoir des notifications lors de la génération automatique des factures
          </small>
        </div>

      </div>

      <!-- Actions -->
      <div class="flex justify-content-between align-items-center mt-4">
        <div>
          <p-button 
            label="Demander Permission Notifications" 
            icon="pi pi-bell" 
            (onClick)="requestNotificationPermission()"
            severity="info"
            size="small">
          </p-button>
        </div>
        
        <div class="flex gap-2">
          <p-button 
            label="Annuler" 
            icon="pi pi-times" 
            (onClick)="resetConfig()"
            severity="secondary">
          </p-button>
          <p-button 
            label="Sauvegarder" 
            icon="pi pi-save" 
            (onClick)="saveConfig()"
            [disabled]="!isConfigValid()">
          </p-button>
        </div>
      </div>

      <!-- Test de génération -->
      <div class="mt-4 pt-4 border-top-1 surface-border">
        <h5>Test de Génération</h5>
        <p class="text-gray-600 mb-3">
          Vous pouvez tester la génération manuelle des factures avec la configuration actuelle
        </p>
        <p-button 
          label="Tester la Génération" 
          icon="pi pi-play" 
          (onClick)="testGeneration()"
          [loading]="testing"
          severity="success">
        </p-button>
      </div>

      <!-- Résultat de la dernière génération -->
      <div *ngIf="lastGenerationResult" class="mt-4 pt-4 border-top-1 surface-border">
        <h5>Dernière Génération</h5>
        <div class="grid">
          <div class="col-12 md:col-3">
            <div class="text-center p-3 border-round" 
                 [class]="lastGenerationResult.success ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'">
              <i [class]="lastGenerationResult.success ? 'pi pi-check-circle text-2xl' : 'pi pi-times-circle text-2xl'"></i>
              <p class="font-semibold mt-2">
                {{ lastGenerationResult.success ? 'Succès' : 'Échec' }}
              </p>
            </div>
          </div>
          <div class="col-12 md:col-3">
            <div class="text-center p-3 border-round bg-blue-50 text-blue-700">
              <i class="pi pi-file text-2xl"></i>
              <p class="font-semibold mt-2">{{ lastGenerationResult.billsGenerated }}</p>
              <p class="text-sm">Factures générées</p>
            </div>
          </div>
          <div class="col-12 md:col-3">
            <div class="text-center p-3 border-round bg-gray-50 text-gray-700">
              <i class="pi pi-clock text-2xl"></i>
              <p class="font-semibold mt-2">{{ formatDate(lastGenerationResult.timestamp) }}</p>
              <p class="text-sm">Date/Heure</p>
            </div>
          </div>
          <div class="col-12 md:col-3" *ngIf="lastGenerationResult.errors.length > 0">
            <div class="text-center p-3 border-round bg-orange-50 text-orange-700">
              <i class="pi pi-exclamation-triangle text-2xl"></i>
              <p class="font-semibold mt-2">{{ lastGenerationResult.errors.length }}</p>
              <p class="text-sm">Erreurs</p>
            </div>
          </div>
        </div>
      </div>

    </p-card>

    <p-toast></p-toast>
  `,
  styles: [`
    .disabled {
      opacity: 0.6;
      pointer-events: none;
    }
    
    .border-top-1 {
      border-top: 1px solid var(--surface-border);
    }
  `]
})
export class MonthlyGenerationConfigComponent implements OnInit {
  config: MonthlyGenerationConfig = {
    enabled: true,
    dayOfMonth: 1,
    time: '09:00',
    autoSelectAllCustomers: true,
    defaultMonthlyPayment: 2000,
    notificationEnabled: true
  };

  lastGenerationResult: any = null;
  testing = false;

  constructor(
    private monthlyGenerationService: MonthlyBillGenerationService,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
    this.loadConfig();
    this.subscribeToGenerationResults();
  }

  private loadConfig(): void {
    this.monthlyGenerationService.config$.subscribe(config => {
      this.config = { ...config };
    });
  }

  private subscribeToGenerationResults(): void {
    this.monthlyGenerationService.generationResult$.subscribe(result => {
      if (result) {
        this.lastGenerationResult = result;
      }
    });
  }

  isConfigValid(): boolean {
    const dayOfMonth = Number(this.config.dayOfMonth);
    return !isNaN(dayOfMonth) &&
      dayOfMonth >= 1 &&
      dayOfMonth <= 31 &&
      !!this.config.time &&
      Number(this.config.defaultMonthlyPayment) > 0;
  }

  saveConfig(): void {
    if (!this.isConfigValid()) {
      this.messageService.add({
        severity: 'error',
        summary: 'Configuration invalide',
        detail: 'Veuillez vérifier tous les champs requis'
      });
      return;
    }

    this.monthlyGenerationService.updateConfig(this.config);
    this.messageService.add({
      severity: 'success',
      summary: 'Configuration sauvegardée',
      detail: 'La configuration de génération automatique a été mise à jour'
    });
  }

  resetConfig(): void {
    this.monthlyGenerationService.config$.subscribe(config => {
      this.config = { ...config };
    });
  }

  async requestNotificationPermission(): Promise<void> {
    const granted = await this.monthlyGenerationService.requestNotificationPermission();
    if (granted) {
      this.messageService.add({
        severity: 'success',
        summary: 'Permission accordée',
        detail: 'Les notifications sont maintenant activées'
      });
    } else {
      this.messageService.add({
        severity: 'warn',
        summary: 'Permission refusée',
        detail: 'Les notifications ne seront pas disponibles'
      });
    }
  }

  testGeneration(): void {
    this.testing = true;
    this.monthlyGenerationService.forceGeneration().subscribe({
      next: (result) => {
        this.testing = false;
        this.lastGenerationResult = result;

        if (result.success) {
          this.messageService.add({
            severity: 'success',
            summary: 'Test réussi',
            detail: `${result.billsGenerated} facture(s) générée(s) avec succès`
          });
        } else {
          this.messageService.add({
            severity: 'error',
            summary: 'Test échoué',
            detail: result.errors.join(', ')
          });
        }
      },
      error: (error) => {
        this.testing = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur de test',
          detail: 'Une erreur est survenue lors du test'
        });
      }
    });
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleString('fr-FR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
