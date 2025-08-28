import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { AuthDebugService, AuthDebugInfo } from '../../_shared/services/auth-debug.service';

@Component({
    selector: 'app-auth-debug',
    standalone: true,
    imports: [CommonModule, CardModule, ButtonModule, TableModule, TagModule],
    template: `
    <div class="p-4">
      <div class="card">
        <h3>🔍 Debug d'Authentification</h3>
        
        <div class="mb-3">
          <button pButton 
                  type="button" 
                  label="Actualiser les infos" 
                  icon="pi pi-refresh"
                  class="p-button-sm mr-2"
                  (click)="refreshDebugInfo()">
          </button>
          
          <button pButton 
                  type="button" 
                  label="Log dans Console" 
                  icon="pi pi-eye"
                  class="p-button-sm p-button-secondary mr-2"
                  (click)="logToConsole()">
          </button>
          
          <button pButton 
                  type="button" 
                  label="Vérifier Token" 
                  icon="pi pi-search"
                  class="p-button-sm p-button-info mr-2"
                  (click)="checkToken()">
          </button>
          
          <button pButton 
                  type="button" 
                  label="Reset Auth" 
                  icon="pi pi-trash"
                  class="p-button-sm p-button-danger mr-2"
                  (click)="resetAuth()">
          </button>
          
          <button pButton 
                  type="button" 
                  label="Token Test (1h)" 
                  icon="pi pi-plus"
                  class="p-button-sm p-button-success"
                  (click)="createTestToken()">
          </button>
        </div>

        <div class="grid">
          <div class="col-12 md:col-6">
            <p-table [value]="debugData" [responsive]="true">
              <ng-template pTemplate="header">
                <tr>
                  <th>Propriété</th>
                  <th>Valeur</th>
                </tr>
              </ng-template>
              <ng-template pTemplate="body" let-item>
                <tr>
                  <td><strong>{{ item.label }}</strong></td>
                  <td>
                    <p-tag 
                      [value]="item.value" 
                      [severity]="item.severity"
                      *ngIf="item.isTag; else textValue">
                    </p-tag>
                    <ng-template #textValue>
                      <span [class]="item.class">{{ item.value }}</span>
                    </ng-template>
                  </td>
                </tr>
              </ng-template>
            </p-table>
          </div>
          
          <div class="col-12 md:col-6" *ngIf="debugInfo?.user">
            <h4>Informations Utilisateur</h4>
            <p><strong>Téléphone:</strong> {{ debugInfo?.user?.telephone || 'N/A' }}</p>
            <p><strong>Prénom:</strong> {{ debugInfo?.user?.firstname || 'N/A' }}</p>
            <p><strong>Nom:</strong> {{ debugInfo?.user?.lastname || 'N/A' }}</p>
            <p><strong>Email:</strong> {{ debugInfo?.user?.email || 'N/A' }}</p>
          </div>
        </div>

        <div class="mt-3" *ngIf="debugInfo?.tokenExpiry">
          <h4>Détails du Token</h4>
          <p><strong>Expire le:</strong> {{ debugInfo?.tokenExpiry | date:'dd/MM/yyyy HH:mm:ss' }}</p>
          <p><strong>Minutes restantes:</strong> 
            <span [class]="getExpiryClass()">{{ debugInfo?.minutesUntilExpiry }}</span>
          </p>
        </div>

        <div class="mt-3">
          <small class="text-muted">
            Dernière mise à jour: {{ debugInfo?.timestamp | date:'HH:mm:ss' }}
          </small>
        </div>
      </div>
    </div>
  `,
    styles: [`
    .text-success { color: #28a745; }
    .text-danger { color: #dc3545; }
    .text-warning { color: #ffc107; }
    .text-muted { color: #6c757d; }
  `]
})
export class AuthDebugComponent implements OnInit {

    debugInfo: AuthDebugInfo | null = null;
    debugData: any[] = [];

    constructor(private authDebugService: AuthDebugService) { }

    ngOnInit() {
        this.refreshDebugInfo();

        // Auto-refresh every 30 seconds
        setInterval(() => {
            this.refreshDebugInfo();
        }, 30000);
    }

    refreshDebugInfo() {
        this.debugInfo = this.authDebugService.getCurrentDebugInfo();
        this.updateDebugData();
    }

    private updateDebugData() {
        if (!this.debugInfo) return;

        this.debugData = [
            {
                label: 'Token Présent',
                value: this.debugInfo.hasToken ? 'OUI' : 'NON',
                severity: this.debugInfo.hasToken ? 'success' : 'danger',
                isTag: true
            },
            {
                label: 'Token Valide',
                value: this.debugInfo.tokenValid ? 'OUI' : 'NON',
                severity: this.debugInfo.tokenValid ? 'success' : 'danger',
                isTag: true
            },
            {
                label: 'Authentifié',
                value: this.debugInfo.isAuthenticated ? 'OUI' : 'NON',
                severity: this.debugInfo.isAuthenticated ? 'success' : 'danger',
                isTag: true
            },
            {
                label: 'Utilisateur',
                value: this.debugInfo.user ? 'Présent' : 'Absent',
                severity: this.debugInfo.user ? 'success' : 'warning',
                isTag: true
            }
        ];
    }

    getExpiryClass(): string {
        if (!this.debugInfo?.minutesUntilExpiry) return 'text-muted';

        const minutes = this.debugInfo.minutesUntilExpiry;
        if (minutes < 0) return 'text-danger';
        if (minutes < 5) return 'text-warning';
        return 'text-success';
    }

    logToConsole() {
        this.authDebugService.logCurrentState();
    }

    checkToken() {
        this.authDebugService.checkTokenStructure();
    }

    resetAuth() {
        if (confirm('Êtes-vous sûr de vouloir réinitialiser l\'authentification ?')) {
            this.authDebugService.resetAuth();
            setTimeout(() => this.refreshDebugInfo(), 100);
        }
    }

    createTestToken() {
        this.authDebugService.createTestToken(60);
        setTimeout(() => this.refreshDebugInfo(), 100);
    }
}
