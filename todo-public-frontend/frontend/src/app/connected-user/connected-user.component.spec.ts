import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectedUserComponent } from './connected-user.component';
import { UserService, UserDTO } from 'src/generated';

import { defer } from 'rxjs';
import { DebugElement } from "@angular/core";
import { By } from "@angular/platform-browser";

describe('ConnectedUserComponent', () => {
  let component: ConnectedUserComponent;
  let fixture: ComponentFixture<ConnectedUserComponent>;

  const userServiceSpy = jasmine.createSpyObj('UserService', ['usersMeGet']);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConnectedUserComponent ],
      providers: [ { provide: UserService, useValue: userServiceSpy } ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    userServiceSpy.usersMeGet.and.callFake(function() {
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
      expect(userServiceSpy.usersMeGet).toHaveBeenCalled();
    })
  }));

  it('should display username connected', () => {
    fixture.whenStable().then(() => {
      fixture.detectChanges();

      const usernameEl: HTMLInputElement = fixture.debugElement.nativeElement.querySelector('strong');
      expect(usernameEl.innerText).toEqual('damdamdeo');
      expect(userServiceSpy.usersMeGet).toHaveBeenCalled();
    })
  });

// TODO cas ou une exception est émise !!!
});
