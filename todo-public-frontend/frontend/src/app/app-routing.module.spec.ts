import { Location } from "@angular/common";
import { TestBed, async } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { Router } from "@angular/router";
import { HttpClient, HttpHandler } from "@angular/common/http";

import { routes as appRoutes } from "./app-routing.module";
import { routes as authRoutes } from "./auth/auth-routing.module";
import { routes as dashboardRoutes } from "./dashboard/dashboard-routing.module";

import { AuthenticationService } from 'src/generated';
import { AuthGuard } from "./auth/auth.guard";
import { AuthService } from "./auth/auth.service";

describe("Router: App", () => {
  let location: Location;
  let router: Router;

  const authServiceSpy = jasmine.createSpyObj('AuthService', ['isLoggedIn']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes(appRoutes),
        RouterTestingModule.withRoutes(authRoutes),
        RouterTestingModule.withRoutes(dashboardRoutes)
      ],
      providers: [
        AuthenticationService,
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

  afterEach(() => {
    authServiceSpy.isLoggedIn.calls.reset();
  });

  it('should navigate to "" redirect to /dashboard/todos when user is authenticated', done => {
    // Given
    authServiceSpy.isLoggedIn.and.returnValue(true);

    // When && Then
    router.navigate(['']).then(() => {
      expect(location.path()).toBe('/dashboard/todos');
      expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
      done();
    });
  });

  it('should navigate to "" redirect to /auth/login when user is not authenticated', done => {
    // Given
    authServiceSpy.isLoggedIn.and.returnValue(false);

    // When && Then
    router.navigate(['']).then(() => {
      expect(location.path()).toBe('/auth/login');
      expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
      done();
    });
  });

  it('should navigate to "/dashboard/todos" redirect to /dashboard/todos when user is authenticated', done => {
    // Given
    authServiceSpy.isLoggedIn.and.returnValue(true);

    // When && Then
    router.navigate(['/dashboard/todos']).then(() => {
      expect(location.path()).toBe('/dashboard/todos');
      expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
      done();
    });
  });

  it('should navigate to "/dashboard/todos" redirect to /auth/login when user is not authenticated', done => {
    // Given
    authServiceSpy.isLoggedIn.and.returnValue(false);

    // When && Then
    router.navigate(['/dashboard/todos']).then(() => {
      expect(location.path()).toBe('/auth/login');
      expect(authServiceSpy.isLoggedIn).toHaveBeenCalled();
      done();
    });
  });

  it('should navigate to "/auth/login" redirect to /auth/login when user is authenticated', done => {
    // Given

    // When && Then
    router.navigate(['/auth/login']).then(() => {
      expect(location.path()).toBe('/auth/login');
      expect(authServiceSpy.isLoggedIn).not.toHaveBeenCalled();
      done();
    });
  });

  it('should navigate to "/auth/login" redirect to /auth/login when user is not authenticated', done => {
    // Given

    // When && Then
    router.navigate(['/auth/login']).then(() => {
      expect(location.path()).toBe('/auth/login');
      expect(authServiceSpy.isLoggedIn).not.toHaveBeenCalled();
      done();
    });
  });

});
