import { TestBed, inject, fakeAsync, tick, flush } from '@angular/core/testing';
import { HttpClient, HTTP_INTERCEPTORS, HttpErrorResponse, HttpResponse } from "@angular/common/http";

import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";

import { AuthInterceptorService } from './auth-interceptor.service';
import { AuthService } from "./auth.service";
import { AuthenticationService, TodoService } from 'src/generated';

// https://medium.com/@dev.s4522/how-to-write-unit-test-cases-for-angular-http-interceptor-7595cb3a8843
describe('AuthInterceptorService', () => {
  let authInterceptorService: AuthInterceptorService;
  let authenticationService: AuthenticationService;
  let todoService: TodoService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  const authServiceSpy = jasmine.createSpyObj('AuthService', ['accessToken', 'logout', 'renewToken']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [
        AuthenticationService,
        TodoService,
        { provide: AuthService, useValue: authServiceSpy },
        { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptorService, multi: true }
      ]
    });
    authInterceptorService = TestBed.inject(AuthInterceptorService);
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
  });

  it('should be created', () => {
    expect(authInterceptorService).toBeTruthy();
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

    it('should not add authorization bearer token when call login service', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      authenticationService.authenticationLoginPost('username', 'password').subscribe(res => {
        expect(res).toBeTruthy();
      });

      // When
      const httpReq = httpTestingController.expectOne('https://localhost/authentication/login');
      httpReq.flush({});

      // Then
      expect(httpReq.request.headers.has('Authorization')).toEqual(false);
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
    }));

    it('should add authorization bearer token when access token is present', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      httpClient.get('/fake').subscribe(res => {
        expect(res).toBeTruthy();
      });

      // When
      const httpReq = httpTestingController.expectOne('/fake');
      httpReq.flush({});

      // Then
      expect(httpReq.request.headers.get('Authorization')).toEqual('Bearer accessToken');
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
    }));

    it('should not add authorization bearer token when access token is not available', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue(null);
      httpClient.get('/fake').subscribe(res => {
        expect(res).toBeTruthy();
      });

      // When
      const httpReq = httpTestingController.expectOne('/fake');
      httpReq.flush({});

      // Then
      expect(httpReq.request.headers.has('Authorization')).toEqual(false);
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
    }));

  });

  describe('logout behaviors', () => {

    it('should call authentication service renewToken when user request execution is forbidden', fakeAsync(() => {
      // Given
      authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });
      httpClient.get('/fake').subscribe(
        res => {},
        err => {
          expect(err).toBeTruthy();
        });

      // When
      const httpReq = httpTestingController.expectOne('/fake');
      httpReq.flush('forbidden', new HttpErrorResponse({ error: '403 error', status: 403, statusText: 'Forbidden' }));

      // Then
      expect(authServiceSpy.renewToken).toHaveBeenCalled();
      expect(httpReq.request.headers.get('Authorization')).toEqual('Bearer accessToken');
      expect(authServiceSpy.accessToken).toHaveBeenCalled();
    }));

  });

});
