import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LogoutComponent } from './logout.component';
import { AuthService } from "./../auth.service";
import { Router } from '@angular/router';

import { DebugElement } from "@angular/core";
import { By } from "@angular/platform-browser";

describe('LogoutComponent', () => {
  let component: LogoutComponent;
  let fixture: ComponentFixture<LogoutComponent>;
  let buttonEl: DebugElement;

  const authServiceSpy = jasmine.createSpyObj('AuthService', ['logout']);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LogoutComponent ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LogoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    buttonEl = fixture.debugElement.query(By.css('button'));
  });

  afterEach(() => {
    authServiceSpy.logout.calls.reset();
    routerSpy.navigate.calls.reset();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call authentication service logout when logout', async(() => {
    // Given

    // When
    component.onLogout();

    // Then
    fixture.whenStable().then(() => {
      expect(authServiceSpy.logout).toHaveBeenCalledWith();
    });
  }));

  it('should redirect to login page when logout', async(() => {
    // Given

    // When
    component.onLogout();

    // Then
    fixture.whenStable().then(() => {
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
    });
  }));

  it('should click on logout has to logout the user', async(() => {
    // Given

    // When
    buttonEl.triggerEventHandler('click', null);

    // Then
    fixture.whenStable().then(() => {
      expect(authServiceSpy.logout).toHaveBeenCalledWith();
    });
  }));

});
