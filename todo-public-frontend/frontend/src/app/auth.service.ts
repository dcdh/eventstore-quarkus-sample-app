import { Injectable } from '@angular/core';

import { AuthenticationService, AccessTokenDto } from 'src/generated';
import { throwError, Observable } from "rxjs";
import { HttpResponse, HttpErrorResponse } from "@angular/common/http";
import { map, tap, catchError } from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private authenticationService: AuthenticationService) { }

  isLoggedIn(): boolean {
    const accessToken = JSON.parse(localStorage.getItem('accessToken'));
    return (accessToken !== null) ? true : false;
  }

  logout(): void {
    localStorage.removeItem('accessToken');
  }

  login(username: string, password: string): Observable<AccessTokenDto> {
    return this.authenticationService.authenticationLoginPost(username, password)
      .pipe(
        tap((response: AccessTokenDto) => console.info('Received response when renewing token', response)),
        map((accessToken: AccessTokenDto) => {
          localStorage.setItem('accessToken', JSON.stringify(accessToken));
          return accessToken;
        }),
        catchError((error: HttpErrorResponse) => {
          return throwError(error);
        })
      );
  }

  accessToken(): AccessTokenDto | null {
    const accessToken: AccessTokenDto = JSON.parse(localStorage.getItem('accessToken'));
    return (accessToken !== null) ? accessToken : null;
  }

  renewToken(): Observable<AccessTokenDto> {
    const accessToken: AccessTokenDto = JSON.parse(localStorage.getItem('accessToken'));
    return this.authenticationService.authenticationRefreshTokenPost(accessToken.refreshToken)
      .pipe(
        tap((response: AccessTokenDto) => console.info('Received response when renewing token', response)),
        map((accessToken: AccessTokenDto) => {
          localStorage.setItem('accessToken', JSON.stringify(accessToken));
          return accessToken;
        }),
        catchError((error: HttpErrorResponse) => {
          localStorage.removeItem('accessToken');
          return throwError(error);
        })
      );
  }

}
