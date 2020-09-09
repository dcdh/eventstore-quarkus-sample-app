import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

import { Notification, NotificationType } from './notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private subject = new Subject<Notification>();

  // enable subscribing to notifications observable
  onNotification(): Observable<Notification> {
    return this.subject.asObservable();
  }

  // convenience methods
  success(message: string) {
    this.notify(new Notification({type: NotificationType.Success, message}));
  }

  error(message: string) {
    this.notify(new Notification({type: NotificationType.Error, message}));
  }

  info(message: string) {
    this.notify(new Notification({type: NotificationType.Info, message}));
  }

  warn(message: string) {
    this.notify(new Notification({type: NotificationType.Warning, message}));
  }

  // main notification method
  notify(notification: Notification) {
    this.subject.next(notification);
  }

}
