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

@Injectable({
  providedIn: 'root',
})
export class AuthGuardService implements CanActivate {
  constructor(private router: Router,
              private storageService: StorageService) {
  }

  canActivate(
    _route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): MaybeAsync<GuardResult> {
    const isTokenValid = this.storageService.isTokenValid();

    if (isTokenValid) {
      // User is authenticated, allow access
      return true;
    } else {
      // User is not authenticated, redirect to log in
      console.log('Access denied - redirecting to login');
      this.storageService.clean(); // Clean any invalid tokens
      this.router.navigate(['/login'], {
        queryParams: {returnUrl: state.url}
      }).then(() => true);
      return false;
    }
  }
}
