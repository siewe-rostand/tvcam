import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {AuthResponseModel} from '../model/auth-response.model';
import {Injectable} from '@angular/core';
import {RegisterRequest} from '../model/registration-request.model';
import {StorageService} from '../../_shared/services/storage.service';
import {Router} from '@angular/router';
import {UserModel} from '../../user/model/user.model';
import {AuthState, AuthStateService} from '../../_shared/services/auth-state.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  public isAuthenticated$ = this.authStateService.authState$.pipe(
    map((state: AuthState) => state.isAuthenticated)
  );
  public currentUser$ = this.authStateService.authState$.pipe(
    map((state: AuthState) => state.user)
  );

  isLoggedIn: boolean = false;

  constructor(
    private http: HttpClient,
    private storageService: StorageService,
    private router: Router,
    private authStateService: AuthStateService
  ) { }

  login(telephone: string, password: string): Observable<any> {
    return this.http.post<any>(
      'auth/login',
      {
        password: password,
        telephone: telephone,
      }
    );
  }

  /**
   * Handle successful login response
   */
  handleLoginSuccess(response: AuthResponseModel): void {
    console.log('Handling login success');

    if (response.token) {
      this.storageService.saveToken(response.token);

      if (response.user) {
        console.info('Login response user data', response.user);
        const user: UserModel = response.user;

        this.storageService.saveUser(user);
        this.authStateService.updateAuthState(true, user);
      }

      this.isLoggedIn = true;
      this.router.navigate(['/dashboard']).then(() => true);
    }
  }

  changePassword(telephone: string, password: string): Observable<any> {
    return this.http.post<any>(
      'auth/password/change',
      {
        newPassword: password,
        telephone: telephone,
      }
    );
  }

  signup(request: RegisterRequest): Observable<any> {
    return this.http.post<AuthResponseModel>(
      'auth/register',
      request
    );
  }

  /**
   * Check if user is currently authenticated
   */
  isAuthenticated(): boolean {
    const currentState = this.authStateService.getCurrentState();
    if (currentState.isInitialized) {
      return currentState.isAuthenticated;
    }
    return this.storageService.isTokenValid();
  }

  /**
   * Get current user
   */
  getCurrentUser(): UserModel | null {
    const currentState = this.authStateService.getCurrentState();
    if (currentState.isInitialized && currentState.isAuthenticated) {
      return currentState.user;
    }
    return null;
  }

  /**
   * Get current token
   */
  getToken(): string | null {
    if (this.isAuthenticated()) {
      return this.storageService.getToken;
    }
    return null;
  }

  logout(): Observable<any> {
    return this.http.post('auth/logout', {},);
  }

  /**
   * Handle logout (both API and local)
   */
  performLogout(): void {
    console.log('Performing logout');
    this.authStateService.updateAuthState(false, null);
    this.isLoggedIn = false;

    this.router.navigate(['/login']);
  }

  /**
   * Handle authentication errors
   */
  handleAuthError(error: any): void {
    console.error('Authentication error:', error);

    if (error.status === 401 || error.status === 403) {
      console.log('Unauthorized access, logging out');
      this.performLogout();
    }
  }
}
