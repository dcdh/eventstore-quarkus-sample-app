import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpResponse, HttpErrorResponse } from "@angular/common/http";
import { AuthService } from "./auth.service";
import { throwError, Observable } from "rxjs";
import { tap, map, catchError } from "rxjs/operators";
import { AccessTokenDto } from 'src/generated';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService implements HttpInterceptor {

  constructor(private authService: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const accessToken: AccessTokenDto = this.authService.accessToken();
    if (accessToken != null && this.shouldAddToken(req.url)) {
      req = req.clone({
        headers: req.headers.set('Authorization', 'Bearer ' + accessToken.accessToken)
      });
    }
    return next.handle(req).pipe(
      tap((response: HttpResponse<any>) => console.log(response)),
      catchError((error: HttpErrorResponse) => {
        if (error && error.status === 403) {
          // Forbidden means that token has expired need to renew it.
          this.authService.renewToken().pipe(
            map((accessToken: AccessTokenDto) => {
              req = req.clone({
                headers: req.headers.set('Authorization', 'Bearer ' + accessToken.accessToken)
              });
              return next.handle(req).pipe(
                tap((response: HttpResponse<any>) => console.log(response)),
                catchError((error: HttpErrorResponse) => {
                  this.authService.logout();
                  return throwError(error);
                })
              )
            })
          )
        }
        return throwError(error);
      })
    );
  }

  shouldAddToken(url: string): boolean {
    const excludedUrls: string[] = ["/authentication/login"];
    for (const excludedUrl of excludedUrls) {
      if (url.endsWith(excludedUrl)) {
        return false;
      }
    }
    return true;
  }

}
