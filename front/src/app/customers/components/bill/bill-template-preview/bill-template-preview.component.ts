import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';

@Component({
    selector: 'app-bill-template-preview',
    standalone: true,
    imports: [CommonModule, DialogModule, ButtonModule],
    template: `
    <p-button 
      label="Voir Format de Facture" 
      icon="pi pi-eye" 
      (onClick)="showPreview = true"
      severity="info"
      size="small">
    </p-button>

    <p-dialog 
      header="Format de Facture de Référence" 
      [(visible)]="showPreview" 
      [modal]="true" 
      [draggable]="false"
      [resizable]="false"
      [style]="{width: '70vw', maxWidth: '800px'}"
      styleClass="template-preview-dialog">
      
      <div class="template-preview-content">
        <div class="preview-image-container">
          <img 
            src="/bill.jpeg" 
            alt="Format de facture de référence" 
            class="reference-bill-image"
            (error)="onImageError($event)">
        </div>
        
        <div class="preview-description">
          <h4>Format de Facture TV CAM</h4>
          <p>
            Cette image montre le format standard des factures TV CAM. 
            Toutes les factures générées suivront ce modèle avec :
          </p>
          <ul>
            <li>En-tête avec logo et nom de l'entreprise</li>
            <li>Informations de contact et responsables</li>
            <li>Détails du client et période de facturation</li>
            <li>Tableau des montants (abonnement, arriérés, pénalités)</li>
            <li>Instructions de paiement et notes importantes</li>
          </ul>
          
          <div class="print-info">
            <p><strong>Impression :</strong> Deux factures par page A4</p>
            <p><strong>Génération :</strong> Une seule fois par mois par client</p>
          </div>
        </div>
      </div>
      
      <ng-template pTemplate="footer">
        <p-button 
          label="Fermer" 
          icon="pi pi-times" 
          (onClick)="showPreview = false"
          severity="secondary">
        </p-button>
      </ng-template>
    </p-dialog>
  `,
    styles: [`
    .template-preview-content {
      display: flex;
      gap: 20px;
      align-items: flex-start;
    }

    .preview-image-container {
      flex: 1;
      text-align: center;
    }

    .reference-bill-image {
      max-width: 100%;
      height: auto;
      border: 2px solid #ddd;
      border-radius: 8px;
      box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    }

    .preview-description {
      flex: 1;
      padding-left: 20px;
    }

    .preview-description h4 {
      color: #2c5aa0;
      margin-bottom: 15px;
      border-bottom: 2px solid #e9ecef;
      padding-bottom: 8px;
    }

    .preview-description ul {
      margin: 15px 0;
      padding-left: 20px;
    }

    .preview-description li {
      margin-bottom: 8px;
      line-height: 1.4;
    }

    .print-info {
      background: #f8f9fa;
      padding: 15px;
      border-radius: 6px;
      border-left: 4px solid #007bff;
      margin-top: 20px;
    }

    .print-info p {
      margin: 5px 0;
      font-size: 0.9em;
    }

    .error-message {
      color: #dc3545;
      font-style: italic;
      text-align: center;
      padding: 20px;
    }

    @media (max-width: 768px) {
      .template-preview-content {
        flex-direction: column;
      }
      
      .preview-description {
        padding-left: 0;
        padding-top: 20px;
      }
    }

    :host ::ng-deep .template-preview-dialog .p-dialog-content {
      padding: 20px;
    }
  `]
})
export class BillTemplatePreviewComponent {
    showPreview = false;

    onImageError(event: any): void {
        event.target.style.display = 'none';
        event.target.parentElement.innerHTML = `
      <div class="error-message">
        <i class="pi pi-exclamation-triangle" style="font-size: 2rem; margin-bottom: 10px;"></i>
        <p>Image de référence non disponible</p>
        <p>Le fichier bill.jpeg doit être placé dans le dossier public</p>
      </div>
    `;
    }
}
