import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BillManagementService } from './bill-management.service';

export interface DuplicateCheckResult {
    success: boolean;
    action: 'generated' | 'updated' | 'cancelled' | 'conflict';
    data?: any;
    message: string;
}

@Injectable()
export class BillDuplicateCheckService {

    constructor(
        private billManagementService: BillManagementService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService
    ) { }    /**
     * Handles the complete bill generation workflow with duplicate check
     * @param customerIds Array of customer IDs
     * @param shouldGenerate Force generation flag
     * @returns Observable<DuplicateCheckResult>
     */
    handleBillGeneration(customerIds: number[], shouldGenerate: boolean = false): Observable<DuplicateCheckResult> {
        // First attempt - check for duplicates
        return this.billManagementService.generateBillsWithDuplicateCheck(customerIds, shouldGenerate).pipe(
            switchMap(response => {
                if (response.hasExistingBills && response.success) {
                    // Existing bills found - show conflict dialog
                    return this.showConflictDialog(response).pipe(
                        switchMap(userChoice => {
                            if (userChoice === 'update') {
                                // User chose to update - make second call with forceUpdate = true
                                return this.billManagementService.generateBillsWithDuplicateCheck(
                                    customerIds, shouldGenerate, true
                                ).pipe(
                                    map(updateResponse => ({
                                        success: true,
                                        action: 'updated' as const,
                                        data: updateResponse,
                                        message: `Factures mises à jour avec succès pour ${updateResponse.generatedBills.length} client(s)`
                                    }))
                                );
                            } else {
                                // User cancelled
                                return of({
                                    success: true,
                                    action: 'cancelled' as const,
                                    message: 'Génération de factures annulée par l\'utilisateur'
                                });
                            }
                        })
                    );
                } else if (response.success) {
                    // No conflicts - bills generated successfully
                    return of({
                        success: true,
                        action: 'generated' as const,
                        data: response,
                        message: `Factures générées avec succès pour ${response.generatedBills.length} client(s)`
                    });
                } else {
                    // Error occurred
                    return of({
                        success: false,
                        action: 'conflict' as const,
                        message: response.message || 'Erreur lors de la génération des factures'
                    });
                }
            })
        );
    }

    /**
     * Shows a confirmation dialog when existing bills are found
     * @param response The response containing existing bills information
     * @returns Observable<'update' | 'cancel'>
     */
    private showConflictDialog(response: any): Observable<'update' | 'cancel'> {
        return new Observable(observer => {
            try {
                const existingBillsInfo = this.formatExistingBillsMessage(response.existingBills);

                this.confirmationService.confirm({
                    header: 'Factures existantes détectées',
                    message: `
              <div class="mb-3">
                <p><strong>Des factures ont déjà été générées pour les clients suivants :</strong></p>
                ${existingBillsInfo}
              </div>
              <div class="mb-3">
                <p><strong>Options disponibles :</strong></p>
                <ul>
                  <li><strong>Mettre à jour :</strong> Recalcule et met à jour les factures existantes avec les nouvelles valeurs</li>
                  <li><strong>Annuler :</strong> Conserve les factures existantes sans modification</li>
                </ul>
              </div>
              <p><strong>Que souhaitez-vous faire ?</strong></p>
            `,
                    icon: 'pi pi-exclamation-triangle',
                    acceptLabel: 'Mettre à jour',
                    rejectLabel: 'Annuler',
                    acceptIcon: 'pi pi-check',
                    rejectIcon: 'pi pi-times',
                    acceptButtonStyleClass: 'p-button-warning',
                    rejectButtonStyleClass: 'p-button-secondary',
                    accept: () => {
                        try {
                            observer.next('update');
                            observer.complete();
                        } catch (error) {
                            observer.error(error);
                        }
                    },
                    reject: () => {
                        try {
                            observer.next('cancel');
                            observer.complete();
                        } catch (error) {
                            observer.error(error);
                        }
                    }
                });
            } catch (error) {
                observer.error(error);
            }
        });
    }

    /**
     * Formats the existing bills information for display
     * @param existingBills Array of existing bill information
     * @returns HTML string
     */
    private formatExistingBillsMessage(existingBills: any[]): string {
        if (!existingBills || existingBills.length === 0) {
            return '<p>Aucun détail disponible</p>';
        }

        return `
      <div class="existing-bills-list" style="max-height: 200px; overflow-y: auto;">
        <ul>
          ${existingBills.map(bill => `
            <li>
              <strong>${bill.customerName}</strong> - 
              ${this.capitalizeFirstLetter(bill.month)} ${bill.year}
              <small>(ID: ${bill.billId})</small>
            </li>
          `).join('')}
        </ul>
      </div>
    `;
    }

    /**
     * Capitalizes the first letter of a string
     */
    private capitalizeFirstLetter(str: string): string {
        if (!str) return '';
        return str.charAt(0).toUpperCase() + str.slice(1);
    }

    /**
     * Shows appropriate success message based on the action taken
     */
    showSuccessMessage(result: DuplicateCheckResult): void {
        switch (result.action) {
            case 'generated':
                this.messageService.add({
                    severity: 'success',
                    summary: 'Factures générées',
                    detail: result.message,
                    life: 5000
                });
                break;
            case 'updated':
                this.messageService.add({
                    severity: 'info',
                    summary: 'Factures mises à jour',
                    detail: result.message,
                    life: 5000
                });
                break;
            case 'cancelled':
                this.messageService.add({
                    severity: 'warn',
                    summary: 'Opération annulée',
                    detail: result.message,
                    life: 3000
                });
                break;
        }
    }

    /**
     * Shows error message
     */
    showErrorMessage(message: string): void {
        this.messageService.add({
            severity: 'error',
            summary: 'Erreur',
            detail: message,
            life: 8000
        });
    }
}
