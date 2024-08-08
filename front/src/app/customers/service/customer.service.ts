import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {CustomerModel} from '../model/customer.model';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  constructor(private http: HttpClient) {
  }

  getCustomers(): Observable<any> {
    return this.http.get<any>('customers');
  }

  createCustomer(customer: CustomerModel): Observable<any> {
    return this.http.post<any>('customers/save', customer);
  }

  updateCustomer(customer: CustomerModel): Observable<any> {
    return this.http.put<any>('customers/edit', customer,);
  }

  deleteCustomer(customerId: number | undefined): Observable<any> {
    return this.http.delete<any>(`customers/${customerId}`);
  }

}
