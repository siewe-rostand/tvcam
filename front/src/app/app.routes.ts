import { Routes } from '@angular/router';
import { LoginComponent } from './auth/components/login/login.component';
import { RegistrationComponent } from './auth/components/registration/registration.component';
import { UsersListComponent } from './user/components/users-list/users-list.component';
import { CustomerListComponent } from './customers/components/customer-list/customer-list.component';
import { BillComponent } from "./customers/components/bill/bill.component";
import { PaymentComponent } from "./customers/components/payment/payment.component";
import { CustomerDetailComponent } from "./customers/components/customer-detail/customer-detail.component";
import { ForgottenPasswordComponent } from "./auth/components/forgotten-password/forgotten-password.component";
import { BillPrintComponent } from "./customers/components/bill/bill-print/bill-print.component";
import { HomeComponent } from "./home/home.component";
import { AuthGuardService } from "./auth/services/auth-guard.service";

export const routes: Routes = [
  // Public routes (no authentication required)
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: RegistrationComponent },
  { path: 'forgottenPassword', component: ForgottenPasswordComponent },

  // Protected routes (authentication required)
  {
    path: 'dashboard',
    component: HomeComponent,
    canActivate: [AuthGuardService],
  },

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
    path: 'receipts/generate',
    component: BillPrintComponent,
    canActivate: [AuthGuardService],
  },

  {
    path: 'payment',
    component: PaymentComponent,
    canActivate: [AuthGuardService],
  },

  {
    path: 'home',
    component: HomeComponent,
    canActivate: [AuthGuardService],
  },

  // Default route - redirect based on authentication status
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },

  // Wildcard route - redirect to login for unknown paths
  { path: '**', redirectTo: '/login' }
];
