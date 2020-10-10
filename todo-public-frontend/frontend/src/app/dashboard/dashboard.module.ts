import { Injector, ModuleWithProviders, NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';

import {
  NbAlertModule,
  NbButtonModule,
  NbCardModule,
  NbCheckboxModule,
  NbIconModule,
  NbInputModule,
  NbLayoutModule,
  NbListModule,
  NbActionsModule,
} from '@nebular/theme';

import { DashboardComponent } from './dashboard.component';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { TodoComponent } from './todo/todo.component';
import { TodoListComponent } from './todo-list/todo-list.component';
import { NbAuthModule } from './../auth/auth.module';

@NgModule({
  imports: [
    BrowserModule,
    DashboardRoutingModule,
    NbLayoutModule,
    NbCardModule,
    NbCheckboxModule,
    NbAlertModule,
    NbInputModule,
    NbButtonModule,
    NbIconModule,
    NbListModule,
    ReactiveFormsModule,
    NbAuthModule,
    NbActionsModule,
    CommonModule
  ],
  declarations: [
    DashboardComponent,
    TodoComponent,
    TodoListComponent
  ],
  exports: [

  ],
  providers: [
  ],
})
export class DashboardModule {
}
