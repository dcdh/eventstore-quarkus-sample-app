import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpResponse, HttpErrorResponse } from "@angular/common/http";
import { AuthService } from "./auth.service";
import { throwError, Observable, empty } from "rxjs";
import { tap, catchError, flatMap } from "rxjs/operators";
import { AccessTokenDto } from 'src/generated';
import { NotificationService } from './../notification/notification.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService, private notificationService: NotificationService, private router: Router) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (this.canHandleTokenRenewal(req.url)) {
      const accessToken: AccessTokenDto = this.authService.accessToken();
      if (accessToken != null) {
        req = req.clone({
          headers: req.headers.set('Authorization', 'Bearer ' + accessToken.accessToken)
        });
      }
      return next.handle(req).pipe(
        tap((response: HttpResponse<any>) => console.info('Received response when executing request', response)),
        catchError((error: HttpErrorResponse) => {
          if (error && error.status === 401) {
            // Unauthenticated means that token has expired need to renew it.
            return this.authService.renewToken()
              .pipe(
                tap((response: AccessTokenDto) => console.info('Received response after access token renewal')),
                flatMap((renewedAccessTokenDto: AccessTokenDto) => {
                  req = req.clone({
                    headers: req.headers.set('Authorization', 'Bearer ' + renewedAccessTokenDto.accessToken)
                  });
                  return next.handle(req).pipe(
                    tap((response: HttpResponse<any>) => console.info('Received response when executing request using new access token', response)),
                    catchError((error: HttpErrorResponse) => {
                      return throwError(error);
                    })
                  );
                }),
                catchError((error: HttpErrorResponse) => {
                  this.notificationService.error('Unable to renew authentication token, redirecting to login page');
                  this.router.navigate(['/login']);
                  return empty();
                })
              );
          }
          return throwError(error);
        })
      );
    } else {
      return next.handle(req);
    }
  }

  canHandleTokenRenewal(url: string): boolean {
    const excludedUrls: string[] = ["/authentication/login", "/authentication/refresh-token"];
    for (const excludedUrl of excludedUrls) {
      if (url.endsWith(excludedUrl)) {
        return false;
      }
    }
    return true;
  }

}
