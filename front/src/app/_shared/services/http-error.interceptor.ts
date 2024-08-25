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
import {catchError, map, Observable, throwError} from 'rxjs';
import {NotificationService} from "../../shared/service/notification.service";

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
          let errorMessage:string = '',title: string;
          console.log(error.error)

          switch (error.status) {
            case 400:
              title = 'mauvaise requête';
              errorMessage = 'Vos données sont incorrectes, veuillez vérifier leur exactitude. Si le problème persiste, veuillez contacter le service technique. ';
              break;
            case 401:
              title = 'Non autorisé';
              errorMessage = 'Vous avez un problème d\'autorisation. Si le problème persiste, veuillez contacter l\'administration.';
              break;
            case 403:
              title = 'Accès interdit';
              errorMessage = 'Vous ne disposez pas d\'un droit d\'accès suffisant à ces données';
              break;
            case 404:
              title = 'Introuvable';
              errorMessage = error.error.developerMessage;
              break;
            case 500:
              title = 'Erreur interne';
              errorMessage = 'Une erreur interne est survenue. Veuillez contacter le service technique';
              break;
            default:
              title = 'Erreur Inconnue';
              title = 'une erreur inconnue s\'est produite. veuillez contacter l\'administrateur pour une résolution rapide. ';
          }

          this.notificationService.showError(errorMessage, title);

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
