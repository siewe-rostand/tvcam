import {Injectable} from '@angular/core';
import {UserModel} from '../../user/model/user.model';
import {LocalStorageService} from './local-storage.service';
import {JWT_TOKEN, JWT_TOKEN_EXPIRATION, USER_KEY} from "../utils/constant";


@Injectable({
  providedIn: 'root',
})
export class StorageService {
  constructor(private localStorageService: LocalStorageService) {}

  clean() {
    this.localStorageService.clear();
  }

  public saveUser(user: UserModel): void {
    this.localStorageService.removeItem(USER_KEY);
    this.localStorageService.setItem(USER_KEY, JSON.stringify(user));
  }

  public saveToken(token: string): void {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const expirationDate = new Date(payload.exp * 1000);
    this.localStorageService.removeItem(JWT_TOKEN);
    this.localStorageService.setItem(JWT_TOKEN, token);
    this.localStorageService.setItem(JWT_TOKEN_EXPIRATION, expirationDate.toString());
  }

  public getUser(): any {
    const user = this.localStorageService.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }

    return {};
  }

  get getToken(): string | null {
    return this.localStorageService.getItem(JWT_TOKEN);
  }

  isTokenValid(): boolean {
    try {
      const expirationString = this.localStorageService.getItem(JWT_TOKEN_EXPIRATION);
      const token = this.getToken;

      // if token expiration  is not stored, or it is invalid, we assumed it has expired
      if (expirationString == null || token == null) {
        return false;
      }

      const expirationDate = new Date(expirationString);
      return !(expirationDate < new Date());

    } catch (e) {
      console.error('Error parsing token expiration:', e);
      return false;
    }
  }


}
