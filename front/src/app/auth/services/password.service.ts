import { Injectable } from '@angular/core';
import {
  AbstractControl,
  AsyncValidatorFn,
  ValidationErrors,
} from '@angular/forms';
import { Observable, of, timer } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class PasswordValidatorService {
  passwordValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      const password = control.value;

      // Simulate an asynchronous validation (replace with your actual logic)
      return timer(1000).pipe(
        switchMap(() => {
          if (password.length < 6) {
            return of({ minlength: { minlength: 6 } });
          } else if (password.length > 16) {
            return of({ maxlength: { maxlength: 16 } });
          } else {
            return of(null);
          }
        })
      );
    };
  }
}
