import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { ToastModule } from "primeng/toast";
import { StorageService } from './_shared/services/storage.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ToastModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'TV CAM';

  constructor(
    private storageService: StorageService,
    private router: Router
  ) { }

  ngOnInit() {
    this.checkAuthenticationStatus();
  }

  private checkAuthenticationStatus() {
    const isTokenValid = this.storageService.isTokenValid();
    const currentRoute = this.router.url;

    // Public routes that don't require authentication
    const publicRoutes = ['/login', '/signup', '/forgottenPassword'];
    const isPublicRoute = publicRoutes.some(route => currentRoute.startsWith(route));

    if (isTokenValid && isPublicRoute) {
      // User is logged in but on a public route, redirect to dashboard
      this.router.navigate(['/dashboard']);
    } else if (!isTokenValid && !isPublicRoute && currentRoute !== '/') {
      // User is not logged in and trying to access a protected route
      this.router.navigate(['/login']);
    }
  }
}
