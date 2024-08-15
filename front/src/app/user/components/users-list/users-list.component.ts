import { Component, OnInit } from '@angular/core';
import { TableModule } from 'primeng/table';
import { CommonModule } from '@angular/common';
import { RippleModule } from 'primeng/ripple';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { ToolbarModule } from 'primeng/toolbar';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { DropdownModule } from 'primeng/dropdown';
import { TagModule } from 'primeng/tag';
import { RadioButtonModule } from 'primeng/radiobutton';
import { InputNumberModule } from 'primeng/inputnumber';
import { UserModel } from '../../model/user.model';
import { UserService } from '../../services/user.service';
import { FormsModule } from '@angular/forms';
import { FileUploadModule } from 'primeng/fileupload';
import { NavbarComponent } from '../../../_shared/components/navbar/navbar.component';

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RippleModule,
    TableModule,
    DialogModule,
    ButtonModule,
    ToastModule,
    ToolbarModule,
    InputTextModule,
    InputNumberModule,
    InputTextareaModule,
    DropdownModule,
    RadioButtonModule,
    ConfirmDialogModule,
    TagModule,
    FileUploadModule,
    NavbarComponent,
  ],
  templateUrl: './users-list.component.html',
  styleUrl: './users-list.component.css',
  providers: [MessageService, ConfirmationService],
})
export class UsersListComponent implements OnInit {
  userUpdateDialog: boolean = false;
  userSaveDialog: boolean = false;

  users!: UserModel[];

  user!: UserModel;

  selectedUser!: UserModel[] | null;

  submitted: boolean = false;

  constructor(
    private userService: UserService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
   this.getAllUsers();
  }

  getAllUsers(){
    this.userService.getUsers().subscribe({
      next: (response) => {
        console.log(response);
        this.users = response.data;
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  openNew() {
    this.user = {};
    this.submitted = false;
    this.userSaveDialog = true;
  }

  deleteSelectedUsers() {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete the selected products?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.users = this.users.filter(
          (val) => !this.selectedUser?.includes(val)
        );
        this.selectedUser = null;
        this.messageService.add({
          severity: 'success',
          summary: 'Successful',
          detail: 'Products Deleted',
          life: 3000,
        });
      },
    });
  }

  openEdit(user: UserModel) {
    this.user = { ...user };
    this.userUpdateDialog = true;
    console.log(user);
  }

  editUser() {
    this.userService.updateUser(this.user).subscribe({
      next: (res) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Successful',
          detail: `les données de l'utilisateur ${this.user.firstname} ont été mises à jour avec succès`,
          life: 3000,
          key: 'br',
        });
        this.userUpdateDialog = false;
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

  deleteUser(user: UserModel) {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete ' + user.firstname + '?',
      header: 'Confirm',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.users = this.users.filter((val) => val.id !== user.id);
        this.user = {};
        this.messageService.add({
          severity: 'success',
          summary: 'Successful',
          detail: 'Product Deleted',
          life: 3000,
        });
      },
    });
  }

  hideSaveDialog() {
    this.userSaveDialog = false;
    this.submitted = false;
  }

  hideUpdateDialog() {
    this.userUpdateDialog = false;
    this.submitted = false;
  }

  saveUser() {
    this.submitted = true;
    console.log(this.user);
    this.userService.saveUser(this.user).subscribe({
      next: (res) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Successful',
          detail: `les données de l'utilisateur ${this.user.firstname} ont été mises à jour avec succès`,
          life: 3000,
          key: 'br',
        });
        this.submitted = false;
        this.userSaveDialog = false;
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

  findIndexById(id: Number): number {
    let index = -1;
    for (let i = 0; i < this.users.length; i++) {
      if (this.users[i].id === id) {
        index = i;
        break;
      }
    }

    return index;
  }

  createId(): number {
    let id = '';
    var chars =
      'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    for (var i = 0; i < 5; i++) {
      id += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return Number(id);
  }
}
