import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { DataTableComponent, TableColumn, TableAction } from '../data-table.component';
import { PageToolbarComponent, ToolbarAction } from '../../page-toolbar/page-toolbar.component';

interface User {
  id: number;
  name: string;
  email: string;
  phone: string;
  role: string;
  active: boolean;
  joinDate: string;
}

@Component({
  selector: 'app-users-example',
  standalone: true,
  imports: [CommonModule, DataTableComponent, PageToolbarComponent, ToastModule],
  providers: [MessageService],
  template: `
    <!-- Toolbar Component (Optional - use only where needed) -->
    <app-page-toolbar
      title="Users Management"
      subtitle="Manage all users in the system"
      [actions]="toolbarActions"
      [selectedItemsCount]="selectedUsers.length"
      (actionClick)="onToolbarAction($event)"
    >
    </app-page-toolbar>

    <!-- Table Component (Reusable everywhere) -->
    <app-data-table
      [data]="users"
      [columns]="columns"
      [actions]="actions"
      [rows]="10"
      [paginator]="true"
      [showSearch]="true"
      searchPlaceholder="Search users by name, email or phone..."
      [(selectedItems)]="selectedUsers"
      (rowActionClick)="onRowAction($event)"
      (selectionChange)="onSelectionChange($event)"
    >
    </app-data-table>

    <p-toast></p-toast>
  `
})
export class UsersExampleComponent implements OnInit {
  users: User[] = [];
  selectedUsers: User[] = [];

  columns: TableColumn[] = [
    {
      field: 'id',
      header: 'ID',
      sortable: true,
      width: '100px',
      type: 'text'
    },
    {
      field: 'name',
      header: 'Name',
      sortable: true,
      icon: 'pi-user',
      type: 'text'
    },
    {
      field: 'email',
      header: 'Email',
      sortable: true,
      icon: 'pi-envelope',
      type: 'text'
    },
    {
      field: 'phone',
      header: 'Phone',
      sortable: false,
      icon: 'pi-phone',
      type: 'text'
    },
    {
      field: 'role',
      header: 'Role',
      sortable: true,
      type: 'text'
    },
    {
      field: 'active',
      header: 'Status',
      sortable: true,
      type: 'status',
      statusConfig: {
        trueLabel: 'Active',
        falseLabel: 'Inactive',
        trueIcon: 'pi-check-circle',
        falseIcon: 'pi-times-circle'
      }
    },
    {
      field: 'joinDate',
      header: 'Join Date',
      sortable: true,
      type: 'text'
    }
  ];

  actions: TableAction[] = [
    {
      label: 'View',
      icon: 'pi pi-eye',
      severity: 'info',
      tooltip: 'View details',
      action: 'view'
    },
    {
      label: 'Edit',
      icon: 'pi pi-pencil',
      severity: 'success',
      tooltip: 'Edit user',
      action: 'edit'
    },
    {
      label: 'Delete',
      icon: 'pi pi-trash',
      severity: 'danger',
      tooltip: 'Delete user',
      action: 'delete',
      disabled: (user: User) => user.role === 'admin' // Can't delete admins
    }
  ];

  toolbarActions: ToolbarAction[] = [
    {
      label: 'New User',
      icon: 'pi pi-plus',
      severity: 'success',
      action: 'create'
    },
    {
      label: 'Export',
      icon: 'pi pi-download',
      severity: 'info',
      action: 'export'
    },
    {
      label: 'Delete Selected',
      icon: 'pi pi-trash',
      severity: 'danger',
      action: 'delete-multiple',
      requiresSelection: true
    }
  ];

  constructor(private messageService: MessageService) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    // Simulate API call
    this.users = [
      {
        id: 1,
        name: 'John Doe',
        email: 'john.doe@example.com',
        phone: '+1 234 567 8900',
        role: 'admin',
        active: true,
        joinDate: '2024-01-15'
      },
      {
        id: 2,
        name: 'Jane Smith',
        email: 'jane.smith@example.com',
        phone: '+1 234 567 8901',
        role: 'user',
        active: true,
        joinDate: '2024-02-20'
      },
      {
        id: 3,
        name: 'Bob Johnson',
        email: 'bob.johnson@example.com',
        phone: '+1 234 567 8902',
        role: 'user',
        active: false,
        joinDate: '2024-03-10'
      },
      {
        id: 4,
        name: 'Alice Williams',
        email: 'alice.williams@example.com',
        phone: '+1 234 567 8903',
        role: 'moderator',
        active: true,
        joinDate: '2024-04-05'
      },
      {
        id: 5,
        name: 'Charlie Brown',
        email: 'charlie.brown@example.com',
        phone: '+1 234 567 8904',
        role: 'user',
        active: true,
        joinDate: '2024-05-12'
      }
    ];
  }

  onToolbarAction(event: {action: string, event?: Event}) {
    switch(event.action) {
      case 'create':
        this.createUser();
        break;
      case 'export':
        this.exportUsers();
        break;
      case 'delete-multiple':
        this.deleteMultipleUsers();
        break;
    }
  }

  onRowAction(event: {action: string, item: User, index: number}) {
    switch(event.action) {
      case 'view':
        this.viewUser(event.item);
        break;
      case 'edit':
        this.editUser(event.item);
        break;
      case 'delete':
        this.deleteUser(event.item);
        break;
    }
  }

  onSelectionChange(selected: User[]) {
    console.log('Selected users:', selected);
  }

  createUser() {
    this.messageService.add({
      severity: 'info',
      summary: 'Create User',
      detail: 'Opening create user dialog...'
    });
    // Implement your create logic here
  }

  viewUser(user: User) {
    this.messageService.add({
      severity: 'info',
      summary: 'View User',
      detail: `Viewing details for ${user.name}`
    });
    // Implement your view logic here
  }

  editUser(user: User) {
    this.messageService.add({
      severity: 'info',
      summary: 'Edit User',
      detail: `Editing ${user.name}`
    });
    // Implement your edit logic here
  }

  deleteUser(user: User) {
    this.messageService.add({
      severity: 'warn',
      summary: 'Delete User',
      detail: `Deleting ${user.name}...`
    });
    // Implement your delete logic here
    setTimeout(() => {
      this.users = this.users.filter(u => u.id !== user.id);
      this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: 'User deleted successfully'
      });
    }, 500);
  }

  exportUsers() {
    this.messageService.add({
      severity: 'info',
      summary: 'Export',
      detail: 'Exporting users to CSV...'
    });
    // Implement your export logic here
  }

  deleteMultipleUsers() {
    if (this.selectedUsers.length === 0) return;

    this.messageService.add({
      severity: 'warn',
      summary: 'Delete Multiple',
      detail: `Deleting ${this.selectedUsers.length} users...`
    });

    // Implement your bulk delete logic here
    setTimeout(() => {
      const selectedIds = this.selectedUsers.map(u => u.id);
      this.users = this.users.filter(u => !selectedIds.includes(u.id));
      this.selectedUsers = [];

      this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Users deleted successfully'
      });
    }, 500);
  }
}
