import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';

export interface ZoneInfo {
  id: number;
  name: string;
  code: string;
  address: string;
  description?: string;
  isActive: boolean;
  responsibleName: string;
  responsiblePhone: string;
  collectorName: string;
  collectorPhone: string;
  companyPhone: string;
}

export interface ZoneConfig {
  defaultResponsibleName: string;
  defaultResponsiblePhone: string;
  defaultCollectorName: string;
  defaultCollectorPhone: string;
  defaultCompanyPhone: string;
  defaultCompanyAddress: string;
}

@Injectable({
  providedIn: 'root'
})
export class ZoneManagementService {
  private zonesSubject = new BehaviorSubject<ZoneInfo[]>([]);
  public zones$ = this.zonesSubject.asObservable();

  private configSubject = new BehaviorSubject<ZoneConfig>({
    defaultResponsibleName: 'M. Jackson',
    defaultResponsiblePhone: '6 74 38 17 44',
    defaultCollectorName: 'M. Anderson',
    defaultCollectorPhone: '6 75 16 86 97',
    defaultCompanyPhone: '6 74 38 17 44 / 6 96 39 53 30',
    defaultCompanyAddress: 'Makèpe Missoke'
  });
  public config$ = this.configSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadZones();
    this.loadConfig();
  }

  /**
   * Charge la liste des zones
   */
  loadZones(): void {
    this.http.get<any>('zones').subscribe({
      next: (response) => {
        const zones = response.data || response || [];
        this.zonesSubject.next(zones);
      },
      error: (error) => {
        console.error('Erreur lors du chargement des zones:', error);
        // Utiliser des zones par défaut en cas d'erreur
        this.zonesSubject.next(this.getDefaultZones());
      }
    });
  }

  /**
   * Charge la configuration des zones
   */
  private loadConfig(): void {
    const savedConfig = localStorage.getItem('zoneConfig');
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
   * Sauvegarde la configuration des zones
   */
  saveConfig(config: Partial<ZoneConfig>): void {
    const currentConfig = this.configSubject.value;
    const newConfig = { ...currentConfig, ...config };
    this.configSubject.next(newConfig);
    localStorage.setItem('zoneConfig', JSON.stringify(newConfig));
  }

  /**
   * Obtient les informations d'une zone par ID
   */
  getZoneById(zoneId: number): Observable<ZoneInfo | null> {
    return this.zones$.pipe(
      map(zones => zones.find(zone => zone.id === zoneId) || null)
    );
  }

  /**
   * Obtient les informations d'une zone par nom
   */
  getZoneByName(zoneName: string): Observable<ZoneInfo | null> {
    return this.zones$.pipe(
      map(zones => zones.find(zone => zone.name === zoneName) || null)
    );
  }

  /**
   * Enrichit une facture avec les informations de zone
   */
  enrichBillWithZoneInfo(bill: any): Observable<any> {
    if (!bill.zoneId && !bill.zoneName) {
      // Utiliser la configuration par défaut
      const config = this.configSubject.value;
      return new Observable(observer => {
        observer.next({
          ...bill,
          zoneName: config.defaultCompanyAddress,
          zoneAddress: config.defaultCompanyAddress,
          responsibleName: config.defaultResponsibleName,
          responsiblePhone: config.defaultResponsiblePhone,
          collectorName: config.defaultCollectorName,
          collectorPhone: config.defaultCollectorPhone,
          companyPhone: config.defaultCompanyPhone,
          companyAddress: config.defaultCompanyAddress
        });
      });
    }

    const zoneId = bill.zoneId;
    const zoneName = bill.zoneName;

    if (zoneId) {
      return this.getZoneById(zoneId).pipe(
        map(zone => {
          if (zone) {
            return {
              ...bill,
              zoneName: zone.name,
              zoneAddress: zone.address,
              responsibleName: zone.responsibleName,
              responsiblePhone: zone.responsiblePhone,
              collectorName: zone.collectorName,
              collectorPhone: zone.collectorPhone,
              companyPhone: zone.companyPhone
            };
          }
          return this.enrichWithDefaultConfig(bill);
        })
      );
    } else if (zoneName) {
      return this.getZoneByName(zoneName).pipe(
        map(zone => {
          if (zone) {
            return {
              ...bill,
              zoneName: zone.name,
              zoneAddress: zone.address,
              responsibleName: zone.responsibleName,
              responsiblePhone: zone.responsiblePhone,
              collectorName: zone.collectorName,
              collectorPhone: zone.collectorPhone,
              companyPhone: zone.companyPhone
            };
          }
          return this.enrichWithDefaultConfig(bill);
        })
      );
    }

    return new Observable(observer => {
      observer.next(this.enrichWithDefaultConfig(bill));
    });
  }

  /**
   * Enrichit une facture avec la configuration par défaut
   */
  private enrichWithDefaultConfig(bill: any): any {
    const config = this.configSubject.value;
    return {
      ...bill,
      zoneName: bill.zoneName || config.defaultCompanyAddress,
      zoneAddress: config.defaultCompanyAddress,
      responsibleName: config.defaultResponsibleName,
      responsiblePhone: config.defaultResponsiblePhone,
      collectorName: config.defaultCollectorName,
      collectorPhone: config.defaultCollectorPhone,
      companyPhone: config.defaultCompanyPhone,
      companyAddress: config.defaultCompanyAddress
    };
  }

  /**
   * Obtient les zones par défaut
   */
  private getDefaultZones(): ZoneInfo[] {
    const config = this.configSubject.value;
    return [
      {
        id: 1,
        name: 'Makèpe Missoke',
        code: 'MAK001',
        address: 'En face du Centre Médical la LIFE, près de l\'école la Solidarité',
        description: 'Zone principale de Makèpe Missoke',
        isActive: true,
        responsibleName: config.defaultResponsibleName,
        responsiblePhone: config.defaultResponsiblePhone,
        collectorName: config.defaultCollectorName,
        collectorPhone: config.defaultCollectorPhone,
        companyPhone: config.defaultCompanyPhone
      }
    ];
  }

  /**
   * Crée une nouvelle zone
   */
  createZone(zone: Omit<ZoneInfo, 'id'>): Observable<ZoneInfo> {
    return this.http.post<ZoneInfo>('zones', zone).pipe(
      map(newZone => {
        const currentZones = this.zonesSubject.value;
        this.zonesSubject.next([...currentZones, newZone]);
        return newZone;
      })
    );
  }

  /**
   * Met à jour une zone existante
   */
  updateZone(zone: ZoneInfo): Observable<ZoneInfo> {
    return this.http.put<ZoneInfo>(`zones/${zone.id}`, zone).pipe(
      map(updatedZone => {
        const currentZones = this.zonesSubject.value;
        const index = currentZones.findIndex(z => z.id === zone.id);
        if (index !== -1) {
          currentZones[index] = updatedZone;
          this.zonesSubject.next([...currentZones]);
        }
        return updatedZone;
      })
    );
  }

  /**
   * Supprime une zone
   */
  deleteZone(zoneId: number): Observable<void> {
    return this.http.delete<void>(`zones/${zoneId}`).pipe(
      map(() => {
        const currentZones = this.zonesSubject.value;
        const filteredZones = currentZones.filter(z => z.id !== zoneId);
        this.zonesSubject.next(filteredZones);
      })
    );
  }
}
