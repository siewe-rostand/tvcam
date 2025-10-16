import {Injectable} from '@angular/core';
import {BehaviorSubject, catchError, map, Observable, throwError} from 'rxjs';
import {PaymentService} from './payment.service';
import {PaymentFilter, PaymentModel} from '../model/payment.model';

export interface PaymentValidation {
  valid: boolean;
  message?: string;
  warningMessage?: string;
}

export interface PaymentSummary {
  totalPayments: number;
  totalAmount: number;
  paymentsByMethod: { [key: string]: number };
  paymentsByStatus: { [key: string]: number };
  monthlyTrend: any[];
}

@Injectable({
  providedIn: 'root'
})
export class PaymentManagementService {
  private paymentsSubject = new BehaviorSubject<PaymentModel[]>([]);
  public payments$ = this.paymentsSubject.asObservable();

  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();

  constructor(private paymentService: PaymentService) {
  }

  /**
   * Effectue un paiement avec validation avancée
   */
  makePaymentWithValidation(payment: PaymentModel): Observable<any> {
    // Validation pré-paiement
    const validation = this.validatePaymentRequest(payment);
    if (!validation.valid) {
      return throwError(() => new Error(validation.message));
    }

    this.loadingSubject.next(true);

    return this.paymentService.makePayment(payment).pipe(
      map(response => {
        this.loadingSubject.next(false);
        // Ajouter le paiement à la liste locale
        this.addPaymentToCache(response.data || payment);
        return response;
      }),
      catchError(error => {
        this.loadingSubject.next(false);
        return throwError(() => error);
      })
    );
  }

  /**
   * Valide une demande de paiement
   */
  private validatePaymentRequest(payment: PaymentModel): PaymentValidation {
    if (!payment.amount || payment.amount <= 0) {
      return {valid: false, message: 'Le montant doit être supérieur à 0'};
    }

    if (!payment.customerId) {
      return {valid: false, message: 'L\'ID du client est requis'};
    }

    if (!payment.billId) {
      return {valid: false, message: 'L\'ID de la facture est requis'};
    }

    // Validation du mode de paiement
    const validPaymentMethods = ['CASH', 'MTN_MONEY', 'ORANGE_MONEY', 'BANK_TRANSFER'];
    if (payment.paymentMethod && !validPaymentMethods.includes(payment.paymentMethod)) {
      return {valid: false, message: 'Mode de paiement invalide'};
    }

    // Avertissement pour les gros montants
    if (payment.amount > 100000) {
      return {
        valid: true,
        warningMessage: 'Montant élevé détecté. Veuillez vérifier le montant.'
      };
    }

    return {valid: true};
  }

  /**
   * Charge les paiements pour un client
   */
  // loadPaymentsForCustomer(customerId: number): Observable<ApiResponse<PaymentModel[]>> {
  //   this.loadingSubject.next(true);
  //
  //   return this.paymentService.getPaymentsForCustomer(customerId).pipe(
  //     map(response => {
  //       const payments = response.data || [];
  //       this.paymentsSubject.next(payments);
  //       this.loadingSubject.next(false);
  //       return payments;
  //     }),
  //     catchError(error => {
  //       this.loadingSubject.next(false);
  //       return throwError(() => error);
  //     })
  //   );
  // }

  /**
   * Charge les paiements pour un mois donné
   */
  // loadMonthlyPayments(month: string): Observable<PaymentModel[]> {
  //   this.loadingSubject.next(true);
  //
  //   return this.paymentService.getMonthlyPayment(month).pipe(
  //     map(response => {
  //       const payments = response.data || [];
  //       this.paymentsSubject.next(payments);
  //       this.loadingSubject.next(false);
  //       return payments;
  //     }),
  //     catchError(error => {
  //       this.loadingSubject.next(false);
  //       return throwError(() => error);
  //     })
  //   );
  // }

  /**
   * Ajoute un paiement au cache local
   */
  private addPaymentToCache(payment: PaymentModel): void {
    const currentPayments = this.paymentsSubject.value;
    this.paymentsSubject.next([...currentPayments, payment]);
  }

  /**
   * Génère un résumé des paiements
   */
  getPaymentsSummary(): Observable<PaymentSummary> {
    return this.payments$.pipe(
      map(payments => {
        const totalPayments = payments.length;
        const totalAmount = payments.reduce((sum, p) => sum + (p.amount || 0), 0);

        // Paiements par méthode
        const paymentsByMethod = payments.reduce((acc, payment) => {
          const method = payment.paymentMethod || 'UNKNOWN';
          acc[method] = (acc[method] || 0) + (payment.amount || 0);
          return acc;
        }, {} as { [key: string]: number });

        // Paiements par statut
        const paymentsByStatus = payments.reduce((acc, payment) => {
          const status = payment.paymentStatus || 'UNKNOWN';
          acc[status] = (acc[status] || 0) + 1;
          return acc;
        }, {} as { [key: string]: number });

        // Tendance mensuelle (simplifié)
        const monthlyTrend = this.calculateMonthlyTrend(payments);

        return {
          totalPayments,
          totalAmount,
          paymentsByMethod,
          paymentsByStatus,
          monthlyTrend
        };
      })
    );
  }

  /**
   * Calcule la tendance mensuelle des paiements
   */
  private calculateMonthlyTrend(payments: PaymentModel[]): any[] {
    const monthlyData = payments.reduce((acc, payment) => {
      if (payment.paymentDate) {
        const date = new Date(payment.paymentDate);
        const monthKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;

        if (!acc[monthKey]) {
          acc[monthKey] = {month: monthKey, amount: 0, count: 0};
        }

        acc[monthKey].amount += payment.amount || 0;
        acc[monthKey].count += 1;
      }
      return acc;
    }, {} as { [key: string]: any });

    return Object.values(monthlyData).sort((a, b) => a.month.localeCompare(b.month));
  }

  /**
   * Filtre les paiements selon différents critères
   */
  filterPayments(criteria: PaymentFilter): Observable<PaymentModel[]> {
    return this.payments$.pipe(
      map(payments => {
        return payments.filter(payment => {
          if (criteria.paymentMethod && payment.paymentMethod !== criteria.paymentMethod) {
            return false;
          }

          if (criteria.customerId && payment.customerId !== criteria.customerId) {
            return false;
          }

          if (criteria.minAmount && (payment.amount || 0) < criteria.minAmount) {
            return false;
          }

          if (criteria.maxAmount && (payment.amount || 0) > criteria.maxAmount) {
            return false;
          }

          if (criteria.dateFrom && payment.paymentDate) {
            const paymentDate = new Date(payment.paymentDate);
            if (paymentDate < criteria.dateFrom) return false;
          }

          if (criteria.dateTo && payment.paymentDate) {
            const paymentDate = new Date(payment.paymentDate);
            if (paymentDate > criteria.dateTo) return false;
          }

          return true;
        });
      })
    );
  }

  /**
   * Génère un reçu de paiement formaté
   */
  generatePaymentReceipt(payment: PaymentModel): string {
    const date = payment.paymentDate ? new Date(payment.paymentDate).toLocaleDateString('fr-FR') : 'N/A';
    const amount = payment.amount?.toLocaleString('fr-FR') || '0';

    return `
REÇU DE PAIEMENT
================

Date: ${date}
Référence: ${payment.paymentReference || 'N/A'}
Client: ${payment.customerName || 'N/A'}
Montant: ${amount} FCFA
Méthode: ${this.formatPaymentMethod(payment.paymentMethod)}
Observation: ${payment.observation || 'Aucune'}

Merci pour votre paiement !
    `.trim();
  }

  /**
   * Formate le nom de la méthode de paiement
   */
  private formatPaymentMethod(method?: string): string {
    const methods: { [key: string]: string } = {
      'CASH': 'Espèces',
      'MTN_MONEY': 'MTN Money',
      'ORANGE_MONEY': 'Orange Money',
      'BANK_TRANSFER': 'Virement bancaire'
    };

    return methods[method || ''] || method || 'Non spécifié';
  }

  /**
   * Vérifie si un paiement peut être annulé
   */
  canCancelPayment(payment: PaymentModel): boolean {
    if (!payment.paymentDate) return false;

    const paymentDate = new Date(payment.paymentDate);
    const now = new Date();
    const hoursDiff = (now.getTime() - paymentDate.getTime()) / (1000 * 60 * 60);

    // Peut être annulé dans les 24 heures
    return hoursDiff < 24;
  }

  /**
   * Calcule les statistiques de performance
   */
  getPerformanceMetrics(): Observable<any> {
    return this.payments$.pipe(
      map(payments => {
        const now = new Date();
        const thisMonth = payments.filter(p => {
          if (!p.paymentDate) return false;
          const paymentDate = new Date(p.paymentDate);
          return paymentDate.getMonth() === now.getMonth() &&
            paymentDate.getFullYear() === now.getFullYear();
        });

        const lastMonth = payments.filter(p => {
          if (!p.paymentDate) return false;
          const paymentDate = new Date(p.paymentDate);
          const lastMonthDate = new Date(now.getFullYear(), now.getMonth() - 1);
          return paymentDate.getMonth() === lastMonthDate.getMonth() &&
            paymentDate.getFullYear() === lastMonthDate.getFullYear();
        });

        const thisMonthAmount = thisMonth.reduce((sum, p) => sum + (p.amount || 0), 0);
        const lastMonthAmount = lastMonth.reduce((sum, p) => sum + (p.amount || 0), 0);

        const growth = lastMonthAmount > 0 ?
          ((thisMonthAmount - lastMonthAmount) / lastMonthAmount) * 100 : 0;

        return {
          thisMonthCount: thisMonth.length,
          thisMonthAmount,
          lastMonthCount: lastMonth.length,
          lastMonthAmount,
          growth: Math.round(growth * 100) / 100,
          averagePayment: thisMonth.length > 0 ? thisMonthAmount / thisMonth.length : 0
        };
      })
    );
  }
}
