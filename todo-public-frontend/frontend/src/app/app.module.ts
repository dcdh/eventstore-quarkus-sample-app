import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TodoComponent } from './todo/todo.component';

import { ApiModule } from 'src/generated';
import { TodoListComponent } from './todo-list/todo-list.component';

import { HttpClientModule } from '@angular/common/http';
import { BASE_PATH } from 'src/generated';
import { environment } from '../environments/environment';
import { ConnectedUserComponent } from './connected-user/connected-user.component';
import { NotificationComponent } from './notification/notification.component';
import { LogoutComponent } from './logout/logout.component';
import { NbThemeModule } from '@nebular/theme';
import { NbAuthModule } from './auth/auth.module';

@NgModule({
  declarations: [
    AppComponent,
    TodoComponent,
    TodoListComponent,
    ConnectedUserComponent,
    NotificationComponent,
    LogoutComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatSnackBarModule,
    ApiModule,
    HttpClientModule,
    NbAuthModule,
    NbThemeModule.forRoot({ name: 'dark' })
  ],
  providers: [
    { provide: BASE_PATH, useValue: environment.API_BASE_PATH },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
