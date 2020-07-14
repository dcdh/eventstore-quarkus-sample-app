import { Location } from "@angular/common";
import { TestBed, fakeAsync } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { Router } from "@angular/router";
import { HttpClient, HttpHandler } from "@angular/common/http";

import { routes } from "./app-routing.module";
import { UserService } from 'src/generated';
import { AuthGuard } from "./auth.guard";
import { AuthService } from "./auth.service";

describe("Router: App", () => {
  let location: Location;
  let router: Router;

  const authServiceSpy = jasmine.createSpyObj('AuthService', ['isLoggedIn']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ RouterTestingModule.withRoutes(routes) ],
      providers: [
        UserService,
        HttpClient,
        HttpHandler,
        AuthGuard,
        { provide: AuthService, useValue: authServiceSpy }
      ]
    })
    .compileComponents();

    router = TestBed.get(Router);
    location = TestBed.get(Location);
  });

  it('should navigate to "" redirect to /todos when user is authenticated', (done) => {
    // Given
    authServiceSpy.isLoggedIn.and.returnValue(true);

    // When && Then
    router.navigate(['']).then(() => {
      expect(location.path()).toBe('/todos');
      expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
      done();
    });
  });

  it('should navigate to "" redirect to /login when user is not authenticated', (done) => {
    // Given
    authServiceSpy.isLoggedIn.and.returnValue(false);

    // When && Then
    router.navigate(['']).then(() => {
      expect(location.path()).toBe('/login');
      expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
      done();
    });
  });

  it('should navigate to "todos" redirect to /todos when user is authenticated', (done) => {
    // Given
    authServiceSpy.isLoggedIn.and.returnValue(true);

    // When && Then
    router.navigate(['todos']).then(() => {
      expect(location.path()).toBe('/todos');
      expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
      done();
    });
  });

  it('should navigate to "todos" redirect to /login when user is not authenticated', (done) => {
    // Given
    authServiceSpy.isLoggedIn.and.returnValue(false);

    // When && Then
    router.navigate(['todos']).then(() => {
      expect(location.path()).toBe('/login');
      expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
      done();
    });
  });

  it('should navigate to "login" redirect to /login when user is authenticated', (done) => {
    // Given
    authServiceSpy.isLoggedIn.and.returnValue(true);

    // When && Then
    router.navigate(['login']).then(() => {
      expect(location.path()).toBe('/login');
      expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
      done();
    });
  });

  it('should navigate to "login" redirect to /login when user is not authenticated', (done) => {
    // Given
    authServiceSpy.isLoggedIn.and.returnValue(false);

    // When && Then
    router.navigate(['login']).then(() => {
      expect(location.path()).toBe('/login');
      expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
      done();
    });
  });

});
