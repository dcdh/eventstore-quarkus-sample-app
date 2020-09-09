import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationComponent } from './notification.component';
import { NotificationService } from './notification.service';
import { defer } from 'rxjs';

describe('NotificationComponent', () => {
  let component: NotificationComponent;
  let fixture: ComponentFixture<NotificationComponent>;

  const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['onNotification']);

  beforeEach(async(() => {
    notificationServiceSpy.onNotification.and.callFake(function() {
      return defer(() => Promise.resolve({}));
    });
    TestBed.configureTestingModule({
      declarations: [ NotificationComponent ],
      providers: [
        { provide: NotificationService, useValue: notificationServiceSpy }
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

  // TODO rajouter un test lorsque le snackbar sera implementé


});
