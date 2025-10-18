// Exemple d'utilisation du composant ConfirmDialog

// 1. Import du composant et du service

// 2. Dans votre composant TypeScript
export class ExampleComponent {
  constructor(private confirmDialogService: ConfirmDialogService) {
  }

  // Méthode 1 : Utilisation avec le service (recommandée)
  showGenerateBill() {
    this.confirmDialogService.confirmGenerateBills(
      () => {
        // Action à exécuter si l'utilisateur confirme
        this.generateBills();
      },
      () => {
        // Action optionnelle si l'utilisateur annule
        console.log('Génération annulée');
      }
    );
  }

  // Méthode 2 : Dialog de suppression
  deleteCustomer(customer: any) {
    this.confirmDialogService.confirmDelete(
      `le client "${customer.name}"`,
      () => {
        // Logique de suppression
        this.performDelete(customer);
      }
    );
  }

  // Méthode 3 : Dialog personnalisé
  customConfirm() {
    this.confirmDialogService.confirm(
      {
        header: 'Action personnalisée',
        message: 'Voulez-vous effectuer cette action ?',
        icon: 'pi pi-star',
        acceptLabel: 'Confirmer',
        rejectLabel: 'Annuler',
        acceptButtonClass: 'confirm-button p-button-success',
        rejectButtonClass: 'confirm-button p-button-outlined'
      },
      () => {
        // Action de confirmation
        console.log('Action confirmée');
      },
      () => {
        // Action d'annulation
        console.log('Action annulée');
      }
    );
  }

  private generateBills() {
    // Votre logique de génération de factures
    console.log('Génération des factures...');
  }

  private performDelete(customer: any) {
    // Votre logique de suppression
    console.log('Suppression du client:', customer);
  }
}

// 3. Dans votre template HTML
/*
<!-- Ajoutez simplement le composant dans votre template -->
<app-confirm-dialog></app-confirm-dialog>

<!-- Vos boutons qui déclenchent les confirmations -->
<p-button
  label="Générer Factures"
  (onClick)="showGenerateBill()">
</p-button>

<p-button
  label="Supprimer"
  (onClick)="deleteCustomer(customer)">
</p-button>
*/

// 4. Dans votre module ou composant standalone
/*
import { ConfirmDialogComponent } from '../_shared/components/confirm-dialog';
import { ConfirmDialogService } from '../_shared/services/confirm-dialog.service';

@Component({
  // ...
  imports: [ConfirmDialogComponent, ...],
  providers: [ConfirmDialogService]
})
*/
