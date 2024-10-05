import {Injectable, signal} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, catchError, Observable} from "rxjs";
import {BillModel} from "../model/bill.model";


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

  generateBills(customerIds: number[],shouldGenerate: boolean): Observable<any> {
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


  private handleError(error: any) {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
