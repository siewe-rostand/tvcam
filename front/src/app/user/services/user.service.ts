import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserModel } from '../model/user.model';


@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  getUsers(): Observable<any> {
    return this.http.get<any>('users');
  }

  saveUser(user: UserModel): Observable<any> {
    return this.http.post<any>('users', user,);
  }

  updateUser(user: UserModel): Observable<any> {
    return this.http.put<any>('users/edit', user);
  }
}
