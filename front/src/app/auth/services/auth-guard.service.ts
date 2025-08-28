import {Injectable} from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  GuardResult,
  MaybeAsync,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import {StorageService} from "../../_shared/services/storage.service";
import {AuthStateService} from "../../_shared/services/auth-state.service";
import {map} from 'rxjs';

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
        console.log('################# ==', authState.isInitialized);
        if (authState.isAuthenticated) {
          return true;
        } else {
          console.log(`Access denied - redirecting to login${authState.isInitialized}`);
          this.storageService.clean();
          this.router.navigate(['/login'], {
            queryParams: {returnUrl: state.url}
          }).then(() => true);
          return false;
        }
      })
    );
  }
}
