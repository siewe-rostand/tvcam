import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';

// PrimeNG Components
import { CardModule } from 'primeng/card';
import { ProgressBarModule } from 'primeng/progressbar';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';

// Services
import { MonthlyBillGenerationService, GenerationResult } from '../../../service/monthly-bill-generation.service';

@Component({
  selector: 'app-monthly-generation-stats',
  standalone: true,
  imports: [
    CommonModule,
    CardModule,
    ProgressBarModule,
    TagModule,
    ButtonModule
  ],
  template: `
    <div class="grid">
      
      <!-- Configuration actuelle -->
      <div class="col-12 md:col-6">
        <p-card header="Configuration Actuelle">
          <div class="grid">
            <div class="col-12">
              <div class="flex justify-content-between align-items-center mb-2">
                <span class="font-medium">Génération automatique</span>
                <p-tag 
                  [value]="config.enabled ? 'Activée' : 'Désactivée'"
                  [severity]="config.enabled ? 'success' : 'danger'">
                </p-tag>
              </div>
            </div>
            
            <div class="col-12" *ngIf="config.enabled">
              <div class="flex justify-content-between align-items-center mb-2">
                <span class="font-medium">Jour du mois</span>
                <span class="font-bold">{{ config.dayOfMonth }}</span>
              </div>
              
              <div class="flex justify-content-between align-items-center mb-2">
                <span class="font-medium">Heure</span>
                <span class="font-bold">{{ config.time }}</span>
              </div>
              
              <div class="flex justify-content-between align-items-center mb-2">
                <span class="font-medium">Montant par défaut</span>
                <span class="font-bold">{{ config.defaultMonthlyPayment | currency:'XAF':'symbol':'1.0-0' }}</span>
              </div>
              
              <div class="flex justify-content-between align-items-center">
                <span class="font-medium">Sélection auto</span>
                <p-tag 
                  [value]="config.autoSelectAllCustomers ? 'Oui' : 'Non'"
                  [severity]="config.autoSelectAllCustomers ? 'success' : 'warning'">
                </p-tag>
              </div>
            </div>
          </div>
        </p-card>
      </div>

      <!-- Prochaine génération -->
      <div class="col-12 md:col-6">
        <p-card header="Prochaine Génération">
          <div class="text-center">
            <div *ngIf="config.enabled; else disabledMessage">
              <div class="text-6xl mb-3">
                <i class="pi pi-calendar text-blue-500"></i>
              </div>
              <h4 class="text-blue-600 mb-2">Le {{ config.dayOfMonth }} de chaque mois</h4>
              <p class="text-gray-600 mb-3">à {{ config.time }}</p>
              
              <div class="mt-4">
                <p-button 
                  label="Tester Maintenant" 
                  icon="pi pi-play" 
                  (onClick)="testGeneration()"
                  [loading]="testing"
                  size="small">
                </p-button>
              </div>
            </div>
            
            <ng-template #disabledMessage>
              <div class="text-6xl mb-3">
                <i class="pi pi-pause-circle text-gray-400"></i>
              </div>
              <h4 class="text-gray-500 mb-2">Génération désactivée</h4>
              <p class="text-gray-600">Activez la génération automatique dans les paramètres</p>
            </ng-template>
          </div>
        </p-card>
      </div>

      <!-- Dernière génération -->
      <div class="col-12" *ngIf="lastGenerationResult">
        <p-card header="Dernière Génération">
          <div class="grid">
            <div class="col-12 md:col-3">
              <div class="text-center p-3 border-round" 
                   [class]="lastGenerationResult.success ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'">
                <i [class]="lastGenerationResult.success ? 'pi pi-check-circle text-4xl' : 'pi pi-times-circle text-4xl'"></i>
                <p class="font-bold text-lg mt-2">
                  {{ lastGenerationResult.success ? 'Succès' : 'Échec' }}
                </p>
              </div>
            </div>
            
            <div class="col-12 md:col-3">
              <div class="text-center p-3 border-round bg-blue-50 text-blue-700">
                <i class="pi pi-file text-4xl"></i>
                <p class="font-bold text-lg mt-2">{{ lastGenerationResult.billsGenerated }}</p>
                <p class="text-sm">Factures générées</p>
              </div>
            </div>
            
            <div class="col-12 md:col-3">
              <div class="text-center p-3 border-round bg-gray-50 text-gray-700">
                <i class="pi pi-clock text-4xl"></i>
                <p class="font-bold text-lg mt-2">{{ formatTime(lastGenerationResult.timestamp) }}</p>
                <p class="text-sm">Heure</p>
              </div>
            </div>
            
            <div class="col-12 md:col-3">
              <div class="text-center p-3 border-round bg-gray-50 text-gray-700">
                <i class="pi pi-calendar text-4xl"></i>
                <p class="font-bold text-lg mt-2">{{ formatDate(lastGenerationResult.timestamp) }}</p>
                <p class="text-sm">Date</p>
              </div>
            </div>
          </div>
          
          <!-- Erreurs si présentes -->
          <div *ngIf="lastGenerationResult.errors && lastGenerationResult.errors.length > 0" class="mt-3">
            <h6 class="text-red-600 mb-2">Erreurs rencontrées :</h6>
            <ul class="list-disc list-inside text-red-600">
              <li *ngFor="let error of lastGenerationResult.errors">{{ error }}</li>
            </ul>
          </div>
        </p-card>
      </div>

      <!-- Historique des générations -->
      <div class="col-12">
        <p-card header="Historique des Générations">
          <div class="text-center text-gray-500 py-4">
            <i class="pi pi-history text-4xl mb-3"></i>
            <p>L'historique des générations sera disponible bientôt</p>
          </div>
        </p-card>
      </div>

    </div>
  `,
  styles: [`
    .border-round {
      border-radius: 8px;
    }
  `]
})
export class MonthlyGenerationStatsComponent implements OnInit, OnDestroy {
  config: any = {};
  lastGenerationResult: GenerationResult | null = null;
  testing = false;
  
  private destroy$ = new Subject<void>();

  constructor(
    private monthlyGenerationService: MonthlyBillGenerationService
  ) {}

  ngOnInit(): void {
    this.subscribeToConfig();
    this.subscribeToGenerationResults();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private subscribeToConfig(): void {
    this.monthlyGenerationService.config$
      .pipe(takeUntil(this.destroy$))
      .subscribe(config => {
        this.config = { ...config };
      });
  }

  private subscribeToGenerationResults(): void {
    this.monthlyGenerationService.generationResult$
      .pipe(takeUntil(this.destroy$))
      .subscribe(result => {
        if (result) {
          this.lastGenerationResult = result;
        }
      });
  }

  testGeneration(): void {
    this.testing = true;
    this.monthlyGenerationService.forceGeneration().subscribe({
      next: (result) => {
        this.testing = false;
        this.lastGenerationResult = result;
      },
      error: (error) => {
        this.testing = false;
        console.error('Erreur lors du test:', error);
      }
    });
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });
  }

  formatTime(date: Date): string {
    return new Date(date).toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
