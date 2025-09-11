import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from "@angular/common";
import { BillModel } from "../../../model/bill.model";
import { BillService } from "../../../service/bill.service";
import { ZoneManagementService } from "../../../service/zone-management.service";
import { BillUtils } from "../../../../_shared/utils/bill.utils";

@Component({
  selector: 'app-bill-print',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './bill-print.component.html',
  styleUrl: './bill-print.component.css'
})
export class BillPrintComponent implements OnInit {
  constructor(
    private billService: BillService,
    private zoneManagementService: ZoneManagementService
  ) {
  }

  bills: BillModel[] = [];
  enrichedBills: BillModel[] = [];
  @ViewChild('printSection') printSection!: ElementRef;

  print() {
    window.print();
  }

  ngOnInit(): void {
    this.billService.selectedBills.subscribe(bills => {
      this.bills = bills;
      this.enrichBillsWithZoneInfo();
    });
    console.log('Bills to print:', this.bills);
  }

  private enrichBillsWithZoneInfo(): void {
    this.enrichedBills = [];
    this.bills.forEach(bill => {
      this.zoneManagementService.enrichBillWithZoneInfo(bill).subscribe(enrichedBill => {
        this.enrichedBills.push(enrichedBill);
      });
    });
  }

  /**
   * Format month value to display label using BillUtils
   */
  getMonthLabel(monthValue?: string): string {
    return BillUtils.getMonthLabelFr(monthValue);
  }

  /**
   * Format date for display using BillUtils
   */
  formatDate(dateValue?: string): string {
    return BillUtils.formatDateFr(dateValue);
  }

  /**
   * Format amount with proper FCFA formatting using BillUtils
   */
  formatAmount(amount?: number): string {
    return BillUtils.formatAmountFCFA(amount);
  }

  /**
   * Generate bill reference number
   */
  getBillReference(bill: BillModel): string {
    return BillUtils.generateBillReference(bill.id, bill.month, bill.year);
  }

  /**
   * Get default deposit date if not provided
   */
  getDepositDate(bill: BillModel): string {
    return bill.depositDate ? this.formatDate(bill.depositDate) : BillUtils.getDefaultDepositDate();
  }

  /**
   * Get deadline date, with fallback to default
   */
  getDeadlineDate(bill: BillModel): string {
    return bill.deadLine ? this.formatDate(bill.deadLine) : BillUtils.getDefaultDeadlineDate();
  }
}
