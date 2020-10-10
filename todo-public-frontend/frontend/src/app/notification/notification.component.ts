import { Component, OnInit, OnDestroy } from '@angular/core';
import { NotificationService } from './notification.service';
import { Subscription } from 'rxjs';
import { NbToastrService, NbComponentStatus } from '@nebular/theme';
import { NotificationType } from './notification.model';
import { delay } from 'rxjs/operators';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent implements OnInit, OnDestroy {

  notificationSubscription: Subscription;

  constructor(private notificationService: NotificationService, private nbToastrService: NbToastrService) {}

  ngOnInit(): void {
    // subscribe to new notifications
    this.notificationSubscription = this.notificationService.onNotification()
      .pipe(delay(50))// I add to add a delay to avoid this race condition error in console "this.container is undefined" from auth.guard
      .subscribe(notification => {
        let status : NbComponentStatus;
        switch (notification.type) {
          case NotificationType.Success:
            status = 'success';
            break;
          case NotificationType.Error:
            status = 'danger';
            break;
          case NotificationType.Info:
            status = 'info';
            break;
          case NotificationType.Warning:
            status = 'warning';
            break;
        }
        this.nbToastrService.show(status, notification.message, { duration: 5000, status: status, preventDuplicates: true });
      });
  }

  ngOnDestroy(): void {
    // unsubscribe to avoid memory leaks
    this.notificationSubscription.unsubscribe();
  }

}
