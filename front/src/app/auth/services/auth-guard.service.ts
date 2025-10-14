import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  GuardResult,
  MaybeAsync,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { StorageService } from "../../_shared/services/storage.service";
import { AuthStateService } from "./auth-state.service";
import { map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthGuardService implements CanActivate {
  constructor(
    private router: Router,
    private storageService: StorageService,
    private authStateService: AuthStateService
  ) {
  }

  canActivate(
    _route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): MaybeAsync<GuardResult> {
    return this.authStateService.waitForBrowserInitialization().pipe(
      map(authState => {
        if (authState.isAuthenticated) {
          return true;
        }

        // Not authenticated: cleanup and redirect via UrlTree so the router performs a
        // deterministic redirect instead of relying on an async router.navigate call
        // which can be ignored if the original navigation was cancelled.
        console.log(`Access denied - redirecting to login ${authState.isInitialized}`);
        this.storageService.clean();
        const tree = this.router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
        try {
          console.log('AuthGuard returning UrlTree:', this.router.serializeUrl(tree));
        } catch (e) {
          console.log('AuthGuard UrlTree (serialization failed)', tree);
        }
        return tree;
      })
    );
  }
}
