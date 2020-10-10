import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationComponent } from './notification.component';
import { NotificationService } from './notification.service';
import { defer } from 'rxjs';
import { NotificationType } from './notification.model';
import { NbToastrService, NbComponentStatus } from '@nebular/theme';

describe('NotificationComponent', () => {
  let component: NotificationComponent;
  let fixture: ComponentFixture<NotificationComponent>;

  const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['onNotification']);
  const nbToastrServiceSpy = jasmine.createSpyObj('NbToastrService', ['show']);

  beforeEach(async(() => {
    notificationServiceSpy.onNotification.and.callFake(function() {
      return defer(() => Promise.resolve({type: NotificationType.Success, message: 'it works'}));
    });
    TestBed.configureTestingModule({
      declarations: [ NotificationComponent ],
      providers: [
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: NbToastrService, useValue: nbToastrServiceSpy }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    notificationServiceSpy.onNotification.calls.reset();
    nbToastrServiceSpy.show.calls.reset();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should subscribe on notification when component is initialized', () => {
    // Then
    expect(notificationServiceSpy.onNotification).toHaveBeenCalled();
  });

  it('should call snack-bar when receiving a notification', async(() => {
    fixture.whenStable().then(() => {
       expect(nbToastrServiceSpy.show).toHaveBeenCalledWith('success', 'it works', { duration: 5000, status: 'success', preventDuplicates: true });
    })
  }));

});
