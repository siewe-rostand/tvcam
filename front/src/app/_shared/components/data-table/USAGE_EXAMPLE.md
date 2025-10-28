# Data Table Component - Usage Guide

## How to Use Custom Cell Templates

The `app-data-table` component now supports custom templates for any column. This allows you to render custom components (like `<app-payment-status>`) inside table cells.

### Step 1: Define Your Column Configuration

In your component TypeScript file, define columns with `type: 'custom'` for columns that need custom rendering:

```typescript
import { TableColumn } from '@app/_shared/components/data-table/data-table.component';

columns: TableColumn[] = [
  { field: 'paymentReference', header: 'Reference', sortable: true, type: 'text' },
  { field: 'customerName', header: 'Client', sortable: true, type: 'text' },
  { field: 'month', header: 'Mois', sortable: true, type: 'text' },
  { field: 'paymentAmount', header: 'Montant', sortable: true, type: 'text' },
  { field: 'paymentStatus', header: 'Statut', type: 'custom' },  // Custom template
  { field: 'paymentDate', header: 'Date', sortable: true, type: 'text' }
];
```

### Step 2: Create Template References in Your Component

In your component TypeScript file, use `@ViewChild` to get references to your templates:

```typescript
import { Component, ViewChild, TemplateRef } from '@angular/core';

export class YourComponent {
  @ViewChild('statusTemplate') statusTemplate!: TemplateRef<any>;
  
  // This will hold the mapping of field names to templates
  cellTemplates: { [key: string]: TemplateRef<any> } = {};
  
  ngAfterViewInit() {
    // Map the field name to the template
    this.cellTemplates = {
      'paymentStatus': this.statusTemplate
    };
  }
}
```

### Step 3: Define Templates in Your HTML

In your component HTML file:

```html
<!-- Define the template that will be used for the custom column -->
<ng-template #statusTemplate let-payment let-rowIndex="rowIndex">
  <app-payment-status [status]="payment.paymentStatus"></app-payment-status>
</ng-template>

<!-- Use the data-table component -->
<app-data-table
  [data]="payments"
  [columns]="columns"
  [cellTemplates]="cellTemplates"
  [actions]="actions"
  [rows]="10"
  [paginator]="true"
  [selectionMode]="'multiple'"
  [(selectedItems)]="selectedPayments"
  (rowActionClick)="onAction($event)">
</app-data-table>
```

## Complete Example: Payment Table

### Component TypeScript (payment-list.component.ts)

```typescript
import { Component, ViewChild, TemplateRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataTableComponent, TableColumn, TableAction } from '@app/_shared/components/data-table/data-table.component';
import { PaymentStatusComponent } from './payment-status/payment-status.component';

@Component({
  selector: 'app-payment-list',
  standalone: true,
  imports: [
    CommonModule,
    DataTableComponent,
    PaymentStatusComponent
  ],
  templateUrl: './payment-list.component.html'
})
export class PaymentListComponent implements AfterViewInit {
  @ViewChild('statusTemplate') statusTemplate!: TemplateRef<any>;
  @ViewChild('frequencyTemplate') frequencyTemplate!: TemplateRef<any>;
  
  payments: any[] = [];
  selectedPayments: any[] = [];
  cellTemplates: { [key: string]: TemplateRef<any> } = {};
  
  columns: TableColumn[] = [
    { field: 'paymentReference', header: 'Reference', sortable: true, icon: 'pi-file-o' },
    { field: 'customerName', header: 'Client', sortable: true, icon: 'pi-user' },
    { field: 'month', header: 'Mois', sortable: true, icon: 'pi-calendar' },
    { field: 'paymentAmount', header: 'Montant', sortable: true, icon: 'pi-money-bill' },
    { field: 'paymentStatus', header: 'Statut', type: 'custom' },
    { field: 'paymentDate', header: 'Date', sortable: true, icon: 'pi-clock' }
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
      severity: 'warning',
      tooltip: 'Edit payment',
      action: 'edit'
    }
  ];
  
  ngAfterViewInit() {
    // Map templates after view initialization
    this.cellTemplates = {
      'paymentStatus': this.statusTemplate
    };
  }
  
  onAction(event: { action: string, item: any, index: number }) {
    if (event.action === 'view') {
      this.viewPayment(event.item);
    } else if (event.action === 'edit') {
      this.editPayment(event.item);
    }
  }
  
  viewPayment(payment: any) {
    console.log('View payment:', payment);
  }
  
  editPayment(payment: any) {
    console.log('Edit payment:', payment);
  }
}
```

### Component HTML (payment-list.component.html)

```html
<!-- Define custom templates -->
<ng-template #statusTemplate let-payment>
  <app-payment-status [status]="payment.paymentStatus"></app-payment-status>
</ng-template>

<!-- Use the reusable data table -->
<app-data-table
  [data]="payments"
  [columns]="columns"
  [cellTemplates]="cellTemplates"
  [actions]="actions"
  [rows]="10"
  [rowsPerPageOptions]="[10, 25, 50]"
  [paginator]="true"
  [showSearch]="true"
  [searchPlaceholder]="'Rechercher un paiement...'"
  [globalFilterFields]="['paymentReference', 'customerName', 'month']"
  [selectionMode]="'multiple'"
  [dataKey]="'id'"
  [(selectedItems)]="selectedPayments"
  (rowActionClick)="onAction($event)"
  [emptyMessage]="'Aucun paiement trouvé'"
  [emptySubMessage]="'Il n\'y a actuellement aucun paiement à afficher'">
</app-data-table>
```

## Multiple Custom Columns Example

If you have multiple custom columns:

```typescript
// In your component
@ViewChild('statusTemplate') statusTemplate!: TemplateRef<any>;
@ViewChild('frequencyTemplate') frequencyTemplate!: TemplateRef<any>;
@ViewChild('amountTemplate') amountTemplate!: TemplateRef<any>;

columns: TableColumn[] = [
  { field: 'customerName', header: 'Client', type: 'text' },
  { field: 'paymentStatus', header: 'Statut', type: 'custom' },
  { field: 'frequency', header: 'Fréquence', type: 'custom' },
  { field: 'amount', header: 'Montant', type: 'custom' }
];

ngAfterViewInit() {
  this.cellTemplates = {
    'paymentStatus': this.statusTemplate,
    'frequency': this.frequencyTemplate,
    'amount': this.amountTemplate
  };
}
```

```html
<ng-template #statusTemplate let-payment>
  <app-payment-status [status]="payment.paymentStatus"></app-payment-status>
</ng-template>

<ng-template #frequencyTemplate let-payment>
  <app-customer-payment-frequency [frequency]="payment.frequency"></app-customer-payment-frequency>
</ng-template>

<ng-template #amountTemplate let-payment>
  <span class="font-bold text-green-600">{{ payment.amount | currency:'XAF' }}</span>
</ng-template>

<app-data-table
  [data]="payments"
  [columns]="columns"
  [cellTemplates]="cellTemplates">
</app-data-table>
```

## Template Context

The template receives two variables:
- `$implicit` (default): The row data object
- `rowIndex`: The index of the row

You can access them like this:

```html
<ng-template #myTemplate let-rowData let-i="rowIndex">
  <div>Row {{ i }}: {{ rowData.someField }}</div>
  <app-custom-component [data]="rowData"></app-custom-component>
</ng-template>
```

## Benefits

✅ **Reusable**: One table component for all your tables
✅ **Flexible**: Support for text, status, and custom columns
✅ **Type-safe**: TypeScript interfaces for columns and actions
✅ **Feature-rich**: Sorting, pagination, selection, search, actions
✅ **Customizable**: Pass your own components for any column

