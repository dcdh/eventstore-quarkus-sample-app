import { async, TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import { AuthenticationService, AccessTokenDto } from 'src/generated';
import { Router } from '@angular/router';
import { throwError, Observable, defer } from "rxjs";

describe('AuthService', () => {
  let service: AuthService;

  const authenticationServiceSpy = jasmine.createSpyObj('AuthenticationService', ['authenticationLoginPost', 'authenticationRefreshTokenPost']);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: AuthenticationService, useValue: authenticationServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });
    service = TestBed.inject(AuthService);
  });

  afterEach(() => {
    authenticationServiceSpy.authenticationLoginPost.calls.reset();
    authenticationServiceSpy.authenticationRefreshTokenPost.calls.reset();
    routerSpy.navigate.calls.reset();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('logout behaviors', () => {

    it('should purge accessToken from local storage when logout', () => {
      // Given
      localStorage.setItem('accessToken', '{}');

      // When
      service.logout();

      // Then
      expect(localStorage.getItem('accessToken')).toBeNull();
    });

    it('should redirect to the login page when logout', () => {
      // Given

      // When
      service.logout();

      // Then
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
    });

  });

  describe('login behaviors', () => {

    it('should store accessToken into local storage when logging in', done => {
      // Given
      localStorage.removeItem('accessToken');
      authenticationServiceSpy.authenticationLoginPost.and.callFake(function() {
        return defer(() => Promise.resolve({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' }));
      });

      // When
      service.login('username', 'password').subscribe(() => {
        // Then
        expect(localStorage.getItem('accessToken')).toEqual(JSON.stringify({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' }));
        expect(authenticationServiceSpy.authenticationLoginPost).toHaveBeenCalledWith('username', 'password');
        done();
      });

    });

  });

  describe('accessToken behaviors', () => {

    it('should return access token from local storage', () => {
      // Given
      localStorage.setItem('accessToken', JSON.stringify({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' }));

      // When
      const accessToken: AccessTokenDto = service.accessToken();

      // Then
      expect(accessToken).toEqual({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
    });

    it('should return null when accessToken not present in local storage', () => {
      // Given
      localStorage.removeItem('accessToken');

      // When
      const accessToken: AccessTokenDto = service.accessToken();

      // Then
      expect(accessToken).toBeNull();
    });

  });

  describe('renewToken behaviors', () => {

    beforeEach(() => {
      localStorage.setItem('accessToken', JSON.stringify({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' }));
    });

    it('should call authenticationRefreshTokenPost using refresh token', () => {
      // Given
      authenticationServiceSpy.authenticationRefreshTokenPost.and.callFake(function() {
        return defer(() => Promise.resolve());
      });

      // When
      service.renewToken();

      // Then
      expect(authenticationServiceSpy.authenticationRefreshTokenPost).toHaveBeenCalledWith('refreshToken');
    });

    it('should store new access token after successfully renew it', done => {
      // Given
      authenticationServiceSpy.authenticationRefreshTokenPost.and.callFake(function() {
        return defer(() => Promise.resolve({ 'accessToken': 'newAccessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'newRefreshToken' }));
      });

      // When && Then
      service.renewToken().subscribe(() => {
        expect(localStorage.getItem('accessToken')).toEqual(JSON.stringify({ 'accessToken': 'newAccessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'newRefreshToken' }));
        expect(authenticationServiceSpy.authenticationRefreshTokenPost).toHaveBeenCalled();
        done();
      });
    });

    it('should return new access token after successfully renew it', done => {
      // Given
      authenticationServiceSpy.authenticationRefreshTokenPost.and.callFake(function() {
        return defer(() => Promise.resolve({ 'accessToken': 'newAccessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'newRefreshToken' }));
      });

      // When && Then
      service.renewToken().subscribe((accessToken: AccessTokenDto) => {
        expect(accessToken).toEqual({ 'accessToken': 'newAccessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'newRefreshToken' });
        expect(authenticationServiceSpy.authenticationRefreshTokenPost).toHaveBeenCalled();
        done();
      });
    });

    it('should throw an exception when renewing token is unsuccessful', done => {
      // Given
      authenticationServiceSpy.authenticationRefreshTokenPost.and.returnValue(throwError({ error: { message: 'test-error' }}));

      // When && Then
      service.renewToken().subscribe(
        null,
        error => {
          expect(error).toEqual({ error: { message: 'test-error' }});
          expect(authenticationServiceSpy.authenticationRefreshTokenPost).toHaveBeenCalled();
          done();
        },
        () => {
          fail('Error, the Observable is expected to error and not complete');
        });
    });

    it('should remove access token from local storage when renewing token is unsuccessful', done => {
      // Given
      authenticationServiceSpy.authenticationRefreshTokenPost.and.returnValue(throwError({}));

      // When && Then
      service.renewToken().subscribe(
        null,
        error => {
          expect(localStorage.getItem('accessToken')).toBeNull();
          expect(authenticationServiceSpy.authenticationRefreshTokenPost).toHaveBeenCalled();
          done();
        },
        () => {
          fail('Error, the Observable is expected to error and not complete');
        });
    });

  });

});
