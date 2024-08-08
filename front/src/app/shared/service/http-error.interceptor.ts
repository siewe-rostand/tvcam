import {
  HTTP_INTERCEPTORS,
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse,
} from '@angular/common/http';
import {inject, Injectable} from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import {NotificationService} from "./notification.service";

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

  private notificationService = inject(NotificationService);

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error) => {
        if (error instanceof HttpErrorResponse) {
          let errorMessage = '';

          switch (error.status) {
            case 400:
              errorMessage = 'Bad Request';
              break;
            case 401:
              errorMessage = 'Unauthorized';
              break;
            case 403:
              errorMessage = 'Forbidden';
              break;
            case 404:
              errorMessage = 'Not Found';
              break;
            case 500:
              errorMessage = 'Internal Server Error';
              break;
            default:
              errorMessage = 'An unknown error occurred';
          }

          this.notificationService.showError(errorMessage);

          // Log the error
          console.error('HTTP Error:', error);

          return throwError(() => new Error(errorMessage));
        } else {
          // Handle other types of errors (e.g., network errors)
          console.error('Network error:', error);
          return throwError(() => new Error('A network error occurred.'));
        }
      }),
      map<HttpEvent<any>, any>((event) => {
        if (event instanceof HttpResponse) {
          console.log(event);
        }

        return event;
      })
    );
  }
}


export const httpErrorInterceptorProvider = [
  {provide: HTTP_INTERCEPTORS,useClass: HttpErrorInterceptor, multi: true},
];
