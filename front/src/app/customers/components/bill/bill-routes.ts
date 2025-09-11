import { Routes } from '@angular/router';

export const billRoutes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./bill-management-dashboard/bill-management-dashboard.component')
      .then(m => m.BillManagementDashboardComponent)
  },
  {
    path: 'generate',
    loadComponent: () => import('./enhanced-bill-generation.component')
      .then(m => m.EnhancedBillGenerationComponent)
  },
  {
    path: 'print',
    loadComponent: () => import('./bill-print/bill-print.component')
      .then(m => m.BillPrintComponent)
  },
  {
    path: 'config',
    loadComponent: () => import('./monthly-generation-config/monthly-generation-config.component')
      .then(m => m.MonthlyGenerationConfigComponent)
  },
  {
    path: 'stats',
    loadComponent: () => import('./monthly-generation-stats/monthly-generation-stats.component')
      .then(m => m.MonthlyGenerationStatsComponent)
  }
];
