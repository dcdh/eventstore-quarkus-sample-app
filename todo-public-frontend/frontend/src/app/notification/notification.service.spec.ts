import { TestBed } from '@angular/core/testing';

import { Observable, Subscription } from 'rxjs';

import { NotificationService } from './notification.service';
import { Notification, NotificationType } from './notification.model';

describe('NotificationService', () => {
  let service: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call expected notification on success', () => {
    // Given
    const notification: Subscription = service.onNotification()
      .subscribe(notification => {
        // Then
        expect(notification).toEqual(new Notification({type: NotificationType.Success, message: 'It is a success'}));
      });

    // When
    service.success('It is a success');
  });

  it('should call expected notification on error', () => {
    // Given
    const notification: Subscription = service.onNotification()
      .subscribe(notification => {
        // Then
        expect(notification).toEqual(new Notification({type: NotificationType.Error, message: 'It fails'}));
      });

    // When
    service.error('It fails');
  });

  it('should call expected notification on info', () => {
    // Given
    const notification: Subscription = service.onNotification()
      .subscribe(notification => {
        // Then
        expect(notification).toEqual(new Notification({type: NotificationType.Info, message: 'It works'}));
      });

    // When
    service.info('It works');
  });

  it('should call expected notification on warn', () => {
    // Given
    const notification: Subscription = service.onNotification()
      .subscribe(notification => {
        // Then
        expect(notification).toEqual(new Notification({type: NotificationType.Warning, message: 'It may fails'}));
      });

    // When
    service.warn('It may fails');
  });

});
