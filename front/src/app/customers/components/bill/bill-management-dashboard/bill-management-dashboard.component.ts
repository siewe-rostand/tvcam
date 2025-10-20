import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

// PrimeNG Components
import { TabViewModule } from 'primeng/tabview';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';

// Components
import { EnhancedBillGenerationComponent } from '../enhanced-bill-generation.component';
import { MonthlyGenerationStatsComponent } from '../monthly-generation-stats/monthly-generation-stats.component';
import { MonthlyGenerationConfigComponent } from '../monthly-generation-config/monthly-generation-config.component';
import { NavbarComponent } from '../../../../_shared/components/navbar/navbar.component';


@Component({
  selector: 'app-bill-management-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    TabViewModule,
    CardModule,
    ButtonModule,
    NavbarComponent,
    EnhancedBillGenerationComponent,
    MonthlyGenerationStatsComponent,
    MonthlyGenerationConfigComponent
  ],
  template: `

    <div class="container mx-auto p-4">
      <p-card header="Gestion des Factures TV CAM" styleClass="mb-4">
        <div class="text-center mb-4">
          <h2 class="text-2xl font-bold text-blue-600 mb-2">Système de Génération de Factures</h2>
          <p class="text-gray-600">
            Gérez la génération automatique et manuelle des factures pour tous vos clients
          </p>
        </div>

        <p-tabView>

          <!-- Onglet Génération Manuelle -->
          <p-tabPanel header="Génération Manuelle" leftIcon="pi pi-file-plus">
            <app-enhanced-bill-generation></app-enhanced-bill-generation>
          </p-tabPanel>

          <!-- Onglet Statistiques -->
          <p-tabPanel header="Statistiques" leftIcon="pi pi-chart-bar">
            <app-monthly-generation-stats></app-monthly-generation-stats>
          </p-tabPanel>

          <!-- Onglet Configuration -->
          <p-tabPanel header="Configuration" leftIcon="pi pi-cog">
            <app-monthly-generation-config></app-monthly-generation-config>
          </p-tabPanel>

        </p-tabView>
      </p-card>

      <!-- Informations sur le système -->
      <p-card header="À propos du Système" styleClass="mt-4">
        <div class="grid">
          <div class="col-12 md:col-4">
            <div class="text-center p-3">
              <i class="pi pi-calendar text-4xl text-blue-500 mb-3"></i>
              <h4 class="font-bold mb-2">Génération Automatique</h4>
              <p class="text-gray-600 text-sm">
                Les factures sont générées automatiquement le 1er de chaque mois selon votre configuration
              </p>
            </div>
          </div>

          <div class="col-12 md:col-4">
            <div class="text-center p-3">
              <i class="pi pi-file-edit text-4xl text-green-500 mb-3"></i>
              <h4 class="font-bold mb-2">Format Standardisé</h4>
              <p class="text-gray-600 text-sm">
                Toutes les factures suivent le format standard TV CAM avec toutes les informations requises
              </p>
            </div>
          </div>

          <div class="col-12 md:col-4">
            <div class="text-center p-3">
              <i class="pi pi-bell text-4xl text-orange-500 mb-3"></i>
              <h4 class="font-bold mb-2">Notifications</h4>
              <p class="text-gray-600 text-sm">
                Recevez des notifications pour chaque génération et en cas d'erreur
              </p>
            </div>
          </div>
        </div>
      </p-card>
    </div>
  `,
  styles: [`
    :host ::ng-deep .p-tabview-nav {
      background: #f8f9fa;
      border-radius: 8px 8px 0 0;
    }

    :host ::ng-deep .p-tabview-nav li .p-tabview-nav-link {
      padding: 1rem 1.5rem;
      font-weight: 500;
    }

    :host ::ng-deep .p-tabview-panels {
      background: white;
      border-radius: 0 0 8px 8px;
      border: 1px solid #dee2e6;
      border-top: none;
    }

    :host ::ng-deep .p-tabview-panel {
      padding: 1.5rem;
    }
  `]
})
export class BillManagementDashboardComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
