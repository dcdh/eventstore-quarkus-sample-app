import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import { AuthService } from "./../auth.service";

import { DebugElement } from "@angular/core";
import { By } from "@angular/platform-browser";

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LoginComponent ],
      providers: [ { provide: AuthService, useValue: authServiceSpy } ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call login from authService when log in', () => {
    // Given
    component.loginForm.controls['username'].setValue("damdamdeo");
    component.loginForm.controls['password'].setValue("123456789");

    // When
    component.onLogin();

    // Then
    expect(authServiceSpy.login).toHaveBeenCalledWith('damdamdeo', '123456789');
  });

  // TODO add a test to ensure that form is submit when clicking on login button

});
