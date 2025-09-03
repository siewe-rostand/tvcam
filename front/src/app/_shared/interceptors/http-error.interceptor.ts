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
import {NotificationService} from "../services/notification.service";
import {AuthService} from "../../auth/services/auth.service";
import {environment} from "../../../environments/environment";

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

  private notificationService = inject(NotificationService);
  private authService = inject(AuthService);

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error) => {
        if (error instanceof HttpErrorResponse) {
          let errorMessage: string = '', title: string;
          console.log('HTTP Error Response:', error);

          if (!environment.production) {
            console.log('HTTP Error Response:', error);
          }

          switch (error.status) {
            case 400:
              title = 'Mauvaise requête';
              errorMessage = error.error?.message || 'Vos données sont incorrectes, veuillez vérifier leur exactitude. Si le problème persiste, veuillez contacter le service technique.';
              break;
            case 401:
              title = 'Non autorisé';
              errorMessage = 'Vous avez un problème d\'autorisation. Si le problème persiste, veuillez contacter l\'administration.';
              break;
            case 403:
              title = 'Accès interdit';
              errorMessage = "Vous ne disposez pas d''un droit d''accès suffisant à ces données";
              break;
            case 404:
              title = 'Introuvable';
              errorMessage = error.error?.message || error.error?.developerMessage || 'La ressource demandée est introuvable';
              break;
            case 409:
              title = 'Conflit';
              errorMessage = error.error?.message || error.error?.developerMessage || 'Conflit entre les donnees transmise';
              break;
            case 500:
              title = 'Erreur interne';
              errorMessage = 'Une erreur interne est survenue. Veuillez contacter le service technique';
              break;
            case 0:
              title = 'Erreur de connexion';
              errorMessage = 'Impossible de se connecter au serveur. Vérifiez votre connexion internet.';
              break;
            default:
              title = 'Erreur Inconnue';
              errorMessage = 'Une erreur inconnue s\'est produite. Veuillez contacter l\'administrateur pour une résolution rapide.';
          }

          if (error.status !== 401) {
            this.notificationService.showError(errorMessage, title);
          }

          // Log the error
          console.error('HTTP Error:', error);

          return throwError(() => new Error(errorMessage));
        } else {
          console.error('Network error:', error);
          this.notificationService.showNetworkError();
          return throwError(() => new Error('A network error occurred.'));
        }
      }),
      map<HttpEvent<any>, any>((event) => {
        if (event instanceof HttpResponse) {
          console.log('HTTP Response:', event);
        }

        return event;
      })
    );
  }
}


export const httpErrorInterceptorProvider = [
  {provide: HTTP_INTERCEPTORS, useClass: HttpErrorInterceptor, multi: true},
];
