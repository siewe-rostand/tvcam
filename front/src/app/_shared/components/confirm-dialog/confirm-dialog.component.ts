import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ButtonModule } from 'primeng/button';
import { ConfirmationService } from 'primeng/api';

export interface ConfirmDialogConfig {
  header?: string;
  message?: string;
  icon?: string;
  acceptLabel?: string;
  rejectLabel?: string;
  acceptButtonClass?: string;
  rejectButtonClass?: string;
}

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule, ConfirmDialogModule, ButtonModule],
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.css'],
  providers: [ConfirmationService]
})
export class ConfirmDialogComponent {

  constructor(private confirmationService: ConfirmationService) {}

  /**
   * Affiche le dialog de confirmation
   * @param config Configuration du dialog
   * @param onAccept Callback exécuté lors de l'acceptation
   * @param onReject Callback exécuté lors du rejet (optionnel)
   */
  show(
    config: ConfirmDialogConfig,
    onAccept: () => void,
    onReject?: () => void
  ): void {
    this.confirmationService.confirm({
      header: config.header || 'Confirmation',
      message: config.message || 'Êtes-vous sûr de vouloir continuer ?',
      icon: config.icon || 'pi pi-question',
      acceptLabel: config.acceptLabel || 'OUI',
      rejectLabel: config.rejectLabel || 'NON',
      acceptButtonStyleClass: config.acceptButtonClass || 'confirm-button',
      rejectButtonStyleClass: config.rejectButtonClass || 'confirm-button p-button-outlined',
      accept: () => {
        onAccept();
      },
      reject: () => {
        if (onReject) {
          onReject();
        }
      }
    });
  }

  /**
   * Méthode de convenance pour afficher un dialog de suppression
   */
  showDelete(
    itemName: string,
    onAccept: () => void,
    onReject?: () => void
  ): void {
    this.show(
      {
        header: 'Confirmer la suppression',
        message: `Êtes-vous sûr de vouloir supprimer ${itemName} ? Cette action est irréversible.`,
        icon: 'pi pi-trash',
        acceptLabel: 'Supprimer',
        rejectLabel: 'Annuler',
        acceptButtonClass: 'confirm-button p-button-danger',
        rejectButtonClass: 'confirm-button p-button-outlined'
      },
      onAccept,
      onReject
    );
  }

  /**
   * Méthode de convenance pour afficher un dialog de sauvegarde
   */
  showSave(
    message: string,
    onAccept: () => void,
    onReject?: () => void
  ): void {
    this.show(
      {
        header: 'Confirmer la sauvegarde',
        message: message,
        icon: 'pi pi-save',
        acceptLabel: 'Sauvegarder',
        rejectLabel: 'Annuler',
        acceptButtonClass: 'confirm-button p-button-success',
        rejectButtonClass: 'confirm-button p-button-outlined'
      },
      onAccept,
      onReject
    );
  }
}
