import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { ToastModule } from "primeng/toast";
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { AuthState, AuthStateService } from './auth/services/auth-state.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ToastModule, CommonModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  title = 'TV CAM';
  authState$: Observable<AuthState>;

  constructor(
    private readonly router: Router,
    private readonly authStateService: AuthStateService,
    @Inject(PLATFORM_ID) private readonly platformId: Object
  ) {
    isPlatformBrowser(this.platformId);
    this.authState$ = this.authStateService.authState$;
  }

  ngOnInit() {
    this.checkAuthenticationStatus();
  }

  private checkAuthenticationStatus() {
    const currentRoute = this.router.url;
    const publicRoutes = ['/login', '/signup', '/forgottenPassword'];
    const isPublicRoute = publicRoutes.some(route => currentRoute.startsWith(route));

    if (this.authStateService.isAuthenticated() && isPublicRoute) {
      this.router.navigate(['/dashboard']).then(() => true);
    } else if (!this.authStateService.isAuthenticated() && !isPublicRoute && currentRoute !== '/') {
      this.router.navigate(['/login']).then(() => true);
    }
  }
}
