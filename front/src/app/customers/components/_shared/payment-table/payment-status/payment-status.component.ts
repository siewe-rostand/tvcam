import {Component, Input} from '@angular/core';
import {StatusService} from "../../../../../_shared/services/status.service";
import {TagModule} from "primeng/tag";

type SeverityType = 'success' | 'info' | 'warning' | 'danger';
@Component({
  selector: 'app-payment-status',
  standalone: true,
  imports: [
    TagModule
  ],
  templateUrl: './payment-status.component.html',
  styleUrl: './payment-status.component.css'
})

export class PaymentStatusComponent {

  @Input() status: string = '';

  get statusText(): string {
    return this.statusService.getStatus(this.status);
  }

  get statusSeverity(): SeverityType {
    return this.statusService.getStatusSeverity(this.status) as SeverityType;
  }

  constructor(private statusService: StatusService) {
  }
}
