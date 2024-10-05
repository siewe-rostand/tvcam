import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {CommonModule} from "@angular/common";
import {BillModel} from "../../../model/bill.model";
import {BillService} from "../../../service/bill.service";

@Component({
  selector: 'app-bill-print',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './bill-print.component.html',
  styleUrl: './bill-print.component.css'
})
export class BillPrintComponent implements OnInit{
  constructor(private billService: BillService) {
  }

  bills: BillModel[] = [];
  @ViewChild('printSection') printSection!: ElementRef;
  factures = [
    {id: 'bill 2', amount: 300, customer: "client 2"},
    {id: 'bill 2', amount: 300, customer: "client 2"},
    {id: 'bill 2', amount: 300, customer: "client 2"},
    {id: 'bill 2', amount: 300, customer: "client 2"},
    {id: 'bill 2', amount: 300, customer: "client 2"},
    {id: 'bill 2', amount: 300, customer: "client 2"},
    {id: 'bill 2', amount: 300, customer: "client 2"},
    {id: 'bill 2', amount: 300, customer: "client 2"},
    {id: 'bill 2', amount: 300, customer: "client 2"},
  ];


  print() {
    window.print();
  }

  ngOnInit(): void {
    this.billService.selectedBills.subscribe(bills =>this.bills = bills);
    console.log(this.bills);
  }


}
