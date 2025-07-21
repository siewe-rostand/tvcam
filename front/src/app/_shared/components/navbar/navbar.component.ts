import {CommonModule, NgOptimizedImage} from '@angular/common';
import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {ConfirmationService, MenuItem, MessageService} from 'primeng/api';
import {AvatarModule} from 'primeng/avatar';
import {MenubarModule} from 'primeng/menubar';
import {RippleModule} from "primeng/ripple";
import {FormsModule} from '@angular/forms';
import {filter} from "rxjs";
import {StorageService} from '../../services/storage.service';
import {UserModel} from "../../../user/model/user.model";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {ToastModule} from "primeng/toast";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, AvatarModule, MenubarModule, RippleModule,
    FormsModule, NgOptimizedImage, ConfirmDialogModule, ToastModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  providers: [ConfirmationService, MessageService]
})
export class NavbarComponent implements OnInit {
  items: MenuItem[] = [];
  activeItem: MenuItem | undefined;
  user: UserModel = {};
  notificationCount: number = 3;

  constructor(
    private router: Router,
    private storageService: StorageService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) {
  }

  ngOnInit(): void {
    this.user = this.storageService.getUser();
    /// navigation menu items
    this.items = [
      {
        label: 'Dashboard',
        icon: 'pi pi-home',
        routerLink: ['/dashboard']
      },
      {
        label: 'Utilisateur',
        icon: 'pi pi-user',
        routerLink: ['/users'],
      },
      {
        label: 'Client',
        icon: 'pi pi-users',
        routerLink: ['/customers']
      },
      {
        label: 'Factures',
        icon: 'pi pi-receipt',
        routerLink: ['/receipts'],
      },
      {
        label: 'Paiements',
        icon: 'pi pi-dollar',
        routerLink: ['/payment'],
      },
    ];
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.setActiveItem();
    });

    this.setActiveItem();
  }

  setActiveItem(): void {
    const currentRoute = this.router.url;
    this.activeItem = this.items.find(item =>
      item.routerLink && item.routerLink[0] === currentRoute
    );
  }

  logout() {
    this.confirmationService.confirm({
      message: 'Are you sure you want to logout? You will need to sign in again to access your account.',
      header: 'Confirm Logout',
      icon: 'pi pi-sign-out',
      acceptButtonStyleClass: 'p-button-danger p-button-outlined',
      rejectButtonStyleClass: 'p-button-secondary p-button-outlined',
      acceptLabel: 'Yes, Logout',
      rejectLabel: 'Cancel',
      acceptIcon: 'pi pi-check',
      rejectIcon: 'pi pi-times',

      accept: () => {
        // User confirmed logout
        this.performLogout();
      },
      reject: () => {
        // User cancelled logout - show friendly message
        this.messageService.add({
          severity: 'info',
          summary: 'Logout Cancelled',
          detail: 'You are still logged in.',
          life: 3000
        });
        console.log('Logout cancelled by user');
      },
    });
  }

  private performLogout(): void {
    try {
      // Show logout in progress message
      this.messageService.add({
        severity: 'success',
        summary: 'Logging out',
        detail: 'Please wait while we log you out safely...',
        life: 2000
      });

      // Clear user data and tokens
      this.storageService.logout();

      // Navigate to login page
      this.router.navigate(['/login']).then(() => {
        console.log('User successfully logged out');
      });
    } catch (error) {
      console.error('Error during logout:', error);

      // Show error message
      this.messageService.add({
        severity: 'error',
        summary: 'Logout Error',
        detail: 'There was an issue logging you out. Redirecting to login...',
        life: 3000
      });

      // Still navigate to log in even if there's an error
      this.router.navigate(['/login']).then(() => true);
    }
  }
}
