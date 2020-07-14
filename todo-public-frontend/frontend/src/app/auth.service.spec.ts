import { async, TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import { UserService, AccessTokenDto } from 'src/generated';
import { Router } from '@angular/router';
import { defer } from 'rxjs';

describe('AuthService', () => {
  let service: AuthService;

  const userServiceSpy = jasmine.createSpyObj('UserService', ['usersLoginPost']);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

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

  it('should store accessToken into local storage when logging in', (async() => {
    // Given
    localStorage.removeItem('accessToken');
    userServiceSpy.usersLoginPost.and.callFake(function() {
      return defer(() => Promise.resolve({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' }));
    });

    // When
    await service.login('username', 'password');

    // Then
    expect(localStorage.getItem('accessToken')).toEqual(JSON.stringify({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' }));
    expect(userServiceSpy.usersLoginPost).toHaveBeenCalledWith('username', 'password');
  }));

  it('should redirect to the list of todo when logging in', (async() => {
    // Given
    userServiceSpy.usersLoginPost.and.callFake(function() {
      return defer(() => Promise.resolve({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' }));
    });

    // When
    await service.login('username', 'password');

    // Then
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/todos']);
    expect(userServiceSpy.usersLoginPost).toHaveBeenCalled();
  }));

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
