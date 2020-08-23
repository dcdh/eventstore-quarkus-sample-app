import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectedUserComponent } from './connected-user.component';
import { AuthenticationService, UserDTO } from 'src/generated';

import { defer } from 'rxjs';
import { DebugElement } from "@angular/core";
import { By } from "@angular/platform-browser";

describe('ConnectedUserComponent', () => {
  let component: ConnectedUserComponent;
  let fixture: ComponentFixture<ConnectedUserComponent>;

  const authenticationServiceSpy = jasmine.createSpyObj('AuthenticationService', ['authenticationMeGet']);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConnectedUserComponent ],
      providers: [ { provide: AuthenticationService, useValue: authenticationServiceSpy } ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    authenticationServiceSpy.authenticationMeGet.and.callFake(function() {
      return defer(() => Promise.resolve({ anonymous: false, attributes: {}, roles: ['frontend-user'], userName: 'damdamdeo' }));
    });
    fixture = TestBed.createComponent(ConnectedUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should retrieve user on init', async(() => {
    fixture.whenStable().then(() => {
      expect(component.user).toEqual({ anonymous: false, attributes: {}, roles: ['frontend-user'], userName: 'damdamdeo' });
      expect(authenticationServiceSpy.authenticationMeGet).toHaveBeenCalled();
    })
  }));

  it('should display username connected', async(() => {
    fixture.whenStable().then(() => {
      fixture.detectChanges();

      const usernameEl: HTMLInputElement = fixture.debugElement.nativeElement.querySelector('strong');
      expect(usernameEl.innerText).toEqual('damdamdeo');
      expect(authenticationServiceSpy.authenticationMeGet).toHaveBeenCalled();
    })
  }));

// TODO cas ou une exception est émise !!!
});
