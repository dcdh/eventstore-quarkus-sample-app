import { TestBed } from '@angular/core/testing';

import { AuthGuard } from './auth.guard';
import { AuthService } from "./auth.service";
import { Router, UrlTree } from '@angular/router';

describe('AuthGuard', () => {
  let guard: AuthGuard;

  const authServiceSpy = jasmine.createSpyObj('AuthService', ['isLoggedIn']);
  const routerSpy = jasmine.createSpyObj('Router', ['parseUrl']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });
    guard = TestBed.inject(AuthGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to login page when user is not authenticated', () => {
    // Given
    authServiceSpy.isLoggedIn.and.returnValue(false);
    const urlTree: UrlTree = new UrlTree();
    routerSpy.parseUrl.and.returnValue(urlTree);

    // When
    const canActivate = guard.canActivate(null, null);

    // Then
    expect(canActivate).toEqual(urlTree);
    expect(routerSpy.parseUrl).toHaveBeenCalledWith('/login');
    expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
  });

  it('should not redirect to login page when user is authenticated', () => {
    // Given
    authServiceSpy.isLoggedIn.and.returnValue(true);

    // When
    const canActivate = guard.canActivate(null, null);

    // Then
    expect(canActivate).toEqual(true);
    expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
  });

});
