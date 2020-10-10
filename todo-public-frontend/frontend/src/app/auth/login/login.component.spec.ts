import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import { AuthService } from "./../auth.service";
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { DebugElement, ChangeDetectorRef } from "@angular/core";
import { By } from "@angular/platform-browser";
import { Router } from '@angular/router';
import { defer } from 'rxjs';

// https://stackoverflow.com/questions/39712150/angular2-formbuilder-unit-testing
describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
  const changeDetectorRefSpy = jasmine.createSpyObj('ChangeDetectorRef', ['detectChanges']);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LoginComponent ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: FormBuilder },
        { provide: ChangeDetectorRef, useValue: changeDetectorRefSpy }
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
    changeDetectorRefSpy.detectChanges.calls.reset();
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
    component.login();

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
    component.login();

    // Then
    fixture.whenStable().then(() => {
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/dashboard/todos']);
      expect(authServiceSpy.login).toHaveBeenCalled();
    });
  }));

  // TODO add a test to ensure that form is submit when clicking on login button

});
