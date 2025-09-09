import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthResponseModel, LoginResponse } from '../model/auth-response.model';
import { Injectable } from '@angular/core';
import { RegisterRequest } from '../model/registration-request.model';
import { StorageService } from '../../_shared/services/storage.service';
import { Router } from '@angular/router';
import { UserModel } from '../../user/model/user.model';
import { AuthState, AuthStateService } from './auth-state.service';
import { ApiResponse } from '../../_shared/model/api-response';
import { log } from 'console';

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
  ) {
  }

  login(telephone: string, password: string): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(
      'auth/login',
      {
        password: password,
        telephone: telephone,
      }
    );
  }

  getUserInfo(token: string): Observable<UserModel> {
    return this.http.get<ApiResponse<UserModel>>('auth/user', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }).pipe(
      map((response: ApiResponse<UserModel>) => {
        const user = response.data;
        this.authStateService.updateAuthState(true, user);
        return user;
      })
    );
  }

  /**
   * Handle successful login response
   */
  handleLoginSuccess(response: UserModel): void {

    if (response.telephone) {
      this.authStateService.updateAuthState(true, response);
    }

    this.isLoggedIn = true;
    this.router.navigate(['/dashboard']).then(() => true);
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
