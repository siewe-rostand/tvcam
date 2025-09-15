import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';

interface ExistingBillInfo {
    customerId: number;
    customerName: string;
    billId: number;
    month: string;
    year: string;
}

@Component({
    selector: 'app-duplicate-bill-dialog',
    standalone: true,
    imports: [CommonModule, DialogModule, ButtonModule, CardModule],
    template: `
    <p-dialog 
      [(visible)]="visible" 
      [modal]="true" 
      [closable]="true"
      [draggable]="false"
      [resizable]="false"
      styleClass="duplicate-bill-dialog"
      [style]="{ width: '50vw', minWidth: '400px' }"
      header="Factures existantes détectées">
      
      <div class="dialog-content">
        <div class="mb-4">
          <i class="pi pi-exclamation-triangle text-orange-500 text-2xl mr-2"></i>
          <span class="text-lg font-semibold">Des factures ont déjà été générées</span>
        </div>

        <div class="mb-4">
          <p class="text-gray-700 mb-3">
            Les clients suivants ont déjà des factures pour la période sélectionnée :
          </p>
          
          <div class="existing-bills-list max-h-60 overflow-y-auto">
            <div *ngFor="let bill of existingBills" class="flex justify-content-between align-items-center p-2 border-bottom-1 border-gray-200">
              <div>
                <div class="font-medium">{{ bill.customerName }}</div>
                <div class="text-sm text-gray-500">
                  {{ formatMonth(bill.month) }} {{ bill.year }} - ID: {{ bill.billId }}
                </div>
              </div>
              <i class="pi pi-file text-blue-500"></i>
            </div>
          </div>
        </div>

        <div class="mb-4">
          <p-card header="Options disponibles" [style]="{ backgroundColor: '#f8f9fa' }">
            <div class="grid">
              <div class="col-6">
                <div class="option-item">
                  <i class="pi pi-refresh text-orange-500 text-xl mb-2"></i>
                  <h6 class="font-semibold mb-1">Mettre à jour</h6>
                  <p class="text-sm text-gray-600 m-0">
                    Recalcule et met à jour les factures existantes avec les nouvelles valeurs
                  </p>
                </div>
              </div>
              <div class="col-6">
                <div class="option-item">
                  <i class="pi pi-times text-gray-500 text-xl mb-2"></i>
                  <h6 class="font-semibold mb-1">Annuler</h6>
                  <p class="text-sm text-gray-600 m-0">
                    Conserve les factures existantes sans modification
                  </p>
                </div>
              </div>
            </div>
          </p-card>
        </div>

        <div class="text-center">
          <p class="font-medium text-gray-800">
            Que souhaitez-vous faire ?
          </p>
        </div>
      </div>

      <ng-template pTemplate="footer">
        <div class="flex justify-content-end gap-2">
          <p-button 
            label="Annuler" 
            icon="pi pi-times" 
            (onClick)="onCancel()"
            [outlined]="true"
            severity="secondary">
          </p-button>
          <p-button 
            label="Mettre à jour" 
            icon="pi pi-refresh" 
            (onClick)="onUpdate()"
            severity="warning">
          </p-button>
        </div>
      </ng-template>
    </p-dialog>
  `,
    styles: [`
    .dialog-content {
      padding: 1rem 0;
    }
    
    .existing-bills-list {
      background: #fafafa;
      border: 1px solid #e0e0e0;
      border-radius: 6px;
      padding: 0.5rem;
    }
    
    .option-item {
      text-align: center;
      padding: 1rem;
    }
    
    .duplicate-bill-dialog .p-dialog-header {
      background: linear-gradient(90deg, #ff9800 0%, #ff5722 100%);
      color: white;
    }
    
    .duplicate-bill-dialog .p-dialog-header .p-dialog-title {
      font-weight: 600;
    }
  `]
})
export class DuplicateBillDialogComponent {
    @Input() visible: boolean = false;
    @Input() existingBills: ExistingBillInfo[] = [];

    @Output() visibleChange = new EventEmitter<boolean>();
    @Output() update = new EventEmitter<void>();
    @Output() cancel = new EventEmitter<void>();

    onUpdate(): void {
        this.visible = false;
        this.visibleChange.emit(false);
        this.update.emit();
    }

    onCancel(): void {
        this.visible = false;
        this.visibleChange.emit(false);
        this.cancel.emit();
    }

    formatMonth(month: string): string {
        const months: { [key: string]: string } = {
            'january': 'Janvier',
            'february': 'Février',
            'march': 'Mars',
            'april': 'Avril',
            'may': 'Mai',
            'june': 'Juin',
            'july': 'Juillet',
            'august': 'Août',
            'september': 'Septembre',
            'october': 'Octobre',
            'november': 'Novembre',
            'december': 'Décembre'
        };
        return months[month?.toLowerCase()] || month;
    }
}
