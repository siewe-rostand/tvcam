import { Injectable } from '@angular/core';
import {
  CanActivate,
  Router,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Observable } from 'rxjs';
import { StorageService } from './storage.service';
import { AlertService } from '../../_shared/services/alert.service';

@Injectable({
  providedIn: 'root', // Assuming this guard is used application-wide
})
export class AuthGuardService implements CanActivate {
  constructor(
    private storageService: StorageService,
    private router: Router,
    private alert: AlertService
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ):
    | boolean
    | UrlTree
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree> {
    const isAuthenticated = this.storageService.isLoggedIn();

    if (isAuthenticated) {
      return true;
    } else {
      this.alert.show('Access not allowed!');
      this.router.navigate(['/login']); // Assuming login route is '/login'
      return false;
    }
  }
}
