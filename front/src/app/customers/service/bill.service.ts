import { Injectable, signal } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject, catchError, Observable, throwError } from "rxjs";
import { BillModel } from "../model/bill.model";


@Injectable({
  providedIn: 'root'
})

export class BillService {
  selectedBillsSignal = signal<BillModel[]>([]);
  private statusSource = new BehaviorSubject<BillModel[]>([]);
  selectedBills = this.statusSource.asObservable();

  constructor(private http: HttpClient) { }

  setSelectedBills(selectedBills: BillModel[]) {
    this.statusSource.next(selectedBills);
  }

  fetchBills(): Observable<any> {
    return this.http.get(`bills`)
      .pipe(catchError(this.handleError));
  }

  generateBills(customerIds: (number | undefined)[], shouldGenerate: boolean): Observable<any> {
    return this.http.post(`bills/generate?shouldGenerate=${shouldGenerate}`, customerIds)
      .pipe(catchError(this.handleError));
  }

  getBillsForCustomer(customerId: number): Observable<any> {
    return this.http.get(`bills/customer/${customerId}`)
      .pipe(catchError(this.handleError));
  }

  deleteBill(billId: number): Observable<any> {
    return this.http.delete(`bills/${billId}`)
      .pipe(catchError(this.handleError));
  }

  deleteBills(billIds: number[]): Observable<any> {
    return this.http.delete(`bills/batch`, { body: billIds })
      .pipe(catchError(this.handleError));
  }

  private handleError(error: any): Observable<never> {
    console.error('An error occurred', error);
    return throwError(() => error);
  }
}
