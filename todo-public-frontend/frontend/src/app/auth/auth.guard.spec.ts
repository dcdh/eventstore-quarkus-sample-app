import { TestBed } from '@angular/core/testing';

import { AuthGuard } from './auth.guard';
import { AuthService } from "./auth.service";
import { Router, UrlTree } from '@angular/router';
import { NotificationService } from './../notification/notification.service';

describe('AuthGuard', () => {
  let guard: AuthGuard;

  const authServiceSpy = jasmine.createSpyObj('AuthService', ['isLoggedIn']);
  const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['info']);
  const routerSpy = jasmine.createSpyObj('Router', ['parseUrl']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });
    guard = TestBed.inject(AuthGuard);
  });

  afterEach(() => {
    authServiceSpy.isLoggedIn.calls.reset();
    notificationServiceSpy.info.calls.reset();
    routerSpy.parseUrl.calls.reset();
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  describe('canActivate behaviors', () => {

    it('should redirect to login page when user is not authenticated', () => {
      // Given
      authServiceSpy.isLoggedIn.and.returnValue(false);
      const urlTree: UrlTree = new UrlTree();
      routerSpy.parseUrl.and.returnValue(urlTree);

      // When
      const canActivate = guard.canActivate(null, null);

      // Then
      expect(canActivate).toEqual(urlTree);
      expect(routerSpy.parseUrl).toHaveBeenCalledWith('/auth/login');
      expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
    });

    it('should notify user when user is not authenticated', () => {
      // Given
      authServiceSpy.isLoggedIn.and.returnValue(false);

      // When
      guard.canActivate(null, null);

      // Then
      expect(notificationServiceSpy.info).toHaveBeenCalledWith('Access Denied, Login is Required to Access This Page!');
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

});
