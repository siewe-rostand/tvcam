import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {catchError, Observable} from "rxjs";

const AUTH_API = 'http://localhost:8085/api/v1/bills';
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};
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

  getPaymentsForCustomer(customerId: number): Observable<any> {
    return this.http.get(`payments/customer/${customerId}`)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: any) {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
