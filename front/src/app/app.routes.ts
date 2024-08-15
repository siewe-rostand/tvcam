import { Routes } from '@angular/router';
import { LoginComponent } from './auth/components/login/login.component';
import { SigninComponent } from './auth/components/signin/signin.component';
import { UsersListComponent } from './user/components/users-list/users-list.component';
import { AuthGuardService } from './auth/services/auth.guard';
import { CustomerListComponent } from './customers/components/customer-list/customer-list.component';
import {BillComponent} from "./customers/components/customer-bill/bill.component";
import {PaymentComponent} from "./customers/components/payment/payment.component";

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'signin', component: SigninComponent },
  {
    path: 'users',
    component: UsersListComponent,
    canActivate: [AuthGuardService],
  },
  {
    path: 'customers',
    component: CustomerListComponent,
    canActivate: [AuthGuardService],
  },
  {
    path: 'receipts',
    component: BillComponent,
    canActivate: [AuthGuardService],
  },
  {
    path: 'payment',
    component: PaymentComponent,
    canActivate: [AuthGuardService],
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];
