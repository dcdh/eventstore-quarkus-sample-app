import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import { AuthService } from "./../auth.service";

import { DebugElement } from "@angular/core";
import { By } from "@angular/platform-browser";
import { Router } from '@angular/router';
import { defer } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LoginComponent ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    authServiceSpy.login.calls.reset();
    routerSpy.navigate.calls.reset();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call login from authService when log in', async(() => {
    // Given
    authServiceSpy.login.and.callFake(function() {
      return defer(() => Promise.resolve({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' }));
    });

    component.loginForm.controls['username'].setValue("damdamdeo");
    component.loginForm.controls['password'].setValue("123456789");

    // When
    component.onLogin();

    // Then
    fixture.whenStable().then(() => {
      expect(authServiceSpy.login).toHaveBeenCalledWith('damdamdeo', '123456789');
    });
  }));

  it('should redirect to the list of todo when logging in', async(() => {
    // Given
    authServiceSpy.login.and.callFake(function() {
      return defer(() => Promise.resolve({ 'accessToken': 'accessToken', 'expiresIn': 300, 'refreshExpiresIn': 1800, 'refreshToken': 'refreshToken' }));
    });

    // When
    component.onLogin();

    // Then
    fixture.whenStable().then(() => {
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/todos']);
      expect(authServiceSpy.login).toHaveBeenCalled();
    });
  }));

  // TODO add a test to ensure that form is submit when clicking on login button

});
