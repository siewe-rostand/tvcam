import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, Observable} from "rxjs";


@Injectable({
  providedIn: 'root'
})

export class BillService {

  constructor(private http: HttpClient) { }


  fetchBills(): Observable<any> {
    return this.http.get(`bills`)
      .pipe(catchError(this.handleError));
  }

  generateBills(customerIds: number[] | undefined): Observable<any> {
    return this.http.post(`bills/generate`, customerIds)
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
