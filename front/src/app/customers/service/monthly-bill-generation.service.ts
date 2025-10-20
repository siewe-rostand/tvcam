import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, timer, fromEvent, of } from 'rxjs';
import { switchMap, catchError, map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { BillManagementService } from './bill-management.service';

export interface MonthlyGenerationConfig {
  enabled: boolean;
  dayOfMonth: number; // Jour du mois pour la génération (1-31)
  time: string; // Heure de génération (format HH:MM)
  autoSelectAllCustomers: boolean;
  defaultMonthlyPayment: number;
  notificationEnabled: boolean;
}

export interface GenerationResult {
  success: boolean;
  billsGenerated: number;
  errors: string[];
  timestamp: Date;
}

@Injectable({
  providedIn: 'root'
})
export class MonthlyBillGenerationService {
  private configSubject = new BehaviorSubject<MonthlyGenerationConfig>({
    enabled: true,
    dayOfMonth: 1,
    time: '09:00',
    autoSelectAllCustomers: true,
    defaultMonthlyPayment: 2000,
    notificationEnabled: true
  });

  private generationResultSubject = new BehaviorSubject<GenerationResult | null>(null);

  public config$ = this.configSubject.asObservable();
  public generationResult$ = this.generationResultSubject.asObservable();

  constructor(
    private http: HttpClient,
    private billManagementService: BillManagementService
  ) {
    this.initializeAutoGeneration();
  }

  /**
   * Initialise la génération automatique mensuelle
   */
  private initializeAutoGeneration(): void {
    // Vérifier toutes les minutes si c'est le moment de générer
    timer(0, 60000).pipe(
      switchMap(() => this.checkIfShouldGenerate())
    ).subscribe();
  }

  /**
   * Vérifie si c'est le moment de générer les factures
   */
  private checkIfShouldGenerate(): Observable<boolean> {
    const config = this.configSubject.value;

    if (!config.enabled) {
      return of(false);
    }

    const now = new Date();
    const currentDay = now.getDate();
    const currentTime = now.getHours() * 60 + now.getMinutes();
    const targetTime = this.parseTime(config.time);

    // Vérifier si c'est le bon jour et la bonne heure
    if (currentDay === config.dayOfMonth && currentTime >= targetTime) {
      // Vérifier si on a déjà généré aujourd'hui
      return this.hasGeneratedToday().pipe(
        switchMap(hasGenerated => {
          if (!hasGenerated) {
            return this.performMonthlyGeneration();
          }
          return of(false as boolean);
        })
      );
    }

    return new Observable(observer => observer.next(false));
  }

  /**
   * Parse l'heure au format HH:MM en minutes
   */
  private parseTime(timeString: string): number {
    const [hours, minutes] = timeString.split(':').map(Number);
    return hours * 60 + minutes;
  }

  /**
   * Vérifie si les factures ont déjà été générées aujourd'hui
   */
  private hasGeneratedToday(): Observable<boolean> {
    const today = new Date();
    const month = this.getMonthName(today.getMonth());
    const year = today.getFullYear().toString();

    return this.http.get<{ hasGenerated: boolean }>(`bills/check-generation-today?month=${month}&year=${year}`).pipe(
      map((response: { hasGenerated: boolean }) => response.hasGenerated as boolean),
      catchError(() => of(false as boolean))
    );
  }

  /**
   * Effectue la génération mensuelle des factures
   */
  private performMonthlyGeneration(): Observable<boolean> {
    const config = this.configSubject.value;
    const today = new Date();
    const month = this.getMonthName(today.getMonth());
    const year = today.getFullYear().toString();
    // Charger tous les clients actifs
    return this.billManagementService.getCustomers().pipe(
      switchMap((customers: any[]) => {
        const activeCustomers = customers.filter(c => c.isActive);
        const customerIds = activeCustomers.map(c => c.id);

        if (customerIds.length === 0) {
          this.generationResultSubject.next({
            success: false,
            billsGenerated: 0,
            errors: ['Aucun client actif trouvé'],
            timestamp: new Date()
          });
          return of(false);
        }

        // Générer les factures
        return this.billManagementService.generateBillsForCustomers(customerIds, false).pipe(
          map(response => {
            const billsGenerated = Array.isArray(response) ? response.length : 1;

            this.generationResultSubject.next({
              success: true,
              billsGenerated,
              errors: [],
              timestamp: new Date()
            });

            // Notifier l'utilisateur si activé
            if (config.notificationEnabled) {
              this.notifyGenerationSuccess(billsGenerated, month, year);
            }

            return true;
          }),
          catchError(error => {
            this.generationResultSubject.next({
              success: false,
              billsGenerated: 0,
              errors: [error.message || 'Erreur lors de la génération'],
              timestamp: new Date()
            });

            if (config.notificationEnabled) {
              this.notifyGenerationError(error.message || 'Erreur lors de la génération');
            }

            return of(false);
          }),
          // Ensure the catchError returns Observable<boolean>
          switchMap(result => typeof result === 'boolean' ? of(result) : of(false))
        );
      })
    );
  }

  /**
   * Obtient le nom du mois en français
   */
  private getMonthName(monthIndex: number): string {
    const months = [
      'january', 'february', 'march', 'april', 'may', 'june',
      'july', 'august', 'september', 'october', 'november', 'december'
    ];
    return months[monthIndex];
  }

  /**
   * Met à jour la configuration
   */
  updateConfig(config: Partial<MonthlyGenerationConfig>): void {
    const currentConfig = this.configSubject.value;
    this.configSubject.next({ ...currentConfig, ...config });

    // Sauvegarder la configuration
    this.saveConfig();
  }

  /**
   * Sauvegarde la configuration
   */
  private saveConfig(): void {
    const config = this.configSubject.value;
    localStorage.setItem('monthlyBillGenerationConfig', JSON.stringify(config));
  }

  /**
   * Charge la configuration sauvegardée
   */
  loadConfig(): void {
    const savedConfig = localStorage.getItem('monthlyBillGenerationConfig');
    if (savedConfig) {
      try {
        const config = JSON.parse(savedConfig);
        this.configSubject.next(config);
      } catch (error) {
        console.error('Erreur lors du chargement de la configuration:', error);
      }
    }
  }

  /**
   * Force la génération manuelle des factures
   */
  forceGeneration(): Observable<GenerationResult> {
    return this.performMonthlyGeneration().pipe(
      map(success => this.generationResultSubject.value || {
        success,
        billsGenerated: 0,
        errors: ['Génération forcée'],
        timestamp: new Date()
      })
    );
  }

  /**
   * Notifie le succès de la génération
   */
  private notifyGenerationSuccess(billsGenerated: number, month: string, year: string): void {
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification('Génération de Factures Réussie', {
        body: `${billsGenerated} facture(s) générée(s) pour ${month} ${year}`,
        icon: '/favicon.ico'
      });
    }
  }

  /**
   * Notifie l'erreur de génération
   */
  private notifyGenerationError(error: string): void {
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification('Erreur de Génération de Factures', {
        body: error,
        icon: '/favicon.ico'
      });
    }
  }

  /**
   * Demande la permission de notification
   */
  requestNotificationPermission(): Promise<boolean> {
    if ('Notification' in window) {
      return Notification.requestPermission().then(permission => permission === 'granted');
    }
    return Promise.resolve(false);
  }
}
