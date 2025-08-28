import {Inject, Injectable, PLATFORM_ID} from '@angular/core';
import { UserModel } from '../../user/model/user.model';
import { LocalStorageService } from './local-storage.service';
import { JWT_TOKEN, JWT_TOKEN_EXPIRATION, USER_KEY } from "../utils/constant";
import {isPlatformBrowser} from "@angular/common";


@Injectable({
  providedIn: 'root',
})
export class StorageService {
  private isBrowser: boolean;
  constructor(
    private localStorageService: LocalStorageService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  clean() {
    this.localStorageService.clear();
  }

  public saveUser(user: UserModel): void {
    this.localStorageService.removeItem(USER_KEY);
    this.localStorageService.setItem(USER_KEY, JSON.stringify(user));
  }

  public saveToken(token: string): void {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expirationDate = new Date(payload.exp * 1000);

      // Validation du token
      if (!payload.exp || isNaN(expirationDate.getTime())) {
        throw new Error('Token invalide');
      }

      this.localStorageService.removeItem(JWT_TOKEN);
      this.localStorageService.setItem(JWT_TOKEN, token);
      this.localStorageService.setItem(JWT_TOKEN_EXPIRATION, expirationDate.toISOString());
    } catch (error) {
      console.error('Erreur lors de la sauvegarde du token:', error);
      throw new Error('Token JWT invalide');
    }
  }

  public getUser(): UserModel {
    const user = this.localStorageService.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }

    return {};
  }

  get getToken(): string | null {
    return this.localStorageService.getItem(JWT_TOKEN);
  }

  isTokenValid(): boolean {
    try {
      // Vérifier si on est côté serveur
      if (!this.isBrowser) {
        return false;
      }

      const expirationString = this.localStorageService.getItem(JWT_TOKEN_EXPIRATION);
      const token = this.getToken;

      if (!expirationString || !token) {
        return false;
      }

      const expirationDate = new Date(expirationString);
      const currentDate = new Date();

      // Marge de sécurité de 5 minutes
      const safetyMargin = 5 * 60 * 1000;

      return expirationDate.getTime() > (currentDate.getTime() + safetyMargin);
    } catch (error) {
      console.error('Erreur lors de la validation du token:', error);
      return false;
    }
  }

  public waitForLocalStorage(): Promise<void> {
    return new Promise((resolve) => {
      if (typeof Storage !== 'undefined') {
        resolve();
      } else {
        setTimeout(() => resolve(), 100);
      }
    });
  }

  public logout(): void {
    this.clean();
  }
}
