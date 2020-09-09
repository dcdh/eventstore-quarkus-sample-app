import { Component, OnInit, OnDestroy } from '@angular/core';
import { NotificationService } from './notification.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent implements OnInit, OnDestroy {

  notificationSubscription: Subscription;

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    // subscribe to new notifications
    this.notificationSubscription = this.notificationService.onNotification()
      .subscribe(notification => {
        // TODO toaster !
        console.error(notification);
      });
  }

  ngOnDestroy(): void {
    // unsubscribe to avoid memory leaks
    this.notificationSubscription.unsubscribe();
  }

}
