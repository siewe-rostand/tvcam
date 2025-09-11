import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { BillModel } from '../model/bill.model';

export interface PrintRequest {
  billIds: number[];
}

export interface PrintResponse {
  bills: BillModel[];
  pages: BillModel[][];
  totalBills: number;
  totalPages: number;
  printFormat: string;
  printDate: string;
}

export interface PrintStatistics {
  totalRequested: number;
  totalGenerated: number;
  skipped: number;
  generationDate: string;
  shouldGenerate: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class BillPrintService {
  private selectedBillsSubject = new BehaviorSubject<BillModel[]>([]);
  public selectedBills$ = this.selectedBillsSubject.asObservable();

  private printDataSubject = new BehaviorSubject<PrintResponse | null>(null);
  public printData$ = this.printDataSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Sélectionne des factures pour l'impression
   */
  setSelectedBills(bills: BillModel[]): void {
    this.selectedBillsSubject.next(bills);
  }

  /**
   * Ajoute une facture à la sélection
   */
  addBillToSelection(bill: BillModel): void {
    const currentBills = this.selectedBillsSubject.value;
    if (!currentBills.find(b => b.id === bill.id)) {
      this.selectedBillsSubject.next([...currentBills, bill]);
    }
  }

  /**
   * Retire une facture de la sélection
   */
  removeBillFromSelection(billId: number): void {
    const currentBills = this.selectedBillsSubject.value;
    this.selectedBillsSubject.next(currentBills.filter(b => b.id !== billId));
  }

  /**
   * Vide la sélection
   */
  clearSelection(): void {
    this.selectedBillsSubject.next([]);
  }

  /**
   * Récupère les factures pour impression depuis le backend
   */
  getBillsForPrint(billIds: number[]): Observable<PrintResponse> {
    return this.http.post<any>('bills/print', billIds).pipe(
      map(response => {
        const printData: PrintResponse = {
          bills: response.data.bills || [],
          pages: response.data.pages || [],
          totalBills: response.data.totalBills || 0,
          totalPages: response.data.totalPages || 0,
          printFormat: response.data.printFormat || '2_per_page',
          printDate: response.data.printDate || new Date().toISOString()
        };
        
        this.printDataSubject.next(printData);
        return printData;
      }),
      catchError(error => {
        console.error('Erreur lors de la récupération des factures pour impression:', error);
        throw error;
      })
    );
  }

  /**
   * Organise les factures en pages de 2 factures chacune
   */
  organizeBillsInPages(bills: BillModel[]): BillModel[][] {
    const pages: BillModel[][] = [];
    
    for (let i = 0; i < bills.length; i += 2) {
      const page: BillModel[] = [];
      page.push(bills[i]);
      
      if (i + 1 < bills.length) {
        page.push(bills[i + 1]);
      }
      
      pages.push(page);
    }
    
    return pages;
  }

  /**
   * Génère un PDF des factures (si implémenté côté backend)
   */
  generatePDF(billIds: number[]): Observable<Blob> {
    return this.http.post('bills/generate-pdf', billIds, {
      responseType: 'blob'
    });
  }

  /**
   * Obtient les statistiques de génération
   */
  getGenerationStatistics(): Observable<PrintStatistics> {
    return this.http.get<any>('bills/generation-statistics').pipe(
      map(response => response.data || response)
    );
  }

  /**
   * Vérifie l'existence de factures pour un mois donné
   */
  checkExistingBillsForMonth(customerIds: number[], month: string, year: string): Observable<any> {
    return this.http.post('bills/check-existing', {
      customerIds,
      month,
      year
    });
  }

  /**
   * Génère des factures pour des clients sélectionnés
   */
  generateBillsForCustomers(customerIds: number[], shouldGenerate: boolean = false): Observable<any> {
    return this.http.post(`bills/generate?shouldGenerate=${shouldGenerate}`, customerIds).pipe(
      map(response => {
        // Mettre à jour les factures sélectionnées si la génération est réussie
        if (response.data && response.data.bills) {
          this.setSelectedBills(response.data.bills);
        }
        return response;
      })
    );
  }

  /**
   * Supprime des factures en lot
   */
  deleteBillsBatch(billIds: number[]): Observable<any> {
    return this.http.delete('bills/batch', { body: billIds });
  }

  /**
   * Obtient le nombre de factures sélectionnées
   */
  getSelectedBillsCount(): number {
    return this.selectedBillsSubject.value.length;
  }

  /**
   * Vérifie si une facture est sélectionnée
   */
  isBillSelected(billId: number): boolean {
    return this.selectedBillsSubject.value.some(bill => bill.id === billId);
  }

  /**
   * Obtient toutes les factures sélectionnées
   */
  getSelectedBills(): BillModel[] {
    return this.selectedBillsSubject.value;
  }

  /**
   * Calcule le nombre de pages nécessaires pour l'impression
   */
  calculatePagesCount(bills: BillModel[]): number {
    return Math.ceil(bills.length / 2);
  }

  /**
   * Formate les données pour l'impression
   */
  formatForPrint(bills: BillModel[]): PrintResponse {
    const pages = this.organizeBillsInPages(bills);
    
    return {
      bills,
      pages,
      totalBills: bills.length,
      totalPages: pages.length,
      printFormat: '2_per_page',
      printDate: new Date().toISOString()
    };
  }
}
