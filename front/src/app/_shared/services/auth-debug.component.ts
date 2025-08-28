import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ButtonModule} from 'primeng/button';
import {CardModule} from 'primeng/card';
import {AuthService} from '../../auth/services/auth.service';
import {StorageService} from './storage.service';

@Component({
  selector: 'app-auth-debug',
  standalone: true,
  imports: [CommonModule, ButtonModule, CardModule],
  template: `
    <p-card header="Debug Authentification" styleClass="mt-4">
      <div class="mb-3">
        <h5>État d'authentification :</h5>
        <ul>
          <li><strong>Token valide (StorageService) :</strong> {{ storageTokenValid }}</li>
          <li><strong>Authentifié (AuthService) :</strong> {{ authServiceAuthenticated }}</li>
          <li><strong>Token présent :</strong> {{ hasToken }}</li>
          <li><strong>Date d'expiration :</strong> {{ tokenExpiration }}</li>
          <li><strong>Utilisateur présent :</strong> {{ hasUser }}</li>
          <li><strong>Dernière vérification :</strong> {{ lastCheck }}</li>
        </ul>
      </div>

      <div class="flex gap-2">
        <p-button
          label="Vérifier Token"
          (onClick)="checkToken()"
          severity="info">
        </p-button>
        <p-button
          label="Nettoyer Storage"
          (onClick)="clearStorage()"
          severity="warning">
        </p-button>
        <p-button
          label="Simuler Logout"
          (onClick)="simulateLogout()"
          severity="danger">
        </p-button>
      </div>

      <div class="mt-3" *ngIf="debugInfo">
        <h6>Informations détaillées :</h6>
        <pre>{{ debugInfo | json }}</pre>
      </div>
    </p-card>
  `
})
export class AuthDebugComponent implements OnInit {
  storageTokenValid = false;
  authServiceAuthenticated = false;
  hasToken = false;
  tokenExpiration = '';
  hasUser = false;
  lastCheck = '';
  debugInfo: any = null;

  constructor(
    private authService: AuthService,
    private storageService: StorageService
  ) {
  }

  ngOnInit(): void {
    this.updateStatus();

    this.authService.isAuthenticated$.subscribe(isAuth => {
      console.log('Auth state changed:', isAuth);
      this.updateStatus();
    });
  }

  updateStatus(): void {
    this.storageTokenValid = this.storageService.isTokenValid();
    this.authServiceAuthenticated = this.authService.isAuthenticated();
    this.hasToken = !!this.storageService.getToken;
    this.hasUser = !!this.storageService.getUser()?.telephone;
    this.lastCheck = new Date().toLocaleTimeString();

    const expirationString = this.storageService['localStorageService'].getItem('tokenExpiration');
    this.tokenExpiration = expirationString ? new Date(expirationString).toLocaleString() : 'Non définie';
  }

  checkToken(): void {
    const token = this.storageService.getToken;
    const user = this.storageService.getUser();
    const expiration = this.storageService['localStorageService'].getItem('tokenExpiration');

    this.debugInfo = {
      token: token ? 'Present (length: ' + token.length + ')' : 'Missing',
      user: user,
      expiration: expiration,
      isValid: this.storageService.isTokenValid(),
      currentTime: new Date().toISOString(),
      localStorage: {
        authToken: localStorage.getItem('auth_token') ? 'Present' : 'Missing',
        tokenExpiration: localStorage.getItem('tokenExpiration'),
        authUser: localStorage.getItem('auth_user') ? 'Present' : 'Missing'
      }
    };

    this.updateStatus();
  }

  clearStorage(): void {
    this.storageService.clean();
    this.updateStatus();
    console.log('Storage cleared');
  }

  simulateLogout(): void {
    this.authService.performLogout();
    this.updateStatus();
    console.log('Logout simulated');
  }
}
