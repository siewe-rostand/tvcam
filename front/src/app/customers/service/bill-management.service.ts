import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, forkJoin, map, catchError, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { BillService } from './bill.service';
import { PaymentService } from './payment.service';
import { BillModel } from '../model/bill.model';
import { PaymentModel } from '../model/payment.model';

@Injectable({
    providedIn: 'root'
})
export class BillManagementService {
    private billsSubject = new BehaviorSubject<BillModel[]>([]);
    public bills$ = this.billsSubject.asObservable();

    private loadingSubject = new BehaviorSubject<boolean>(false);
    public loading$ = this.loadingSubject.asObservable();

    constructor(
        private billService: BillService,
        private paymentService: PaymentService,
        private http: HttpClient
    ) { }

    /**
     * Charge toutes les factures avec mise en cache
     */
    loadBills(): Observable<BillModel[]> {
        this.loadingSubject.next(true);

        return this.billService.fetchBills().pipe(
            map(response => {
                const bills = response.data || [];
                // Calculer les montants restants et enrichir les données
                const enrichedBills = bills.map((bill: BillModel) => ({
                    ...bill,
                    remainingBalance: this.calculateRemainingBalance(bill)
                }));

                this.billsSubject.next(enrichedBills);
                this.loadingSubject.next(false);
                return enrichedBills;
            })
        );
    }

    /**
     * Actualise une facture spécifique après paiement
     */
    updateBillAfterPayment(billId: number, paymentAmount: number): void {
        const currentBills = this.billsSubject.value;
        const updatedBills = currentBills.map(bill => {
            if (bill.id === billId) {
                const newPaidAmount = (bill.paidAmount || 0) + paymentAmount;
                const remainingBalance = (bill.netToPay || 0) - newPaidAmount;

                return {
                    ...bill,
                    paidAmount: newPaidAmount,
                    remainingBalance: remainingBalance,
                    status: remainingBalance <= 0 ? 'PAID' : 'PARTIALLY_PAID'
                };
            }
            return bill;
        });

        this.billsSubject.next(updatedBills);
    }

    /**
     * Supprime des factures de la liste locale
     */
    removeBillsFromCache(billIds: number[]): void {
        const currentBills = this.billsSubject.value;
        const filteredBills = currentBills.filter(bill => !billIds.includes(bill.id || 0));
        this.billsSubject.next(filteredBills);
    }

    /**
     * Génère des factures pour des clients sélectionnés avec validation
     */
    generateBillsForCustomers(customerIds: number[], shouldGenerate: boolean = false): Observable<any> {
        this.loadingSubject.next(true);

        return this.billService.generateBills(customerIds, shouldGenerate).pipe(
            map(response => {
                this.loadingSubject.next(false);
                // Recharger les factures après génération
                this.loadBills().subscribe();
                return response;
            })
        );
    }

    /**
     * Génère des factures avec vérification de doublons
     * @param customerIds Liste des IDs clients
     * @param shouldGenerate Forcer la génération
     * @param forceUpdate Forcer la mise à jour des factures existantes
     */
    generateBillsWithDuplicateCheck(
        customerIds: number[],
        shouldGenerate: boolean = false,
        forceUpdate?: boolean
    ): Observable<any> {
        this.loadingSubject.next(true);

        return this.billService.generateBillsWithDuplicateCheck(customerIds, shouldGenerate, forceUpdate).pipe(
            map(response => {
                this.loadingSubject.next(false);
                // Recharger les factures après génération
                this.loadBills().subscribe();
                return response;
            })
        );
    }

    /**
     * Vérifie si des factures existent déjà pour le mois donné
     */
    checkExistingBillsForMonth(customerIds: number[], month: string, year: string): Observable<any> {
        return this.billService.checkExistingBillsForMonth(customerIds, month, year);
    }

    /**
     * Génère des factures avec vérification mensuelle
     */
    generateBillsWithMonthlyCheck(
        customerIds: number[],
        month: string,
        year: string,
        forceGeneration: boolean = false
    ): Observable<any> {
        this.loadingSubject.next(true);

        // Si forceGeneration est true, générer directement
        if (forceGeneration) {
            return this.generateBillsForCustomers(customerIds, true);
        }

        // Sinon, vérifier d'abord s'il y a des factures existantes
        return this.checkExistingBillsForMonth(customerIds, month, year).pipe(
            map(response => {
                this.loadingSubject.next(false);
                return response;
            })
        );
    }

    /**
     * Calcule le montant restant à payer pour une facture
     */
    private calculateRemainingBalance(bill: BillModel): number {
        return (bill.netToPay || 0) - (bill.paidAmount || 0);
    }

    /**
     * Valide si un paiement est possible pour une facture
     */
    validatePayment(bill: BillModel, amount: number): { valid: boolean; message?: string } {
        if (amount <= 0) {
            return { valid: false, message: 'Le montant doit être supérieur à 0' };
        }

        const remainingBalance = this.calculateRemainingBalance(bill);

        if (amount > remainingBalance) {
            return {
                valid: false,
                message: `Le montant ne peut pas dépasser le reste à payer: ${remainingBalance} FCFA`
            };
        }

        if (bill.status === 'PAID') {
            return { valid: false, message: 'Cette facture a déjà été payée intégralement' };
        }

        return { valid: true };
    }

    /**
     * Obtient les statistiques des factures
     */
    getBillsStatistics(): Observable<any> {
        return this.bills$.pipe(
            map(bills => {
                const total = bills.length;
                const paid = bills.filter(b => b.status === 'PAID').length;
                const unpaid = bills.filter(b => b.status === 'UNPAID').length;
                const partiallyPaid = bills.filter(b => b.status === 'PARTIALLY_PAID').length;

                const totalAmount = bills.reduce((sum, bill) => sum + (bill.netToPay || 0), 0);
                const paidAmount = bills.reduce((sum, bill) => sum + (bill.paidAmount || 0), 0);
                const remainingAmount = totalAmount - paidAmount;

                return {
                    total,
                    paid,
                    unpaid,
                    partiallyPaid,
                    totalAmount,
                    paidAmount,
                    remainingAmount,
                    paymentRate: total > 0 ? (paid / total) * 100 : 0
                };
            })
        );
    }

    /**
     * Filtre les factures selon différents critères
     */
    filterBills(criteria: {
        status?: string;
        month?: string;
        year?: string;
        customerName?: string;
    }): Observable<BillModel[]> {
        return this.bills$.pipe(
            map(bills => {
                return bills.filter(bill => {
                    if (criteria.status && bill.status !== criteria.status) return false;
                    if (criteria.month && bill.month !== criteria.month) return false;
                    if (criteria.year && bill.year !== criteria.year) return false;
                    if (criteria.customerName &&
                        !bill.customerName?.toLowerCase().includes(criteria.customerName.toLowerCase())) {
                        return false;
                    }
                    return true;
                });
            })
        );
    }

    /**
     * Récupère les factures en retard
     */
    getOverdueBills(): Observable<BillModel[]> {
        return this.bills$.pipe(
            map(bills => {
                const today = new Date();
                return bills.filter(bill => {
                    if (bill.status === 'PAID') return false;
                    if (!bill.deadLine) return false;

                    const deadline = new Date(bill.deadLine);
                    return deadline < today;
                });
            })
        );
    }

    /**
     * Charge la liste des clients
     */
    getCustomers(): Observable<any> {
        return this.http.get<any>('customers').pipe(
            map(response => {
                // Handle different response structures from the API
                if (response && response.data && Array.isArray(response.data)) {
                    return response.data;
                } else if (Array.isArray(response)) {
                    return response;
                } else {
                    console.warn('Unexpected customers response structure:', response);
                    return [];
                }
            }),
            catchError(error => {
                console.error('Error fetching customers:', error);
                return of([]);
            })
        );
    }
}
