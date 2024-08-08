import { Routes } from '@angular/router';
import { LoginComponent } from './auth/components/login/login.component';
import { SigninComponent } from './auth/components/signin/signin.component';
import { UsersListComponent } from './user/components/users-list/users-list.component';
import { AuthGuardService } from './auth/services/auth.guard';
import { CustomerListComponent } from './customers/components/customer-list/customer-list.component';
import {CustomerBillComponent} from "./customers/components/customer-bill/customer-bill.component";
import {BillPaymentComponent} from "./customers/components/bill-payment/bill-payment.component";

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
    component: CustomerBillComponent,
    canActivate: [AuthGuardService],
  },
  {
    path: 'payment',
    component: BillPaymentComponent,
    canActivate: [AuthGuardService],
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];
