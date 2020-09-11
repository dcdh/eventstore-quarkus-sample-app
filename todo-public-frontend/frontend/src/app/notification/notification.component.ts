import { Component, OnInit, OnDestroy } from '@angular/core';
import { NotificationService } from './notification.service';
import { Subscription } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent implements OnInit, OnDestroy {

  notificationSubscription: Subscription;

  constructor(private notificationService: NotificationService, private matSnackBar: MatSnackBar) {}

  ngOnInit(): void {
    // subscribe to new notifications
    this.notificationSubscription = this.notificationService.onNotification()
      .subscribe(notification => {
        this.matSnackBar.open(notification.message, null, {
          duration: 5000,
          horizontalPosition: 'start',
          verticalPosition: 'bottom',
        });
      });
  }

  ngOnDestroy(): void {
    // unsubscribe to avoid memory leaks
    this.notificationSubscription.unsubscribe();
  }

}
