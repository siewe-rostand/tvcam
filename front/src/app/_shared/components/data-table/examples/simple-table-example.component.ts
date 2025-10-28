import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataTableComponent, TableColumn, TableAction } from '../data-table.component';

interface Product {
  id: number;
  name: string;
  category: string;
  price: number;
  stock: number;
}

@Component({
  selector: 'app-simple-table-example',
  standalone: true,
  imports: [CommonModule, DataTableComponent],
  template: `
    <!-- Just the table - no toolbar, no header -->
    <app-data-table
      [data]="products"
      [columns]="columns"
      [actions]="actions"
      [rows]="10"
      [paginator]="true"
      [showSearch]="true"
      searchPlaceholder="Search products..."
      [selectionMode]="null"
      (rowActionClick)="onAction($event)"
    >
    </app-data-table>
  `
})
export class SimpleTableExampleComponent implements OnInit {
  products: Product[] = [];

  columns: TableColumn[] = [
    { field: 'id', header: 'ID', sortable: true, width: '80px' },
    { field: 'name', header: 'Product', sortable: true, icon: 'pi-box' },
    { field: 'category', header: 'Category', sortable: true },
    { field: 'price', header: 'Price', sortable: true },
    { field: 'stock', header: 'Stock', sortable: true }
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
      tooltip: 'Edit product',
      action: 'edit'
    }
  ];

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.products = [
      { id: 1, name: 'Laptop', category: 'Electronics', price: 999.99, stock: 15 },
      { id: 2, name: 'Mouse', category: 'Electronics', price: 29.99, stock: 50 },
      { id: 3, name: 'Keyboard', category: 'Electronics', price: 79.99, stock: 30 },
      { id: 4, name: 'Monitor', category: 'Electronics', price: 299.99, stock: 20 }
    ];
  }

  onAction(event: {action: string, item: Product, index: number}) {
    console.log('Action:', event.action, 'Item:', event.item);
    // Handle your actions here
  }
}

