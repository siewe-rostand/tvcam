import { Injectable } from '@angular/core';
import {MessageService} from "primeng/api";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  constructor(private  messageService: MessageService) {}
  showError(message: string): void {
    this.messageService.add({
      severity: 'error',
      summary: 'Error occurred',
      detail: message,
    })
  }
}
