import { Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';

export interface NotificationOptions {
  severity?: 'success' | 'info' | 'warn' | 'error';
  summary?: string;
  detail?: string;
  life?: number;
  sticky?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private messageService: MessageService) { }

  /**
   * Affiche un message de succès
   */
  showSuccess(message: string, title: string = 'Succès', life: number = 4000): void {
    this.messageService.add({
      severity: 'success',
      summary: title,
      detail: message,
      life: life
    });
  }

  /**
   * Affiche un message d'erreur
   */
  showError(message: string, title: string = 'Erreur', sticky: boolean = false): void {
    this.messageService.add({
      severity: 'error',
      summary: title,
      detail: message,
      sticky: sticky,
      life: sticky ? undefined : 6000
    });
  }

  /**
   * Affiche un message d'avertissement
   */
  showWarning(message: string, title: string = 'Attention', life: number = 4000): void {
    this.messageService.add({
      severity: 'warn',
      summary: title,
      detail: message,
      life: life
    });
  }

  /**
   * Affiche un message d'information
   */
  showInfo(message: string, title: string = 'Information', life: number = 4000): void {
    this.messageService.add({
      severity: 'info',
      summary: title,
      detail: message,
      life: life
    });
  }

  /**
   * Affiche une notification personnalisée
   */
  showCustom(options: NotificationOptions): void {
    this.messageService.add({
      severity: options.severity || 'info',
      summary: options.summary || 'Notification',
      detail: options.detail || '',
      life: options.life || 4000,
      sticky: options.sticky || false
    });
  }

  /**
   * Affiche un message de validation d'opération
   */
  showOperationSuccess(operation: string, entityName?: string): void {
    const detail = entityName
      ? `${entityName} ${operation} avec succès`
      : `Opération ${operation} réussie`;

    this.showSuccess(detail, 'Opération réussie');
  }

  /**
   * Affiche un message d'erreur de validation
   */
  showValidationError(field: string, message?: string): void {
    const detail = message || `Le champ ${field} est invalide`;
    this.showError(detail, 'Erreur de validation');
  }

  /**
   * Affiche un message pour une opération de sauvegarde
   */
  showSaveSuccess(entityName: string = 'Élément'): void {
    this.showSuccess(`${entityName} sauvegardé avec succès`, 'Sauvegarde réussie');
  }

  /**
   * Affiche un message pour une opération de suppression
   */
  showDeleteSuccess(entityName: string = 'Élément'): void {
    this.showSuccess(`${entityName} supprimé avec succès`, 'Suppression réussie');
  }

  /**
   * Affiche un message pour une opération de mise à jour
   */
  showUpdateSuccess(entityName: string = 'Élément'): void {
    this.showSuccess(`${entityName} mis à jour avec succès`, 'Mise à jour réussie');
  }

  /**
   * Affiche un message d'erreur réseau
   */
  showNetworkError(): void {
    this.showError(
      'Problème de connexion réseau. Veuillez vérifier votre connexion internet.',
      'Erreur de connexion',
      true
    );
  }

  /**
   * Affiche un message d'erreur serveur
   */
  showServerError(): void {
    this.showError(
      'Une erreur serveur est survenue. Veuillez réessayer plus tard ou contacter le support.',
      'Erreur serveur'
    );
  }

  /**
   * Affiche un message d'autorisation insuffisante
   */
  showUnauthorizedError(): void {
    this.showError(
      'Vous n\'avez pas les autorisations nécessaires pour effectuer cette action.',
      'Accès refusé'
    );
  }

  /**
   * Affiche un message pour une ressource introuvable
   */
  showNotFoundError(resource: string = 'ressource'): void {
    this.showError(
      `La ${resource} demandée est introuvable.`,
      'Ressource introuvable'
    );
  }

  /**
   * Affiche un message de confirmation d'action
   */
  showActionConfirmation(action: string): void {
    this.showInfo(
      `Action "${action}" en cours de traitement...`,
      'Traitement en cours'
    );
  }

  /**
   * Affiche un message de bienvenue
   */
  showWelcome(username?: string): void {
    const message = username
      ? `Bienvenue ${username} !`
      : 'Bienvenue sur TV CAM !';

    this.showSuccess(message, 'Connexion réussie');
  }

  /**
   * Affiche un message de déconnexion
   */
  showLogout(): void {
    this.showInfo('Vous avez été déconnecté avec succès', 'Au revoir');
  }

  /**
   * Efface tous les messages
   */
  clear(): void {
    this.messageService.clear();
  }

  /**
   * Efface un message spécifique par clé
   */
  clearByKey(key: string): void {
    this.messageService.clear(key);
  }
}
