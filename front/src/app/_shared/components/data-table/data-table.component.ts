import { Component, Input, Output, EventEmitter, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Table, TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ToolbarModule } from 'primeng/toolbar';
import { InputTextModule } from 'primeng/inputtext';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { InputIconModule } from 'primeng/inputicon';
import { IconFieldModule } from 'primeng/iconfield';
import { TooltipModule } from 'primeng/tooltip';
import { DialogModule } from 'primeng/dialog';
import { FormsModule } from '@angular/forms';

export interface TableColumn {
  field: string;
  header: string;
  sortable?: boolean;
  icon?: string;
  width?: string;
  type?: 'text' | 'badge' | 'actions' | 'status' | 'custom';
  statusConfig?: {
    trueLabel: string;
    falseLabel: string;
    trueIcon: string;
    falseIcon: string;
  };
}

export interface TableAction {
  label: string;
  icon: string;
  severity: 'success' | 'info' | 'warning' | 'danger' | 'secondary';
  tooltip: string;
  action: string;
  disabled?: (item: any) => boolean;
}

export interface ToolbarAction {
  label: string;
  icon: string;
  severity: 'success' | 'info' | 'warning' | 'danger' | 'secondary';
  action: string;
  disabled?: boolean;
  requiresSelection?: boolean;
}

@Component({
  selector: 'app-data-table',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    ButtonModule,
    ToolbarModule,
    InputTextModule,
    ToastModule,
    ConfirmDialogModule,
    InputIconModule,
    IconFieldModule,
    TooltipModule,
    DialogModule,
    FormsModule
  ],
  template: `
    <div class="data-table-container">
      <!-- Header de la page -->
      <div class="page-header" *ngIf="title || subtitle">
        <h1 class="page-title" *ngIf="title">{{ title }}</h1>
        <p class="page-subtitle" *ngIf="subtitle">{{ subtitle }}</p>
      </div>

      <div class="main-card">
        <!-- Toolbar modernisée -->
        <p-toolbar class="custom-toolbar" *ngIf="toolbarActions && toolbarActions.length > 0">
          <ng-template pTemplate="left">
            <p-button
              *ngFor="let action of toolbarActions"
              [severity]="action.severity"
              [label]="action.label"
              [icon]="action.icon"
              [class]="'modern-button btn-' + action.severity"
              [disabled]="isActionDisabled(action)"
              (onClick)="onToolbarAction(action.action, $event)"
            />
          </ng-template>
        </p-toolbar>

        <!-- Table modernisée -->
        <p-table
          #dt
          [value]="data"
          [rows]="rows"
          [rowsPerPageOptions]="rowsPerPageOptions"
          [paginator]="paginator"
          [globalFilterFields]="globalFilterFields"
          [tableStyle]="{ 'min-width': '75rem' }"
          [(selection)]="selectedItems"
          (selectionChange)="onSelectionChange($event)"
          [rowHover]="rowHover"
          [dataKey]="dataKey"
          [selectionMode]="selectionMode"
          class="modern-table">

          <ng-template pTemplate="caption" *ngIf="showSearch">
            <div class="flex justify-content-end">
              <div class="search-container">
                <p-iconField iconPosition="left">
                  <p-inputIcon>
                    <i class="pi pi-search"></i>
                  </p-inputIcon>
                  <input
                    pInputText
                    type="text"
                    (input)="onGlobalFilter($event)"
                    [placeholder]="searchPlaceholder"
                    class="search-input"
                  />
                </p-iconField>
              </div>
            </div>
          </ng-template>

          <ng-template pTemplate="header">
            <tr>
              <th style="width: 4rem" *ngIf="selectionMode === 'multiple'">
                <p-tableHeaderCheckbox/>
              </th>
              <th
                *ngFor="let col of columns"
                [pSortableColumn]="col.sortable ? col.field : undefined"
                [style]="col.width ? {'min-width': col.width} : {}">
                <i class="pi {{col.icon}} mr-2" *ngIf="col.icon"></i>{{col.header}}
                <p-sortIcon [field]="col.field" *ngIf="col.sortable"/>
              </th>
              <th *ngIf="actions && actions.length > 0" style="min-width: 12rem">
                <i class="pi pi-cog mr-2"></i>Actions
              </th>
            </tr>
          </ng-template>

          <ng-template pTemplate="body" let-item let-rowIndex="rowIndex">
            <tr>
              <td *ngIf="selectionMode === 'multiple'">
                <p-tableCheckbox [value]="item"/>
              </td>
              <td *ngFor="let col of columns">

                <!-- Text type -->
                <div *ngIf="col.type === 'text' || !col.type" class="flex align-items-center">
                  <i class="pi {{col.icon}} mr-2" *ngIf="col.icon"
                     [class]="'text-' + getIconColor(col.field)"></i>
                  <span [class]="getTextClass(col.field)">{{ getFieldValue(item, col.field) }}</span>
                </div>

                <!-- Status type -->
                <div *ngIf="col.type === 'status'" class="flex align-items-center">
                  <i class="pi status-icon"
                     [class]="getStatusIconClass(col, item)">
                  </i>
                  <span class="ml-2 font-medium"
                        [ngClass]="{
                          'text-green-600': getFieldValue(item, col.field),
                          'text-red-600': !getFieldValue(item, col.field)
                        }">
                    {{ getFieldValue(item, col.field) ? col.statusConfig?.trueLabel : col.statusConfig?.falseLabel }}
                  </span>
                </div>

                <!-- Custom content -->
                <div *ngIf="col.type === 'custom'">
                  <ng-content select="[slot={{col.field}}]"></ng-content>
                </div>

              </td>

              <!-- Actions column -->
              <td *ngIf="actions && actions.length > 0">
                <div class="flex align-items-center">
                  <p-button
                    *ngFor="let action of actions"
                    [icon]="action.icon"
                    class="action-button"
                    [rounded]="true"
                    [outlined]="true"
                    [severity]="action.severity"
                    [disabled]="action.disabled ? action.disabled(item) : false"
                    (onClick)="onRowAction(action.action, item, rowIndex)"
                    [pTooltip]="action.tooltip"
                    tooltipPosition="top"
                  />
                </div>
              </td>
            </tr>
          </ng-template>

          <!-- Empty state -->
          <ng-template pTemplate="emptymessage">
            <tr>
              <td [attr.colspan]="getColspan()" class="text-center p-4">
                <div class="empty-state">
                  <i class="pi pi-inbox text-6xl text-gray-400 mb-3"></i>
                  <p class="text-xl text-gray-500 mb-2">{{ emptyMessage }}</p>
                  <p class="text-gray-400">{{ emptySubMessage }}</p>
                </div>
              </td>
            </tr>
          </ng-template>
        </p-table>

        <!-- Contenu additionnel (dialogs, etc.) -->
        <ng-content></ng-content>
      </div>
    </div>
  `,
  styleUrls: ['./data-table.component.css']
})
export class DataTableComponent implements OnInit {
  @ViewChild('dt') dt!: Table;

  // Configuration de base
  @Input() title?: string;
  @Input() subtitle?: string;
  @Input() data: any[] = [];
  @Input() columns: TableColumn[] = [];
  @Input() actions?: TableAction[];
  @Input() toolbarActions?: ToolbarAction[];

  // Configuration de la table
  @Input() rows: number = 10;
  @Input() rowsPerPageOptions: number[] = [10, 25, 50, 100];
  @Input() paginator: boolean = true;
  @Input() rowHover: boolean = true;
  @Input() dataKey: string = 'id';
  @Input() selectionMode: 'single' | 'multiple' | null = 'multiple';

  // Configuration de la recherche
  @Input() showSearch: boolean = true;
  @Input() searchPlaceholder: string = 'Rechercher...';
  @Input() globalFilterFields: string[] = [];

  // Messages
  @Input() emptyMessage: string = 'Aucune donnée disponible';
  @Input() emptySubMessage: string = 'Il n\'y a actuellement aucun élément à afficher';

  // Selection
  @Input() selectedItems: any[] = [];
  @Output() selectedItemsChange = new EventEmitter<any[]>();

  // Events
  @Output() toolbarActionClick = new EventEmitter<{action: string, event?: Event}>();
  @Output() rowActionClick = new EventEmitter<{action: string, item: any, index: number}>();
  @Output() selectionChange = new EventEmitter<any[]>();

  ngOnInit(): void {
    if (this.globalFilterFields.length === 0) {
      this.globalFilterFields = this.columns
        .filter(col => col.type === 'text' || !col.type)
        .map(col => col.field);
    }
  }

  onGlobalFilter(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    this.dt.filterGlobal(inputElement.value, 'contains');
  }

  onSelectionChange(event: any) {
    this.selectedItems = event;
    this.selectedItemsChange.emit(this.selectedItems);
    this.selectionChange.emit(this.selectedItems);
  }

  onToolbarAction(action: string, event?: Event) {
    this.toolbarActionClick.emit({ action, event });
  }

  onRowAction(action: string, item: any, index: number) {
    this.rowActionClick.emit({ action, item, index });
  }

  isActionDisabled(action: ToolbarAction): boolean {
    if (action.disabled) return true;
    if (action.requiresSelection) {
      return !this.selectedItems || this.selectedItems.length === 0;
    }
    return false;
  }

  getFieldValue(item: any, field: string): any {
    return field.split('.').reduce((obj, key) => obj?.[key], item);
  }

  getTextClass(field: string): string {
    // Vous pouvez personnaliser les classes selon les champs
    if (field === 'name') return 'font-semibold';
    return '';
  }

  getIconColor(field: string): string {
    // Vous pouvez personnaliser les couleurs d'icônes selon les champs
    const colorMap: { [key: string]: string } = {
      'telephone': 'blue-500',
      'address': 'orange-500',
      'email': 'purple-500',
      'status': 'green-500'
    };
    return colorMap[field] || 'gray-500';
  }

  getStatusIconClass(col: TableColumn, item: any): string {
    const isActive = this.getFieldValue(item, col.field);
    if (isActive) {
      return `${col.statusConfig?.trueIcon} active`;
    } else {
      return `${col.statusConfig?.falseIcon} inactive`;
    }
  }

  getColspan(): number {
    let count = this.columns.length;
    if (this.selectionMode === 'multiple') count++;
    if (this.actions && this.actions.length > 0) count++;
    return count;
  }
}
