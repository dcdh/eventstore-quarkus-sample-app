import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { UserService, AccessTokenDto } from 'src/generated';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private userService: UserService, private router: Router) { }

  isLoggedIn(): boolean {
    const accessToken = JSON.parse(localStorage.getItem('accessToken'));
    return (accessToken !== null) ? true : false;
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    this.router.navigate(['/login']);
  }

  login(username: string, password: string): void {
    this.userService.usersLoginPost(username, password)
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
    const accessToken = JSON.parse(localStorage.getItem('accessToken'));
    return (accessToken !== null) ? accessToken : null;
  }

}