import { ErrorHandler, Injectable, Injector } from '@angular/core';
import { Router } from '@angular/router';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  constructor(private injector: Injector) {}

  handleError(error: Error): void {
    const router = this.injector.get(Router);

    const err = {
      message: error.message ? error.message : error.toString(),
      stack: error.stack ? error.stack : '',
    };

    console.log(err);
  }
}
