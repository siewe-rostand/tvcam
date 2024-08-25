import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthResponseModel} from '../model/auth-response.model';
import {Injectable} from '@angular/core';
import {RegisterRequest} from '../model/registration-request.model';


@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {
  }

  isLoggedIn: boolean = false;

  login(telephone: string, password: string): Observable<any> {
    return this.http.post<any>(
      'auth/authenticate',
      {
        password: password,
        telephone: telephone,
      }
    );
  }

  changePassword(telephone: string, password: string): Observable<any> {
    return this.http.post<any>(
      'auth/password/change',
      {
        newPassword: password,
        telephone: telephone,
      }
    );
  }

  signup(request: RegisterRequest): Observable<any> {
    return this.http.post<AuthResponseModel>(
      'auth/register',
      request
    );
  }

  // logout(): void {
  //   localStorage.removeItem(JWT_TOKEN);
  //   this.isLoggedIn = false;
  // }

  isAuthenticated(): boolean {
    return this.isLoggedIn;
  }

  logout(): Observable<any> {
    return this.http.post('auth/signout', {},);
  }
}
