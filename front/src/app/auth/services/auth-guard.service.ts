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
import { AuthService } from "./auth.service";

@Injectable({
  providedIn: 'root',
})
export class AuthGuardService implements CanActivate {
  constructor(
    private readonly router: Router,
    private readonly storageService: StorageService,
    private readonly authService: AuthService
  ) {
  }

  canActivate(
    _route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): MaybeAsync<GuardResult> {
    if (this.authService.isAuthenticated()) {
      return true;
    }
    console.log(`Access denied - redirecting to login`);
    this.storageService.clean();
    const tree = this.router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
    // Serialize the UrlTree for logging; if serialization fails the exception will propagate.
    const serialized = this.router.serializeUrl(tree);
    console.log('AuthGuard returning UrlTree:', serialized);
    return tree;
  }
}
