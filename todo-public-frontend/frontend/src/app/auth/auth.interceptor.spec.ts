import { TestBed, inject, fakeAsync, tick, flush } from '@angular/core/testing';
import { HttpClient, HTTP_INTERCEPTORS, HttpErrorResponse, HttpResponse } from "@angular/common/http";

import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";

import { AuthInterceptor } from './auth.interceptor';
import { AuthService } from "./auth.service";
import { AuthenticationService, TodoService } from 'src/generated';
import { Router } from '@angular/router';
import { defer } from 'rxjs';
import { NotificationService } from './../notification/notification.service';

// https://medium.com/@dev.s4522/how-to-write-unit-test-cases-for-angular-http-interceptor-7595cb3a8843
describe('AuthInterceptor', () => {
  let authInterceptor: AuthInterceptor;
  let authenticationService: AuthenticationService;
  let todoService: TodoService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  const authServiceSpy = jasmine.createSpyObj('AuthService', ['accessToken', 'logout', 'renewToken']);
  const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['info']);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [
        AuthenticationService,
        TodoService,
        { provide: AuthService, useValue: authServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
        { provide: Router, useValue: routerSpy }
      ]
    });
    authInterceptor = TestBed.inject(AuthInterceptor);
    authenticationService = TestBed.inject(AuthenticationService);
    todoService = TestBed.inject(TodoService);
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
    authServiceSpy.accessToken.calls.reset();
    authServiceSpy.logout.calls.reset();
    authServiceSpy.renewToken.calls.reset();
    notificationServiceSpy.info.calls.reset();
    routerSpy.navigate.calls.reset();
  });

  it('should be created', () => {
    expect(authInterceptor).toBeTruthy();
  });

  describe('authentication bearer behaviors', () => {

    // https://guide-angular.wishtack.io/angular/testing/unit-testing/unit-test-et-httpclient
    it('should add authorization bearer token when calling authentication service', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      authenticationService.authenticationMeGet().subscribe(res => {
        expect(res).toBeTruthy();
      });

      // When
      const httpReq = httpTestingController.expectOne('https://localhost/authentication/me');
      httpReq.flush({});

      // Then
      expect(httpReq.request.headers.get('Authorization')).toEqual('Bearer accessToken');
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
    }));

    it('should add authorization bearer token when retrieving list of todo', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      todoService.todosGet().subscribe(res => {
        expect(res).toBeTruthy();
      });

      // When
      const httpReq = httpTestingController.expectOne('https://localhost/todos');
      httpReq.flush({});

      // Then
      expect(httpReq.request.headers.get('Authorization')).toEqual('Bearer accessToken');
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
    }));

    it('should add authorization bearer token when creating a new todo', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      todoService.todosCreateNewTodoPost('description').subscribe(res => {
        expect(res).toBeTruthy();
      });

      // When
      const httpReq = httpTestingController.expectOne('https://localhost/todos/createNewTodo');
      httpReq.flush({});

      // Then
      expect(httpReq.request.headers.get('Authorization')).toEqual('Bearer accessToken');
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
    }));

    it('should add authorization bearer token when access token is present', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      httpClient.get('/fake1').subscribe(res => {
        expect(res).toBeTruthy();
      });

      // When
      const httpReq = httpTestingController.expectOne('/fake1');
      httpReq.flush({});

      // Then
      expect(httpReq.request.headers.get('Authorization')).toEqual('Bearer accessToken');
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
    }));

    it('should not add authorization bearer token when access token is not available', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue(null);
      httpClient.get('/fake2').subscribe(res => {
        expect(res).toBeTruthy();
      });

      // When
      const httpReq = httpTestingController.expectOne('/fake2');
      httpReq.flush({});

      // Then
      expect(httpReq.request.headers.has('Authorization')).toEqual(false);
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
    }));

    it('should throw exception when request return an error response other than 401', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      httpClient.get('/fake3').subscribe(
        res => {},
        err => {
          expect(err).toBeTruthy();
        });

      // When
      const httpReq = httpTestingController.expectOne('/fake3');
      httpReq.flush('Internal Server Error ', new HttpErrorResponse({ error: 'Internal Server Error ', status: 500, statusText: 'Internal Server Error ' }));

      // Then
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
    }));

  });

  describe('token renewal behaviors', () => {

    it('should not activate renewal token when call login service', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      httpClient.get('https://localhost/authentication/login').subscribe(
        res => {},
        err => {
          // Then
          expect(err).toBeTruthy();
        });

      // When
      const httpReq = httpTestingController.expectOne('https://localhost/authentication/login');
      httpReq.flush('unauthorized', new HttpErrorResponse({ error: '401 error', status: 401, statusText: 'Unauthorized' }));
      tick(50);// wait for the second http request to be executed

      // Then
      expect(authServiceSpy.renewToken).not.toHaveBeenCalled();
      expect(httpReq.request.headers.get('Authorization')).not.toBeTruthy();
      expect(authServiceSpy.accessToken).not.toHaveBeenCalled();
    }));

    it('should not activate renewal token when call refresh token service', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      httpClient.get('https://localhost/authentication/refresh-token').subscribe(
        res => {},
        err => {
          // Then
          expect(err).toBeTruthy();
        });

      // When
      const httpReq = httpTestingController.expectOne('https://localhost/authentication/refresh-token');
      httpReq.flush('unauthorized', new HttpErrorResponse({ error: '401 error', status: 401, statusText: 'Unauthorized' }));
      tick(50);// wait for the second http request to be executed

      // Then
      expect(authServiceSpy.renewToken).not.toHaveBeenCalled();
      expect(httpReq.request.headers.get('Authorization')).not.toBeTruthy();
      expect(authServiceSpy.accessToken).not.toHaveBeenCalled();
    }));

    it('should call authentication service renewToken when user request execution is unauthorized', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      httpClient.get('/fake4').subscribe(
        res => {},
        err => {
          // Then
          expect(err).toBeTruthy();
        });

      // When
      const httpReq = httpTestingController.expectOne('/fake4');
      httpReq.flush('unauthorized', new HttpErrorResponse({ error: '401 error', status: 401, statusText: 'Unauthorized' }));
      tick(50);// wait for the second http request to be executed

      // Then
      expect(authServiceSpy.renewToken).toHaveBeenCalled();
      expect(httpReq.request.headers.get('Authorization')).toEqual('Bearer accessToken');
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
    }));

    it('should add renew access token authorization bearer when user request execution is unauthorized', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      authServiceSpy.renewToken.and.callFake(function() {
        return defer(() => Promise.resolve({ 'accessToken': 'newAccessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'newRefreshToken' }));
      });
      httpClient.get('/fake5').subscribe(
        res => {},
        err => {});
      const firstHttpReq = httpTestingController.expectOne('/fake5');
      firstHttpReq.flush('unauthorized', new HttpErrorResponse({ error: '401 error', status: 401, statusText: 'Unauthorized' }));

      // When
      tick(50);// wait for the second http request to be executed

      // Then
      const secondHttpReq = httpTestingController.expectOne('/fake5');

      // Then
      expect(secondHttpReq.request.headers.get('Authorization')).toEqual('Bearer newAccessToken');
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
      expect(authServiceSpy.renewToken).toHaveBeenCalled();
    }));

    it('should notify user when token fails to be renew', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      authServiceSpy.renewToken.and.callFake(function() {
        return defer(() => Promise.reject({}));
      });
      httpClient.get('/fake6').subscribe(
        res => {},
        err => {
          // Then
          fail("Callback has been called");
        });
      const firstHttpReq = httpTestingController.expectOne('/fake6');
      firstHttpReq.flush('unauthorized', new HttpErrorResponse({ error: '401 error', status: 401, statusText: 'Unauthorized' }));

      // When
      tick(50);// wait for the second http request to be executed

      // Then
      expect(notificationServiceSpy.info).toHaveBeenCalledWith('The action cannot be made because your session is closed. You need to login and retry again.');
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
      expect(authServiceSpy.renewToken).toHaveBeenCalled();
    }));

    it('should redirect to login page when token fails to be renew', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      authServiceSpy.renewToken.and.callFake(function() {
        return defer(() => Promise.reject({}));
      });
      httpClient.get('/fake7').subscribe(
        res => {},
        err => {});
      const firstHttpReq = httpTestingController.expectOne('/fake7');
      firstHttpReq.flush('unauthorized', new HttpErrorResponse({ error: '401 error', status: 401, statusText: 'Unauthorized' }));

      // When
      tick(50);// wait for the second http request to be executed

      // Then
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/auth/login']);
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
      expect(authServiceSpy.renewToken).toHaveBeenCalled();
    }));

  });

  describe('request re-executed following token renewal behaviors', () => {

    it('should throw exception when request fails to be re-executed', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      authServiceSpy.renewToken.and.callFake(function() {
        return defer(() => Promise.resolve({ 'accessToken': 'newAccessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'newRefreshToken' }));
      });
      httpClient.get('/fake8').subscribe(
        res => {},
        err => {
        // Then
        expect(err).toBeTruthy();
      });
      const firstHttpReq = httpTestingController.expectOne('/fake8');
      firstHttpReq.flush('unauthorized', new HttpErrorResponse({ error: '401 error', status: 401, statusText: 'Unauthorized' }));

      // When
      tick(50);// wait for the second http request to be executed
      const secondHttpReq = httpTestingController.expectOne('/fake8');
      secondHttpReq.flush('Internal Server Error ', new HttpErrorResponse({ error: 'Internal Server Error ', status: 500, statusText: 'Internal Server Error ' }));

      // Then
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
      expect(authServiceSpy.renewToken).toHaveBeenCalled();
    }));

  });

});
