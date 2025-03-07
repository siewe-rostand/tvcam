import {ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {provideClientHydration} from '@angular/platform-browser';
import {provideAnimations} from '@angular/platform-browser/animations';
import {provideHttpClient, withFetch, withInterceptorsFromDi,} from '@angular/common/http';
import {httpInterceptorProviders} from './_shared/interceptors/http-interceptor';
import {MessageService} from "primeng/api";
import {NotificationService} from "./_shared/services/notification.service";
import {httpErrorInterceptorProvider} from "./_shared/interceptors/http-error.interceptor";
import {environment} from "../environments/environment";
import {baseUrlInterceptorProvider} from "./_shared/interceptors/baseurl.interceptor";

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),
    provideClientHydration(),
    provideAnimations(),
    provideHttpClient(withFetch(), withInterceptorsFromDi()),
    baseUrlInterceptorProvider,
    {provide: 'BASE_API_URL', useValue: environment.BASE_URL},
    httpInterceptorProviders,
    MessageService,
    NotificationService,
    httpErrorInterceptorProvider,
  ],
};
