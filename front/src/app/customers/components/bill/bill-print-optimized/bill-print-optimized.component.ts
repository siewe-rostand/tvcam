import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BillModel } from '../../../model/bill.model';
import { BillUtils } from '../../../../_shared/utils/bill.utils';

@Component({
  selector: 'app-bill-print-optimized',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="print-controls">
      <button (click)="print()" class="print-button">
        <i class="pi pi-print"></i> Imprimer les Factures (2 par page)
      </button>
      <div class="bill-count">{{ bills.length }} facture(s) sélectionnée(s)</div>
      <div class="page-count">{{ totalPages }} page(s) à imprimer</div>
    </div>

    <div class="print-container">
      <div 
        *ngFor="let page of pages; let pageIndex = index" 
        class="page-container"
        [class.page-break]="pageIndex > 0">
        
        <div 
          *ngFor="let bill of page; let billIndex = index" 
          class="bill-container"
          [class.bill-left]="billIndex === 0"
          [class.bill-right]="billIndex === 1">
          
          <!-- En-tête avec logo de référence -->
          <div class="bill-header">
            <div class="company-logo">
              <div class="antenna-icon">📡</div>
              <div class="company-name">TV CAM</div>
            </div>
            <div class="bill-title">FACTURE / BILL</div>
            <div class="billing-period">
              Mois de {{ getMonthLabel(bill.month) }} {{ bill.year }}
            </div>
          </div>

          <!-- Informations de contact et destinataire -->
          <div class="contact-info">
            <div class="company-details">
              <p><strong>{{ bill.zoneName || 'Makèpe Missoke' }}</strong></p>
              <p>Tél: {{ bill.companyPhone || '6 74 38 17 44 / 6 96 39 53 30' }}</p>
              <p>Responsable de zone: {{ bill.responsibleName || 'M. Jackson' }}</p>
              <p>Recouvreur: {{ bill.collectorName || 'M. Anderson' }}</p>
              <p>{{ bill.collectorPhone || '6 75 16 86 97' }}</p>
            </div>

            <div class="customer-details">
              <p>
                Destinataire: M/Mme. <strong>{{ bill.customerName }}</strong>
              </p>
              <p>Zone: {{ bill.zoneName || '______________________' }}</p>
              <p>Date de dépôt le : {{ getDepositDate(bill) }}</p>
              <p>
                Date limite de paiement: Le
                <strong>{{ getDeadlineDate(bill) }}</strong>
              </p>
            </div>
          </div>

          <!-- Tableau des montants -->
          <table class="bill-table">
            <tr>
              <th>Description</th>
              <th>Montant (FCFA)</th>
            </tr>
            <tr>
              <td>Montant mensuel</td>
              <td class="amount">{{ formatAmount(bill.monthlyPayment) }}</td>
            </tr>
            <tr>
              <td>Arriérés</td>
              <td class="amount">{{ formatAmount(bill.debt) }}</td>
            </tr>
            <tr>
              <td>Pénalités</td>
              <td class="amount">{{ formatAmount(bill.penalties) }}</td>
            </tr>
            <tr class="total-row">
              <td><strong>Net à payer</strong></td>
              <td class="amount total-amount">
                <strong>{{ formatAmount(bill.netToPay) }}</strong>
              </td>
            </tr>
            <tr>
              <td>Observations</td>
              <td class="observation">{{ bill.observation || "" }}</td>
            </tr>
          </table>

          <!-- Notes et instructions -->
          <div class="notes">
            <p class="payment-info">
              Vous pouvez désormais payer votre consommation dans notre agence qui a
              été rapprochée de vous et se situe en face du Centre Médical la LIFE non
              loin de l'école la Solidarité de Makèpe Missoke.
            </p>

            <div class="important-notes">
              <p><strong>NB:</strong></p>
              <ol>
                <li>Exigez toujours vos reçus après chaque paiement.</li>
                <li>
                  Le câbleur ne vous appellera jamais avec un autre numéro de
                  téléphone en dehors de ceux inscrits sur la facture: Attention aux
                  arnaqueurs !
                </li>
              </ol>
            </div>

            <div class="signature-section">
              <p class="signature">Le Responsable</p>
            </div>
          </div>

          <!-- Référence bill.jpeg - format de présentation -->
          <div class="bill-reference">
            <small>{{ getBillReference(bill) }} - {{ bill.month }}/{{ bill.year }}</small>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrl: './bill-print-optimized.component.css'
})
export class BillPrintOptimizedComponent implements OnInit {
  bills: BillModel[] = [];
  pages: BillModel[][] = [];
  totalPages = 0;

  @ViewChild('printSection') printSection!: ElementRef;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadBillsForPrint();
  }

  private loadBillsForPrint(): void {
    // Récupérer les factures sélectionnées depuis le service
    // Pour l'instant, on utilise des données de test
    this.bills = [
      {
        id: 1,
        customerName: 'Client Test 1',
        month: 'july',
        year: '2024',
        monthlyPayment: 2000,
        netToPay: 2000,
        debt: 0,
        penalties: 0,
        observation: 'Test facture 1'
      },
      {
        id: 2,
        customerName: 'Client Test 2',
        month: 'july',
        year: '2024',
        monthlyPayment: 2000,
        netToPay: 2000,
        debt: 0,
        penalties: 0,
        observation: 'Test facture 2'
      }
    ];
    
    this.organizeBillsInPages();
  }

  private organizeBillsInPages(): void {
    this.pages = [];
    
    for (let i = 0; i < this.bills.length; i += 2) {
      const page: BillModel[] = [];
      page.push(this.bills[i]);
      
      if (i + 1 < this.bills.length) {
        page.push(this.bills[i + 1]);
      }
      
      this.pages.push(page);
    }
    
    this.totalPages = this.pages.length;
  }

  print(): void {
    window.print();
  }

  /**
   * Format month value to display label using BillUtils
   */
  getMonthLabel(monthValue?: string): string {
    return BillUtils.getMonthLabelFr(monthValue);
  }

  /**
   * Format date for display using BillUtils
   */
  formatDate(dateValue?: string): string {
    return BillUtils.formatDateFr(dateValue);
  }

  /**
   * Format amount with proper FCFA formatting using BillUtils
   */
  formatAmount(amount?: number): string {
    return BillUtils.formatAmountFCFA(amount);
  }

  /**
   * Generate bill reference number
   */
  getBillReference(bill: BillModel): string {
    return BillUtils.generateBillReference(bill.id, bill.month, bill.year);
  }

  /**
   * Get default deposit date if not provided
   */
  getDepositDate(bill: BillModel): string {
    return bill.depositDate ? this.formatDate(bill.depositDate) : BillUtils.getDefaultDepositDate();
  }

  /**
   * Get deadline date, with fallback to default
   */
  getDeadlineDate(bill: BillModel): string {
    return bill.deadLine ? this.formatDate(bill.deadLine) : BillUtils.getDefaultDeadlineDate();
  }
}
