import {Injectable} from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  GuardResult,
  MaybeAsync,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import {AlertService} from "../../_shared/services/alert.service";
import {StorageService} from "../../_shared/services/storage.service";

@Injectable({
  providedIn: 'root',
})
export class AuthGuardService implements CanActivate {
  constructor(private router: Router,
              private alertService: AlertService,
              private storageService: StorageService) {
  }
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): MaybeAsync<GuardResult> {
    const isTokenValid = this.storageService.isTokenValid();

    if (route.data['skipAuthCheck']) {
      return true;
    }

    if (!isTokenValid) {
      this.alertService.show('Access not allowed!');
      this.storageService.clean();
      this.router.navigate(['/login']).then(r => r); // Assuming login route is '/login'
      return false;
    }
    return true;
  }
}
