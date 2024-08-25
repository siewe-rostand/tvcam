import {CommonModule} from '@angular/common';
import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {MenuItem} from 'primeng/api';
import {AvatarModule} from 'primeng/avatar';
import {MenubarModule} from 'primeng/menubar';
import {RippleModule} from "primeng/ripple";
import {filter} from "rxjs";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, AvatarModule, MenubarModule,RippleModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent implements OnInit {
  items: MenuItem[] = [];
  activeItem: MenuItem = {};

  constructor(private router: Router) {}

  ngOnInit(): void {
    /// navigation menu items
    this.items = [
      {
        label: 'Home',
        icon: 'pi pi-home',
        routerLink:['/']
      },
      {
        label: 'Utilisateur',
        icon: 'pi pi-user',
        routerLink:['/users'],
      },
      {
        label: 'Client',
        icon: 'pi pi-user',
        routerLink: ['/customers']
      },
      {
        label: 'Factures',
        icon: 'pi pi-receipt',
        routerLink:['/receipts'],
      },
      {
        label: 'Paiements',
        icon: 'pi pi-dollar',
        routerLink:['/payment'],
      },
    ];
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.setActiveItem();
    });

    this.setActiveItem();
  }

  setActiveItem() {
    const currentRoute = this.router.url;
    this.activeItem = this.items.find(item => item.routerLink[0] === currentRoute) || this.items[0];
  }
}
