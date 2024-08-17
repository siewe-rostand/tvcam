import { Routes } from '@angular/router';
import { LoginComponent } from './auth/components/login/login.component';
import { SigninComponent } from './auth/components/signin/signin.component';
import { UsersListComponent } from './user/components/users-list/users-list.component';
import { AuthGuardService } from './auth/services/auth.guard';
import { CustomerListComponent } from './customers/components/customer-list/customer-list.component';
import {BillComponent} from "./customers/components/bill/bill.component";
import {PaymentComponent} from "./customers/components/payment/payment.component";
import {CustomerDetailComponent} from "./customers/components/customer-detail/customer-detail.component";

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
    path: 'customers/:id/detail',
    component: CustomerDetailComponent,
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
