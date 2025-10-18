import {Component, OnInit} from '@angular/core';
import {CustomerService} from '../../service/customer.service';
import {ToastModule} from 'primeng/toast';
import {ConfirmationService, MessageService} from 'primeng/api';
import {NavbarComponent} from '../../../_shared/components/navbar/navbar.component';
import {FormsModule} from '@angular/forms';
import {DialogModule} from 'primeng/dialog';
import {ButtonModule} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {CommonModule} from '@angular/common';
import {CustomerModel} from '../../model/customer.model';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {BillService} from "../../service/bill.service";
import {Router} from "@angular/router";
import {DataTableComponent, TableColumn, TableAction, ToolbarAction} from '../../../_shared/components/data-table/data-table.component';

@Component({
  selector: 'app-customer-list-v2',
  standalone: true,
  imports: [
    CommonModule,
    ToastModule,
    NavbarComponent,
    FormsModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    ConfirmDialogModule,
    DataTableComponent
  ],
  template: `
    <app-navbar></app-navbar>

    <app-data-table
      title="Gestion des Clients"
      subtitle="Gérez facilement vos clients et générez leurs factures"
      [data]="customers"
      [columns]="tableColumns"
      [actions]="rowActions"
      [toolbarActions]="toolbarActions"
      [selectedItems]="selectedCustomers"
      (selectedItemsChange)="selectedCustomers = $event"
      (toolbarActionClick)="onToolbarAction($event)"
      (rowActionClick)="onRowAction($event)"
      searchPlaceholder="Rechercher un client..."
      [globalFilterFields]="['name', 'telephone', 'address']"
      emptyMessage="Aucun client trouvé"
      emptySubMessage="Commencez par ajouter votre premier client">

      <!-- Dialog de création -->
      <p-dialog
        [(visible)]="saveCustomerDialog"
        [style]="{ width: '650px' }"
        header="Nouveau Client"
        [modal]="true"
        styleClass="p-fluid modern-dialog">

        <ng-template pTemplate="content">
          <div class="form-field">
            <label for="name">
              <i class="pi pi-user mr-2"></i>Nom complet
            </label>
            <input
              type="text"
              pInputText
              id="name"
              [(ngModel)]="customer.name"
              required
              autofocus
              placeholder="Entrez le nom du client"
            />
            <small class="p-error" *ngIf="submitted && !customer.name">
              Le nom doit être indiqué.
            </small>
          </div>

          <div class="form-field">
            <label for="telephone">
              <i class="pi pi-phone mr-2"></i>Numéro de téléphone
            </label>
            <input
              type="text"
              pInputText
              id="telephone"
              [(ngModel)]="customer.telephone"
              required
              placeholder="Ex: +237 6XX XX XX XX"
            />
            <small class="p-error" *ngIf="submitted && !customer.telephone">
              Veuillez saisir le numéro de téléphone.
            </small>
          </div>

          <div class="form-field">
            <label for="address">
              <i class="pi pi-map-marker mr-2"></i>Adresse
            </label>
            <input
              type="text"
              pInputText
              id="address"
              [(ngModel)]="customer.address"
              required
              placeholder="Entrez l'adresse complète"
            />
          </div>
        </ng-template>

        <ng-template pTemplate="footer">
          <div class="flex justify-content-end gap-2">
            <p-button
              label="Annuler"
              icon="pi pi-times"
              [outlined]="true"
              severity="secondary"
              class="modern-button"
              (onClick)="hideSaveDialog()"
            />
            <p-button
              label="Créer"
              icon="pi pi-check"
              severity="success"
              class="modern-button"
              (onClick)="saveCustomer()"
            />
          </div>
        </ng-template>
      </p-dialog>

      <!-- Dialog de modification -->
      <p-dialog
        [(visible)]="updateCustomerDialog"
        [style]="{ width: '650px' }"
        header="Modifier le Client"
        [modal]="true"
        styleClass="p-fluid modern-dialog">

        <ng-template pTemplate="content">
          <div class="form-field">
            <label for="edit-name">
              <i class="pi pi-user mr-2"></i>Nom complet
            </label>
            <input
              type="text"
              pInputText
              id="edit-name"
              [(ngModel)]="customer.name"
              required
              autofocus
              placeholder="Entrez le nom du client"
            />
            <small class="p-error" *ngIf="submitted && !customer.name">
              Le nom doit être indiqué.
            </small>
          </div>

          <div class="form-field">
            <label for="edit-telephone">
              <i class="pi pi-phone mr-2"></i>Numéro de téléphone
            </label>
            <input
              type="text"
              pInputText
              id="edit-telephone"
              [(ngModel)]="customer.telephone"
              required
              placeholder="Ex: +237 6XX XX XX XX"
            />
            <small class="p-error" *ngIf="submitted && !customer.telephone">
              Veuillez saisir le numéro de téléphone.
            </small>
          </div>

          <div class="form-field">
            <label for="edit-address">
              <i class="pi pi-map-marker mr-2"></i>Adresse
            </label>
            <input
              type="text"
              pInputText
              id="edit-address"
              [(ngModel)]="customer.address"
              required
              placeholder="Entrez l'adresse complète"
            />
          </div>
        </ng-template>

        <ng-template pTemplate="footer">
          <div class="flex justify-content-end gap-2">
            <p-button
              label="Annuler"
              icon="pi pi-times"
              [outlined]="true"
              severity="secondary"
              class="modern-button"
              (onClick)="hideUpdateDialog()"
            />
            <p-button
              label="Sauvegarder"
              icon="pi pi-check"
              severity="success"
              class="modern-button"
              (onClick)="editCustomer()"
            />
          </div>
        </ng-template>
      </p-dialog>

      <!-- Dialog de confirmation -->
      <p-confirmDialog #cd>
        <ng-template pTemplate="headless" let-message>
          <div class="custom-confirm-dialog flex flex-column align-items-center p-5">
            <div class="confirm-icon">
              <i class="pi pi-question text-4xl text-white"></i>
            </div>
            <span class="font-bold text-2xl block mb-2 mt-3 text-900">
            {{ message.header }}
          </span>
            <p class="mb-0 text-700 text-center px-3">{{ message.message }}</p>
            <div class="flex align-items-center confirm-buttons">
              <button
                pButton
                label="NON"
                (click)="cd.reject()"
                class="confirm-button p-button-outlined">
              </button>
              <button
                pButton
                label="OUI"
                (click)="cd.accept()"
                class="confirm-button">
              </button>
            </div>
          </div>
        </ng-template>
      </p-confirmDialog>
    </app-data-table>

    <p-toast key="br"/>
  `,
  styleUrls: ['./customer-list-v2.component.css'],
  providers: [MessageService, ConfirmationService],
})
export class CustomerListV2Component implements OnInit {
  saveCustomerDialog: boolean = false;
  updateCustomerDialog: boolean = false;
  submitted: boolean = false;
  isGenerateBillDialogOpen: boolean = false;

  customer!: CustomerModel;
  customers!: CustomerModel[];
  selectedCustomers: CustomerModel[] = [];
  generatedBills: any[] = [];

  // Configuration des colonnes de la table
  tableColumns: TableColumn[] = [
    {
      field: 'name',
      header: 'Nom',
      sortable: true,
      icon: 'pi-user',
      type: 'text'
    },
    {
      field: 'telephone',
      header: 'Téléphone',
      sortable: true,
      icon: 'pi-phone',
      type: 'text'
    },
    {
      field: 'address',
      header: 'Adresse',
      sortable: true,
      icon: 'pi-map-marker',
      type: 'text',
      width: '15rem'
    },
    {
      field: 'hasDebt',
      header: 'Impayé',
      sortable: true,
      icon: 'pi-exclamation-triangle',
      type: 'status',
      statusConfig: {
        trueLabel: 'Oui',
        falseLabel: 'Non',
        trueIcon: 'pi-check-circle',
        falseIcon: 'pi-times-circle'
      }
    },
    {
      field: 'isSuspended',
      header: 'Suspendu',
      sortable: true,
      icon: 'pi-ban',
      type: 'status',
      statusConfig: {
        trueLabel: 'Oui',
        falseLabel: 'Non',
        trueIcon: 'pi-check-circle',
        falseIcon: 'pi-times-circle'
      }
    }
  ];

  // Actions de ligne (boutons d'action pour chaque ligne)
  rowActions: TableAction[] = [
    {
      label: 'Modifier',
      icon: 'pi pi-pencil',
      severity: 'success',
      tooltip: 'Modifier le client',
      action: 'edit'
    },
    {
      label: 'Voir détails',
      icon: 'pi pi-eye',
      severity: 'info',
      tooltip: 'Voir les détails du client',
      action: 'view'
    },
    {
      label: 'Supprimer',
      icon: 'pi pi-trash',
      severity: 'danger',
      tooltip: 'Supprimer le client',
      action: 'delete'
    }
  ];

  // Actions de la barre d'outils
  toolbarActions: ToolbarAction[] = [
    {
      label: 'Nouveau Client',
      icon: 'pi pi-plus',
      severity: 'success',
      action: 'new'
    },
    {
      label: 'Supprimer',
      icon: 'pi pi-trash',
      severity: 'danger',
      action: 'deleteSelected',
      requiresSelection: true
    },
    {
      label: 'Générer Factures',
      icon: 'pi pi-receipt',
      severity: 'info',
      action: 'generateBills',
      requiresSelection: true
    }
  ];

  constructor(
    private customerService: CustomerService,
    private billService: BillService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.getCustomers();
  }

  // Gestion des actions de la barre d'outils
  onToolbarAction(event: {action: string, event?: Event}) {
    switch (event.action) {
      case 'new':
        this.openNew();
        break;
      case 'deleteSelected':
        this.deleteSelectedCustomers();
        break;
      case 'generateBills':
        this.showGenerateBill(event.event!);
        break;
    }
  }

  // Gestion des actions de ligne
  onRowAction(event: {action: string, item: any, index: number}) {
    switch (event.action) {
      case 'edit':
        this.openEdit(event.item);
        break;
      case 'view':
        this.moveToDetail(event.item.id);
        break;
      case 'delete':
        this.deleteCustomer(event.item);
        break;
    }
  }

  // Méthodes existantes (inchangées)
  showGenerateBill(event: Event) {
    if (this.isGenerateBillDialogOpen) {
      return;
    }

    this.isGenerateBillDialogOpen = true;

    this.confirmationService.confirm({
      target: event.target as EventTarget,
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
        this.isGenerateBillDialogOpen = false;
      },
      reject: () => {
        this.isGenerateBillDialogOpen = false;
      }
    });
  }

  generateBills() {
    const customerIds = this.selectedCustomers.map(customer => customer.id);
    if (customerIds.length > 0) {
      this.billService.generateBills(customerIds, true).subscribe({
        next: (bills) => {
          this.generatedBills = bills;
          this.messageService.add({severity: 'success', summary: 'Success', detail: 'Factures générées avec succès'});
        },
        error: (error) => {
          console.log(error)
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Une erreur interne s\'est produite. Si le problème persiste, veuillez contacter l\'administrateur',
            life: 5000
          });
        }
      });
    }
  }

  getCustomers() {
    this.customerService.getCustomers().subscribe({
      next: (res) => {
        console.log(res);
        this.customers = res.data;
      },
      error: (err) => {
        console.log(err);
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur de chargement',
          detail: `Une erreur s'est produite lors du chargement des données`,
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
      message: 'Êtes-vous sûr de vouloir supprimer les clients sélectionnés ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.customers = this.customers.filter(
          (val) => !this.selectedCustomers?.includes(val)
        );
        this.selectedCustomers = [];
        this.messageService.add({
          severity: 'success',
          summary: 'Succès',
          detail: 'Clients supprimés avec succès',
          life: 3000,
        });
      },
    });
  }

  moveToDetail(customerId: number) {
    this.router.navigate(['/customers', customerId, 'detail']).then(r => r);
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
    this.customerService.createCustomer(this.customer).subscribe({
      next: (_) => {
        this.getCustomers();
        this.messageService.add({
          severity: 'success',
          summary: 'Succès',
          detail: `Le client ${this.customer.name} a été créé avec succès`,
          life: 3000,
          key: 'br',
        });
        this.submitted = false;
        this.saveCustomerDialog = false;
      },
      error: (e) => {
        console.error('Erreur lors de la création du client', e)
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur de création',
          detail: `Une erreur s'est produite lors de la création du client`,
          life: 5000,
          key: 'br',
        });
      },
    });
  }

  editCustomer() {
    this.customerService.updateCustomer(this.customer).subscribe({
      next: (_) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Succès',
          detail: `Le client ${this.customer.name} a été modifié avec succès`,
          life: 3000,
          key: 'br',
        });
        this.updateCustomerDialog = false;
        this.getCustomers();
      },
      error: (_) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur de modification',
          detail: `Une erreur s'est produite lors de la modification`,
          life: 5000,
          key: 'br',
        });
      },
    });
  }

  deleteCustomer(customer: CustomerModel) {
    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer ' + customer.name?.toUpperCase() + ' et ses données ?',
      header: 'Confirmation de suppression',
      acceptButtonStyleClass: 'p-button-danger',
      acceptLabel: 'OUI',
      rejectLabel: 'NON',
      accept: () => {
        this.customerService.deleteCustomer(customer.id).subscribe({
          next: (_) => {
            this.customers = this.customers.filter((val) => val.id !== customer.id);
            this.customer = {};
            this.messageService.add({
              severity: 'success',
              summary: 'Succès',
              detail: 'Le client a été supprimé avec succès',
              life: 3000,
            });
            this.getCustomers()
          },
          error: (_) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Erreur',
              detail: 'Une erreur s\'est produite lors de la suppression',
              life: 3000,
            });
            this.getCustomers()
          }
        })
      },
    });
  }
}
