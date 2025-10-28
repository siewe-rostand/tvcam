# DataTable Component - Usage Guide

## Overview
A fully reusable, modern data table component built with PrimeNG. This component provides a flexible and feature-rich table with sorting, filtering, pagination, selection, and custom actions.

## Features
- ✅ Sorting and filtering
- ✅ Pagination with customizable rows per page
- ✅ Single and multiple row selection
- ✅ Custom column types (text, status, custom)
- ✅ Row actions with tooltips
- ✅ Toolbar actions
- ✅ Global search
- ✅ Empty state
- ✅ Modern, gradient-based styling

## Basic Usage

### 1. Import the Component
```typescript
import { DataTableComponent, TableColumn, TableAction } from '@app/_shared/components/data-table/data-table.component';

@Component({
  standalone: true,
  imports: [DataTableComponent, ...]
})
```

### 2. Define Your Columns
```typescript
columns: TableColumn[] = [
  { 
    field: 'id', 
    header: 'ID', 
    sortable: true, 
    width: '100px' 
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
    icon: 'pi-envelope' 
  },
  { 
    field: 'active', 
    header: 'Status', 
    type: 'status',
    statusConfig: {
      trueLabel: 'Active',
      falseLabel: 'Inactive',
      trueIcon: 'pi-check-circle',
      falseIcon: 'pi-times-circle'
    }
  }
];
```

### 3. Define Row Actions
```typescript
actions: TableAction[] = [
  {
    label: 'Edit',
    icon: 'pi pi-pencil',
    severity: 'info',
    tooltip: 'Edit item',
    action: 'edit'
  },
  {
    label: 'Delete',
    icon: 'pi pi-trash',
    severity: 'danger',
    tooltip: 'Delete item',
    action: 'delete',
    disabled: (item) => item.protected // Optional: disable based on condition
  }
];
```

### 4. Define Toolbar Actions
```typescript
toolbarActions: ToolbarAction[] = [
  {
    label: 'New',
    icon: 'pi pi-plus',
    severity: 'success',
    action: 'create'
  },
  {
    label: 'Delete Selected',
    icon: 'pi pi-trash',
    severity: 'danger',
    action: 'delete-multiple',
    requiresSelection: true // Disabled when no items selected
  }
];
```

### 5. Use in Template
```html
<app-data-table
  title="Users Management"
  subtitle="Manage all users in the system"
  [data]="users"
  [columns]="columns"
  [actions]="actions"
  [toolbarActions]="toolbarActions"
  [rows]="10"
  [paginator]="true"
  [showSearch]="true"
  searchPlaceholder="Search users..."
  [(selectedItems)]="selectedUsers"
  (toolbarActionClick)="onToolbarAction($event)"
  (rowActionClick)="onRowAction($event)"
  (selectionChange)="onSelectionChange($event)"
>
</app-data-table>
```

### 6. Handle Events
```typescript
onToolbarAction(event: {action: string, event?: Event}) {
  switch(event.action) {
    case 'create':
      this.openCreateDialog();
      break;
    case 'delete-multiple':
      this.deleteMultiple();
      break;
  }
}

onRowAction(event: {action: string, item: any, index: number}) {
  switch(event.action) {
    case 'edit':
      this.editItem(event.item);
      break;
    case 'delete':
      this.deleteItem(event.item);
      break;
  }
}

onSelectionChange(selected: any[]) {
  console.log('Selected items:', selected);
}
```

## Advanced Usage

### Custom Column Content
For complex cell content, use the 'custom' type and ng-content:

```typescript
// In columns definition
{ 
  field: 'avatar', 
  header: 'Avatar', 
  type: 'custom' 
}
```

```html
<app-data-table [columns]="columns" [data]="data">
  <ng-template slot="avatar" let-item>
    <img [src]="item.avatar" class="avatar-img" />
  </ng-template>
</app-data-table>
```

### Without Toolbar
Simply don't provide `toolbarActions`:
```html
<app-data-table
  [data]="users"
  [columns]="columns"
  [actions]="actions"
>
</app-data-table>
```

### Without Search
```html
<app-data-table
  [data]="users"
  [columns]="columns"
  [showSearch]="false"
>
</app-data-table>
```

### Without Selection
```html
<app-data-table
  [data]="users"
  [columns]="columns"
  [selectionMode]="null"
>
</app-data-table>
```

### Single Selection
```html
<app-data-table
  [data]="users"
  [columns]="columns"
  [selectionMode]="'single'"
>
</app-data-table>
```

## Input Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `title` | string | undefined | Page title |
| `subtitle` | string | undefined | Page subtitle |
| `data` | any[] | [] | Data array to display |
| `columns` | TableColumn[] | [] | Column definitions |
| `actions` | TableAction[] | undefined | Row action buttons |
| `toolbarActions` | ToolbarAction[] | undefined | Toolbar action buttons |
| `rows` | number | 10 | Rows per page |
| `rowsPerPageOptions` | number[] | [10, 25, 50, 100] | Pagination options |
| `paginator` | boolean | true | Show pagination |
| `rowHover` | boolean | true | Enable row hover effect |
| `dataKey` | string | 'id' | Unique identifier field |
| `selectionMode` | 'single' \| 'multiple' \| null | 'multiple' | Selection mode |
| `showSearch` | boolean | true | Show search input |
| `searchPlaceholder` | string | 'Rechercher...' | Search placeholder |
| `globalFilterFields` | string[] | [] | Fields to search (auto-populated if empty) |
| `emptyMessage` | string | 'Aucune donnée disponible' | Empty state message |
| `emptySubMessage` | string | 'Il n\'y a actuellement...' | Empty state submessage |

## Output Events

| Event | Type | Description |
|-------|------|-------------|
| `toolbarActionClick` | `{action: string, event?: Event}` | Emitted when toolbar action is clicked |
| `rowActionClick` | `{action: string, item: any, index: number}` | Emitted when row action is clicked |
| `selectionChange` | `any[]` | Emitted when selection changes |
| `selectedItemsChange` | `any[]` | Two-way binding for selected items |

## Column Types

### Text (default)
```typescript
{ field: 'name', header: 'Name', type: 'text', icon: 'pi-user' }
```

### Status
```typescript
{ 
  field: 'active', 
  header: 'Status', 
  type: 'status',
  statusConfig: {
    trueLabel: 'Active',
    falseLabel: 'Inactive',
    trueIcon: 'pi-check-circle',
    falseIcon: 'pi-times-circle'
  }
}
```

### Custom
```typescript
{ field: 'customField', header: 'Custom', type: 'custom' }
```

## Styling

The component comes with pre-built modern styling. You can override styles in your component's CSS:

```css
::ng-deep .modern-table {
  /* Your custom styles */
}
```

## Complete Example

See the example component for a full implementation:
- `/src/app/_shared/components/data-table/examples/users-table.example.ts`

