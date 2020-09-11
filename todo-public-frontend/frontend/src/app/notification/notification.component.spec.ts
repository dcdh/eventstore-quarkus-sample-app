import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationComponent } from './notification.component';
import { NotificationService } from './notification.service';
import { defer } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NotificationType } from './notification.model';

describe('NotificationComponent', () => {
  let component: NotificationComponent;
  let fixture: ComponentFixture<NotificationComponent>;

  const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['onNotification']);
  const matSnackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

  beforeEach(async(() => {
    notificationServiceSpy.onNotification.and.callFake(function() {
      return defer(() => Promise.resolve({type: NotificationType.Success, message: 'it works'}));
    });
    TestBed.configureTestingModule({
      declarations: [ NotificationComponent ],
      providers: [
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: MatSnackBar, useValue: matSnackBarSpy }
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
      expect(matSnackBarSpy.open).toHaveBeenCalledWith('it works', null, {
        duration: 5000,
        horizontalPosition: 'start',
        verticalPosition: 'bottom',
      });
    })
  }));

});
