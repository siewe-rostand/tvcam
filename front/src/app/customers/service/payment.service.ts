import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {catchError, Observable} from "rxjs";
import {PaymentModel} from "../model/payment.model";

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  constructor(private http: HttpClient) { }

  makePayment(payment: PaymentModel): Observable<any> {
    return this.http.post(`payments`, payment)
      .pipe(catchError(this.handleError));
  }

  getMonthlyPayment(month: string): Observable<any> {
    const params = new HttpParams().set("month",month)
    return this.http.get('payments/all',{params:params})
      .pipe(catchError(this.handleError));
  }

  getPaymentsForCustomer(customerId: number): Observable<any> {
    return this.http.get(`payments/${customerId}`)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: any) {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
