import { Injectable } from '@angular/core';
import {MessageService} from "primeng/api";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  constructor(private  messageService: MessageService) {}
  showError(message: string,title: string): void {
    this.messageService.add({
      severity: 'warn',
      summary: title,
      detail: message,
      sticky: true,
    })
  }
}
