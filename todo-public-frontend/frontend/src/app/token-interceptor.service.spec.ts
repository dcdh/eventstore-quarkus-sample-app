import { TestBed, inject } from '@angular/core/testing';
import { HttpClient, HTTP_INTERCEPTORS, HttpErrorResponse, HttpResponse } from "@angular/common/http";

import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";

import { TokenInterceptorService } from './token-interceptor.service';
import { AuthService } from "./auth.service";
import { UserService, TodoService } from 'src/generated';

// https://medium.com/@dev.s4522/how-to-write-unit-test-cases-for-angular-http-interceptor-7595cb3a8843
describe('TokenInterceptorService', () => {
  let service: TokenInterceptorService;
  let httpMock: HttpTestingController;
  const authServiceSpy = jasmine.createSpyObj('AuthService', ['accessToken', 'logout']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [
        UserService,
        TodoService,
        { provide: AuthService, useValue: authServiceSpy },
        { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptorService, multi: true }
      ]
    });
    service = TestBed.inject(TokenInterceptorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add authorization bearer token when calling user service', inject([UserService], (userService: UserService) => {
    // Given
    authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });

    // When
    userService.usersMeGet().subscribe(res => {
      expect(res).toBeTruthy();
    });

    // Then
    const httpReq = httpMock.expectOne('https://localhost/users/me');
    expect(httpReq.request.headers.get('Authorization')).toEqual('Bearer accessToken');
    expect(authServiceSpy.accessToken).toHaveBeenCalled();
  }));

  it('should add authorization bearer token when retrieving list of todo', inject([TodoService], (todoService: TodoService) => {
    // Given
    authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });

    // When
    todoService.todosGet().subscribe(res => {
      expect(res).toBeTruthy();
    });

    // Then
    const httpReq = httpMock.expectOne('https://localhost/todos');
    expect(httpReq.request.headers.get('Authorization')).toEqual('Bearer accessToken');
    expect(authServiceSpy.accessToken).toHaveBeenCalled();
  }));

  it('should add authorization bearer token when creating a new todo', inject([TodoService], (todoService: TodoService) => {
    // Given
    authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });

    // When
    todoService.todosCreateNewTodoPost('description').subscribe(res => {
      expect(res).toBeTruthy();
    });

    // Then
    const httpReq = httpMock.expectOne('https://localhost/todos/createNewTodo');
    expect(httpReq.request.headers.get('Authorization')).toEqual('Bearer accessToken');
    expect(authServiceSpy.accessToken).toHaveBeenCalled();
  }));

  it('should not add authorization bearer token when call login service', inject([UserService], (userService: UserService) => {
    // Given
    authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });

    // When
    userService.usersLoginPost('username', 'password').subscribe(res => {
      expect(res).toBeTruthy();
    });

    // Then
    const httpReq = httpMock.expectOne('https://localhost/users/login');
    expect(httpReq.request.headers.has('Authorization')).toEqual(false);
    expect(authServiceSpy.accessToken).toHaveBeenCalled();
  }));

  it('should add authorization bearer token when access token is present', inject([HttpClient], (http: HttpClient) => {
    // Given
    authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });

    // When
    http.get('/fake').subscribe(res => { expect(res).toBeTruthy() });

    // Then
    const httpReq = httpMock.expectOne('/fake');
    expect(httpReq.request.headers.get('Authorization')).toEqual('Bearer accessToken');
    expect(authServiceSpy.accessToken).toHaveBeenCalled();
  }));

  it('should not add authorization bearer token when access token is not available', inject([HttpClient], (http: HttpClient) => {
    // Given
    authServiceSpy.accessToken.and.returnValue(null);

    // When
    http.get('/fake').subscribe(res => { expect(res).toBeTruthy() });

    // Then
    const httpReq = httpMock.expectOne('/fake');
    expect(httpReq.request.headers.has('Authorization')).toEqual(false);
    expect(authServiceSpy.accessToken).toHaveBeenCalled();
  }));

  it('should logout when user request execution is forbidden', inject([HttpClient], (http: HttpClient) => {
    // Given
    authServiceSpy.accessToken.and.returnValue({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' });

    // When
    http.get('/fake').subscribe(res => res, err => err);

    // Then
    const httpReq = httpMock.expectOne('/fake');
    httpReq.flush('forbidden', new HttpErrorResponse({ error: '403 error', status: 403, statusText: 'Forbidden' }));
    expect(httpReq.request.headers.get('Authorization')).toEqual('Bearer accessToken');
    expect(authServiceSpy.accessToken).toHaveBeenCalled();
    expect(authServiceSpy.logout).toHaveBeenCalled();
  }));

});
