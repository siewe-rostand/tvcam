import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {PaymentModel} from "../model/payment.model";
import {ApiResponse} from "../../_shared/model/api-response";

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  constructor(private http: HttpClient) {
  }

  makePayment(payment: PaymentModel): Observable<ApiResponse<PaymentModel>> {
    return this.http.post<ApiResponse<PaymentModel>>(`payments`, payment);
  }

  getMonthlyPayment(month: string): Observable<ApiResponse<PaymentModel[]>> {
    const params = new HttpParams().set("month", month)
    return this.http.get<ApiResponse<PaymentModel[]>>('payments/all', {params: params})
  }

  getPaymentsForCustomer(customerId: number): Observable<ApiResponse<PaymentModel[]>> {
    return this.http.get<ApiResponse<PaymentModel[]>>(`payments/${customerId}`);
  }
}
