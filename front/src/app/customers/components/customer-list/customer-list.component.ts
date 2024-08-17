import {Component, OnInit} from '@angular/core';
import {CustomerService} from '../../service/customer.service';
import {ToastModule} from 'primeng/toast';
import {ConfirmationService, MessageService} from 'primeng/api';
import {NavbarComponent} from '../../../_shared/components/navbar/navbar.component';
import {FormsModule} from '@angular/forms';
import {RippleModule} from 'primeng/ripple';
import {TableModule} from 'primeng/table';
import {DialogModule} from 'primeng/dialog';
import {ButtonModule} from 'primeng/button';
import {ToolbarModule} from 'primeng/toolbar';
import {InputTextModule} from 'primeng/inputtext';
import {InputNumberModule} from 'primeng/inputnumber';
import {CommonModule} from '@angular/common';
import {CustomerModel} from '../../model/customer.model';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {BillService} from "../../service/bill.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-customer-list',
  standalone: true,
  imports: [
    CommonModule,
    ToastModule,
    NavbarComponent,
    FormsModule,
    RippleModule,
    TableModule,
    DialogModule,
    ButtonModule,
    ToastModule,
    ToolbarModule,
    InputTextModule,
    InputNumberModule,
    ConfirmDialogModule,
  ],
  templateUrl: './customer-list.component.html',
  styleUrl: './customer-list.component.css',
  providers: [MessageService, ConfirmationService],
})
export class CustomerListComponent implements OnInit {
  saveCustomerDialog: boolean = false;
  updateCustomerDialog: boolean = false;
  submitted: boolean = false;

  customer!: CustomerModel;

  customers!: CustomerModel[];
  selectedCustomers!: CustomerModel[] | null;
  generatedBills: any[] = [];

  constructor(
    private customerService: CustomerService, private billService: BillService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.getCustomers();
  }

  onSelectionChange(event: any) {
    this.selectedCustomers = event;
  }

  showGenerateBill() {
    this.confirmationService.confirm({
      header: 'Confirmation',
      message: 'Voulez-vous générer les factures des clients sélectionnés ?',
      acceptIcon: 'pi pi-check mr-2',
      rejectIcon: 'pi pi-times mr-2',
      rejectButtonStyleClass: 'p-button-sm',
      acceptButtonStyleClass: 'p-button-outlined p-button-sm',
      acceptLabel: 'OUI',
      rejectLabel: 'NON',
      accept: () => {
        this.generateBills();
      },
    });
  }

  generateBills() {
    // @ts-ignore
    const customerIds = this.selectedCustomers.map(customer => customer.id);
    // @ts-ignore
    this.billService.generateBills(customerIds).subscribe({
        next: (bills) => {
          this.generatedBills = bills;
          this.messageService.add({severity: 'success', summary: 'Success', detail: 'factures générées avec succès'});
        },
        error: (error) => {
          console.log(error)
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'une erreur interne s\'est produite. Si le problème persiste, veuillez contacter l\'administrateur',
            life: 5000
          });
        }
      }
    );
  }

  getCustomers() {
    this.customerService.getCustomers().subscribe({
      next: (res) => {
        console.log(res);
        this.customers = res.data;
        this.messageService.add({
          severity: 'success',
          summary: 'Successful',
          detail: `les données de l'utilisateur ont été mises à jour avec succès`,
          life: 3000,
          key: 'br',
        });
      },
      error: (err) => {
        console.log(err);
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur de mise à jour',
          detail: `une erreur s'est produite lors de la mise à jour des données`,
          life: 5000,
          key: 'br',
        });
      },
    });
  }

  openNew() {
    this.customer = {};
    this.submitted = false;
    this.saveCustomerDialog = true;
  }

  deleteSelectedCustomers() {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete the selected products?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.customers = this.customers.filter(
          (val) => !this.selectedCustomers?.includes(val)
        );
        this.selectedCustomers = null;
        this.messageService.add({
          severity: 'success',
          summary: 'Successful',
          detail: 'Products Deleted',
          life: 3000,
        });
      },
    });
  }

  moveToDetail(customerId: number) {
    this.router.navigate(['/customers', customerId,'detail']);
  }
  openEdit(customer: CustomerModel) {
    this.customer = {...customer};
    this.updateCustomerDialog = true;
    console.log(customer);
  }

  hideSaveDialog() {
    this.saveCustomerDialog = false;
    this.submitted = false;
  }

  hideUpdateDialog() {
    this.updateCustomerDialog = false;
    this.submitted = false;
  }

  saveCustomer() {
    this.submitted = true;
    console.log(this.customer);
    this.customerService.createCustomer(this.customer).subscribe({
      next: (res) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Successful',
          detail: `les données de l'utilisateur ${this.customer.name} ont été sauvegarder avec succès`,
          life: 3000,
          key: 'br',
        });
        this.submitted = false;
        this.saveCustomerDialog = false;
      },
      error: (err) => {
        console.log(err);
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur de mise à jour',
          detail: `une erreur s'est produite lors de la mise à jour des données`,
          life: 5000,
          key: 'br',
        });
      },
    });
    // this.userDialog = false;
  }

  editCustomer() {
    this.customerService.updateCustomer(this.customer).subscribe({
      next: (res) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Successful',
          detail: `les données de l'utilisateur ${this.customer.name} ont été mises à jour avec succès`,
          life: 3000,
          key: 'br',
        });
        this.updateCustomerDialog = false;
      },
      error: (err) => {
        console.log(err);
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur de mise à jour',
          detail: `une erreur s'est produite lors de la mise à jour des données`,
          life: 5000,
          key: 'br',
        });
      },
    });
  }

  deleteCustomer(customer: CustomerModel) {
    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer  ' + customer.name?.toUpperCase() + ' ses données ?',
      header: 'Confirm',
      acceptButtonStyleClass: 'p-button-danger',
      acceptLabel: 'OUI',
      accept: () => {
        this.customerService.deleteCustomer(customer.id).subscribe({
          next: (res) => {
            this.customers = this.customers.filter((val) => val.id !== customer.id);
            this.customer = {};
            this.messageService.add({
              severity: 'success',
              summary: 'Successful',
              detail: 'le client a été supprimé avec succès',
              life: 3000,
            });
          },
          error: (err) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: 'Une erreur s\'est produite lors de la suppression',
              life: 3000,
            });
          }
        })
      },
    });
  }
}
