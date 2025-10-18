import { Injectable } from '@angular/core';
import { ConfirmationService } from 'primeng/api';

export interface ConfirmDialogOptions {
  header?: string;
  message?: string;
  icon?: string;
  acceptLabel?: string;
  rejectLabel?: string;
  acceptButtonClass?: string;
  rejectButtonClass?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ConfirmDialogService {

  constructor(private confirmationService: ConfirmationService) {}

  /**
   * Affiche un dialog de confirmation personnalisé
   */
  confirm(
    options: ConfirmDialogOptions,
    onAccept: () => void,
    onReject?: () => void
  ): void {
    this.confirmationService.confirm({
      header: options.header || 'Confirmation',
      message: options.message || 'Êtes-vous sûr de vouloir continuer ?',
      icon: options.icon || 'pi pi-question',
      acceptLabel: options.acceptLabel || 'OUI',
      rejectLabel: options.rejectLabel || 'NON',
      acceptButtonStyleClass: options.acceptButtonClass || 'confirm-button',
      rejectButtonStyleClass: options.rejectButtonClass || 'confirm-button p-button-outlined',
      accept: () => onAccept(),
      reject: () => onReject && onReject()
    });
  }

  /**
   * Dialog de confirmation pour suppression
   */
  confirmDelete(
    itemName: string,
    onAccept: () => void,
    onReject?: () => void
  ): void {
    this.confirm(
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
   * Dialog de confirmation pour sauvegarde
   */
  confirmSave(
    message: string,
    onAccept: () => void,
    onReject?: () => void
  ): void {
    this.confirm(
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

  /**
   * Dialog de confirmation pour génération de factures
   */
  confirmGenerateBills(
    onAccept: () => void,
    onReject?: () => void
  ): void {
    this.confirm(
      {
        header: 'Génération de factures',
        message: 'Voulez-vous générer les factures des clients sélectionnés ?',
        icon: 'pi pi-receipt',
        acceptLabel: 'Générer',
        rejectLabel: 'Annuler',
        acceptButtonClass: 'confirm-button p-button-success',
        rejectButtonClass: 'confirm-button p-button-outlined'
      },
      onAccept,
      onReject
    );
  }

  /**
   * Dialog de confirmation générique avec icône d'avertissement
   */
  confirmWarning(
    message: string,
    onAccept: () => void,
    onReject?: () => void
  ): void {
    this.confirm(
      {
        header: 'Attention',
        message: message,
        icon: 'pi pi-exclamation-triangle',
        acceptLabel: 'Continuer',
        rejectLabel: 'Annuler',
        acceptButtonClass: 'confirm-button p-button-warning',
        rejectButtonClass: 'confirm-button p-button-outlined'
      },
      onAccept,
      onReject
    );
  }
}
