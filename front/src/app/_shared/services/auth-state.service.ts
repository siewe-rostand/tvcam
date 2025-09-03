import {Inject, Injectable, PLATFORM_ID} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {filter, take} from 'rxjs/operators';
import {StorageService} from './storage.service';
import {isPlatformBrowser} from "@angular/common";

export interface AuthState {
  isAuthenticated: boolean;
  isInitialized: boolean;
  user: any | null;
  hasToken: boolean;
}


@Injectable({
  providedIn: 'root'
})
export class AuthStateService {
  private authStateSubject: BehaviorSubject<AuthState> = new BehaviorSubject<AuthState>({
    isAuthenticated: false,
    isInitialized: false,
    user: null,
    hasToken: false
  });
  private readonly isBrowser: boolean;

  public authState$ = this.authStateSubject.asObservable();

  constructor(private storageService: StorageService,
              @Inject(PLATFORM_ID) private platformId: Object) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    if (this.isBrowser) {
      this.initializeAuthState();
    } else {
      this.authStateSubject.next({
        isAuthenticated: false,
        isInitialized: true,
        user: null,
        hasToken: false
      });
    }
  }

  async initializeAuthState(): Promise<void> {
    try {
      if (!this.isBrowser) {
        console.log('🔐 SSR detected, deferring auth initialization...');
        this.authStateSubject.next({
          isAuthenticated: false,
          isInitialized: true,
          user: null,
          hasToken: false
        });
        return;
      }

      await this.waitForAuthInitialization();

      console.log('🔐 Initializing auth state...');

      const isTokenValid = this.storageService.isTokenValid();
      const user = isTokenValid ? this.storageService.getUser() : null;
      const hasToken = !!this.storageService.getToken;

      console.log('✅ Auth state initialized:', {
        isTokenValid,
        hasUser: !!user?.telephone,
        hasToken
      });

      this.authStateSubject.next({
        isAuthenticated: isTokenValid,
        isInitialized: true,
        user: user,
        hasToken: hasToken
      });

    } catch (error) {
      console.error('❌ Error initializing auth state:', error);
      this.authStateSubject.next({
        isAuthenticated: false,
        isInitialized: true,
        user: null,
        hasToken: false
      });
    }
  }

  private waitForAuthInitialization(): Promise<void> {
    return new Promise((resolve) => {
      setTimeout(() => resolve(), 200);
    });
  }

  updateAuthState(isAuthenticated: boolean, user: any = null): void {
    console.log('🔄 Updating auth state:', {isAuthenticated, user});

    const currentState = this.authStateSubject.value;
    const newState = {
      ...currentState,
      isAuthenticated,
      user: user,
      hasToken: isAuthenticated
    };

    console.log('📊 New auth state:', newState);
    this.authStateSubject.next(newState);
  }

  getCurrentState(): AuthState {
    return this.authStateSubject.value;
  }

  isInitialized(): boolean {
    const state = this.authStateSubject.value;
    return state.isInitialized;
  }

  isAuthenticated(): boolean {
    const state = this.authStateSubject.value;
    console.log('🔍 Checking authentication:', state);
    return state.isAuthenticated;
  }

  waitForBrowserInitialization(): Observable<AuthState> {
    if (!this.isBrowser) {
      console.log(this.authStateSubject.value);
      return this.authState$.pipe(take(1));
    }

    return this.authState$.pipe(
      filter(state => state.isInitialized),
      take(1)
    );
  }
}
