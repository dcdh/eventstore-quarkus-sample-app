import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { ApiModule } from 'src/generated';

import { HttpClientModule } from '@angular/common/http';
import { BASE_PATH } from 'src/generated';
import { environment } from '../environments/environment';
import { NotificationComponent } from './notification/notification.component';
import { NbThemeModule } from '@nebular/theme';
import { NbAuthModule } from './auth/auth.module';
import { DashboardModule } from './dashboard/dashboard.module';
import { NbEvaIconsModule } from '@nebular/eva-icons';

@NgModule({
  declarations: [
    AppComponent,
    NotificationComponent
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
    DashboardModule,
    NbThemeModule.forRoot({ name: 'default' }),
    NbEvaIconsModule
  ],
  providers: [
    { provide: BASE_PATH, useValue: environment.API_BASE_PATH },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
