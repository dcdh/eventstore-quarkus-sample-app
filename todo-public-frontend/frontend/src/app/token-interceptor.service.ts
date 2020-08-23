import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpResponse, HttpErrorResponse } from "@angular/common/http";
import { AuthService } from "./auth.service";
import { throwError, Observable } from "rxjs";
import { tap, catchError } from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class TokenInterceptorService implements HttpInterceptor {

  constructor(private authService: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const accessToken = this.authService.accessToken();
    if (accessToken != null && this.shouldAddToken(req.url)) {
      req = req.clone({
        headers: req.headers.set('Authorization', 'Bearer ' + accessToken.accessToken)
      });
    }
    return next.handle(req).pipe(
      tap((response: HttpResponse<any>) => console.log(response)),
      catchError((error: HttpErrorResponse) => {
        if (error && error.status === 403) {
            // Todo renew accessToken
            this.authService.logout();
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
