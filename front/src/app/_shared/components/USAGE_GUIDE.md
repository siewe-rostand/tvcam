# Reusable Components Usage Guide

## Overview
The table functionality has been split into two separate, focused components:
1. **DataTableComponent** - Pure table component (reusable everywhere)
2. **PageToolbarComponent** - Optional toolbar with title and actions

## 1. DataTableComponent (Pure Table)

### Location
`/src/app/_shared/components/data-table/data-table.component.ts`

### Purpose
A clean, reusable table component focused solely on displaying data with sorting, filtering, pagination, and row actions.

### Basic Usage (Table Only)

```typescript
import { Component } from '@angular/core';
import { DataTableComponent, TableColumn, TableAction } from '@app/_shared/components/data-table/data-table.component';

@Component({
  selector: 'app-simple-list',
  standalone: true,
  imports: [DataTableComponent],
  template: `
    <app-data-table
      [data]="items"
      [columns]="columns"
      [actions]="actions"
      (rowActionClick)="handleAction($event)"
    >
    </app-data-table>
  `
})
export class SimpleListComponent {
  items = [...];
  columns: TableColumn[] = [...];
  actions: TableAction[] = [...];
}
```

---

## 2. PageToolbarComponent (Optional Header/Toolbar)

### Location
`/src/app/_shared/components/page-toolbar/page-toolbar.component.ts`

### Purpose
Displays a page title, subtitle, and action buttons. Use only when you need a consistent header/toolbar.

### Usage

```typescript
import { PageToolbarComponent, ToolbarAction } from '@app/_shared/components/page-toolbar/page-toolbar.component';

@Component({
  template: `
    <app-page-toolbar
      title="My Page Title"
      subtitle="Page description"
      [actions]="toolbarActions"
      [selectedItemsCount]="selectedItems.length"
      (actionClick)="handleToolbarAction($event)"
    >
    </app-page-toolbar>
  `
})
```

---

## 3. Complete Example (Both Components Together)

```typescript
import { Component } from '@angular/core';
import { DataTableComponent, TableColumn, TableAction } from '@app/_shared/components/data-table/data-table.component';
import { PageToolbarComponent, ToolbarAction } from '@app/_shared/components/page-toolbar/page-toolbar.component';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [DataTableComponent, PageToolbarComponent],
  template: `
    <!-- Optional: Use toolbar when you need page header and actions -->
    <app-page-toolbar
      title="Users Management"
      subtitle="Manage system users"
      [actions]="toolbarActions"
      [selectedItemsCount]="selectedUsers.length"
      (actionClick)="onToolbarAction($event)"
    >
    </app-page-toolbar>

    <!-- Always: The data table -->
    <app-data-table
      [data]="users"
      [columns]="columns"
      [actions]="rowActions"
      [showSearch]="true"
      [(selectedItems)]="selectedUsers"
      (rowActionClick)="onRowAction($event)"
    >
    </app-data-table>
  `
})
export class UsersComponent {
  users = [];
  selectedUsers = [];
  
  columns: TableColumn[] = [
    { field: 'name', header: 'Name', sortable: true },
    { field: 'email', header: 'Email', sortable: true }
  ];
  
  rowActions: TableAction[] = [
    { label: 'Edit', icon: 'pi pi-pencil', severity: 'info', tooltip: 'Edit', action: 'edit' }
  ];
  
  toolbarActions: ToolbarAction[] = [
    { label: 'New', icon: 'pi pi-plus', severity: 'success', action: 'create' },
    { label: 'Delete', icon: 'pi pi-trash', severity: 'danger', action: 'delete', requiresSelection: true }
  ];
}
```

---

## 4. Usage Scenarios

### Scenario A: Simple List (No Toolbar)
When you just need a table without any header or toolbar actions:

```html
<app-data-table
  [data]="items"
  [columns]="columns"
  [showSearch]="false"
  [paginator]="true"
>
</app-data-table>
```

### Scenario B: List with Custom Header
When you want your own custom header instead of the toolbar:

```html
<div class="my-custom-header">
  <h2>My Custom Title</h2>
  <button (click)="doSomething()">Custom Action</button>
</div>

<app-data-table
  [data]="items"
  [columns]="columns"
>
</app-data-table>
```

### Scenario C: Full Page with Toolbar
When you want the standard toolbar with title and actions:

```html
<app-page-toolbar
  title="Page Title"
  subtitle="Description"
  [actions]="toolbarActions"
  (actionClick)="handleToolbarAction($event)"
>
</app-page-toolbar>

<app-data-table
  [data]="items"
  [columns]="columns"
  [actions]="rowActions"
>
</app-data-table>
```

### Scenario D: Multiple Tables on Same Page
Each with different configurations:

```html
<h2>Active Users</h2>
<app-data-table
  [data]="activeUsers"
  [columns]="userColumns"
  [showSearch]="true"
>
</app-data-table>

<h2>Inactive Users</h2>
<app-data-table
  [data]="inactiveUsers"
  [columns]="userColumns"
  [showSearch]="false"
  [paginator]="false"
>
</app-data-table>
```

---

## Key Benefits

✅ **Separation of Concerns**: Table logic is separate from page layout
✅ **Maximum Reusability**: Use the table anywhere without toolbar overhead
✅ **Flexibility**: Mix and match - use toolbar only where needed
✅ **Consistency**: When you do use the toolbar, it looks the same everywhere
✅ **Simplicity**: Each component does one thing well

---

## Component Inputs/Outputs

### DataTableComponent

**Inputs:**
- `data: any[]` - Data to display
- `columns: TableColumn[]` - Column configuration
- `actions?: TableAction[]` - Row action buttons
- `rows: number` - Rows per page (default: 10)
- `paginator: boolean` - Show pagination (default: true)
- `showSearch: boolean` - Show search input (default: true)
- `selectionMode: 'single' | 'multiple' | null` - Selection mode (default: 'multiple')
- `selectedItems: any[]` - Two-way binding for selected items
- ... and more

**Outputs:**
- `rowActionClick` - Emits when row action is clicked
- `selectionChange` - Emits when selection changes
- `selectedItemsChange` - Two-way binding output

### PageToolbarComponent

**Inputs:**
- `title?: string` - Page title
- `subtitle?: string` - Page subtitle
- `actions: ToolbarAction[]` - Toolbar action buttons
- `selectedItemsCount: number` - Number of selected items (for disabling bulk actions)

**Outputs:**
- `actionClick` - Emits when toolbar action is clicked

---

## Migration from Old Component

If you were using the old combined component, simply split the usage:

**Before:**
```html
<app-data-table
  title="Users"
  subtitle="Manage users"
  [data]="users"
  [columns]="columns"
  [toolbarActions]="toolbarActions"
  [actions]="rowActions"
>
</app-data-table>
```

**After:**
```html
<app-page-toolbar
  title="Users"
  subtitle="Manage users"
  [actions]="toolbarActions"
  (actionClick)="onToolbarAction($event)"
>
</app-page-toolbar>

<app-data-table
  [data]="users"
  [columns]="columns"
  [actions]="rowActions"
  (rowActionClick)="onRowAction($event)"
>
</app-data-table>
```

