import { Injectable } from '@angular/core';
import { UserModel } from '../../user/model/user.model';
import { LocalStorageService } from '../../_shared/services/local-storage.service';

const USER_KEY = 'auth_user';
const JWT_TOKEN = 'auth_token';

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
    this.localStorageService.removeItem(JWT_TOKEN);
    this.localStorageService.setItem(JWT_TOKEN, token);
  }

  public getUser(): any {
    const user = this.localStorageService.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }

    return {};
  }

  public isLoggedIn(): boolean {
    const user = this.localStorageService.getItem(USER_KEY);
    return !!user;

  }
}
