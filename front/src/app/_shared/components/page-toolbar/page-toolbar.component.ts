import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { ToolbarModule } from 'primeng/toolbar';

export interface ToolbarAction {
  label: string;
  icon: string;
  severity: 'success' | 'info' | 'warning' | 'danger' | 'secondary';
  action: string;
  disabled?: boolean;
  requiresSelection?: boolean;
}

@Component({
  selector: 'app-page-toolbar',
  standalone: true,
  imports: [
    CommonModule,
    ButtonModule,
    ToolbarModule
  ],
  template: `
    <div class="page-toolbar-container">
      <!-- Header de la page -->
      <div class="page-header" *ngIf="title || subtitle">
        <h1 class="page-title" *ngIf="title">{{ title }}</h1>
        <p class="page-subtitle" *ngIf="subtitle">{{ subtitle }}</p>
      </div>

      <!-- Toolbar modernisée -->
      <p-toolbar class="custom-toolbar" *ngIf="actions && actions.length > 0">
        <ng-template pTemplate="left">
          <p-button
            *ngFor="let action of actions"
            [severity]="action.severity"
            [label]="action.label"
            [icon]="action.icon"
            [class]="'modern-button btn-' + action.severity"
            [disabled]="isActionDisabled(action)"
            (onClick)="onAction(action.action, $event)"
          />
        </ng-template>

        <ng-template pTemplate="right">
          <ng-content select="[slot=right]"></ng-content>
        </ng-template>
      </p-toolbar>

      <!-- Additional content slot -->
      <ng-content></ng-content>
    </div>
  `,
  styleUrls: ['./page-toolbar.component.css']
})
export class PageToolbarComponent {
  @Input() title?: string;
  @Input() subtitle?: string;
  @Input() actions: ToolbarAction[] = [];
  @Input() selectedItemsCount: number = 0;

  @Output() actionClick = new EventEmitter<{action: string, event?: Event}>();

  onAction(action: string, event?: Event) {
    this.actionClick.emit({ action, event });
  }

  isActionDisabled(action: ToolbarAction): boolean {
    if (action.disabled) return true;
    if (action.requiresSelection) {
      return this.selectedItemsCount === 0;
    }
    return false;
  }
}

