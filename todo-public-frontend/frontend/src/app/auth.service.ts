import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { AuthenticationService, AccessTokenDto } from 'src/generated';
import { throwError, Observable } from "rxjs";
import { HttpResponse, HttpErrorResponse } from "@angular/common/http";
import { map, catchError } from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private authenticationService: AuthenticationService, private router: Router) { }

  isLoggedIn(): boolean {
    const accessToken = JSON.parse(localStorage.getItem('accessToken'));
    return (accessToken !== null) ? true : false;
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    this.router.navigate(['/login']);
  }

  login(username: string, password: string): void {
    this.authenticationService.authenticationLoginPost(username, password)
      .subscribe({
        next: accessToken => {
          localStorage.setItem('accessToken', JSON.stringify(accessToken));
          this.router.navigate(['/todos']);
        },
        error: error => {
          console.warn(error);
        }
      })
  }

  accessToken(): AccessTokenDto | null {
    const accessToken: AccessTokenDto = JSON.parse(localStorage.getItem('accessToken'));
    return (accessToken !== null) ? accessToken : null;
  }

  renewToken(): Observable<AccessTokenDto> {
    const accessToken: AccessTokenDto = JSON.parse(localStorage.getItem('accessToken'));
    return this.authenticationService.authenticationRefreshTokenPost(accessToken.refreshToken)
      .pipe(
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
