import {Injectable} from "@angular/core";
import {ConfirmationService} from "primeng/api";

export type DialogType = 'success' | 'warning' | 'error' | 'info';

export interface CustomConfirmOptions {
  type?: DialogType;
  header: string;
  message: string;
  acceptLabel?: string;
  rejectLabel?: string;
  onAccept?: () => void;
  onReject?: () => void;
}

@Injectable({
  providedIn: 'root'
})
export class CustomConfirmationDialogService {
  private readonly CONFIG = {
    success: {
      icon: 'pi-check-circle',
      iconClass: 'text-green-500',
      acceptButtonClass: 'p-button-success'
    },
    warning: {
      icon: 'pi-exclamation-triangle',
      iconClass: 'text-yellow-500',
      acceptButtonClass: 'p-button-warning'
    },
    error: {
      icon: 'pi-times-circle',
      iconClass: 'text-red-500',
      acceptButtonClass: 'p-button-danger'
    },
    info: {
      icon: 'pi-info-circle',
      iconClass: 'text-blue-500',
      acceptButtonClass: 'p-button-info'
    }
  };

  constructor(private confirmationService: ConfirmationService) {
  }

  confirm(options: CustomConfirmOptions): void {
    const type = options.type || 'info';
    const config = this.CONFIG[type];

    this.confirmationService.confirm({
      header: options.header,
      message: options.message,
      icon: config.icon  || 'pi pi-question',
      acceptLabel: options.acceptLabel || 'Confirm',
      rejectLabel: options.rejectLabel || 'Cancel',
      acceptButtonStyleClass: config.acceptButtonClass,
      rejectButtonStyleClass: 'p-button-secondary p-button-outlined',
      accept: options.onAccept,
      reject: options.onReject
    });
  }

  confirmDelete(message: string, onAccept?: () => void, onReject?: () => void): void {
    this.confirm({
      type: 'error',
      header: 'Delete Confirmation',
      message,
      acceptLabel: 'Yes, Delete',
      rejectLabel: 'Cancel',
      onAccept,
      onReject
    });
  }

  confirmSuccess(message: string, onAccept?: () => void): void {
    this.confirm({
      type: 'success',
      header: 'Success',
      message,
      acceptLabel: 'OK',
      rejectLabel: 'Close',
      onAccept
    });
  }

  confirmWarning(message: string, onAccept?: () => void, onReject?: () => void): void {
    this.confirm({
      type: 'warning',
      header: 'Warning',
      message,
      acceptLabel: 'Proceed',
      rejectLabel: 'Cancel',
      onAccept,
      onReject
    });
  }
}
