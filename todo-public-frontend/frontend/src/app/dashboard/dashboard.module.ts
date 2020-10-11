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
  NbContextMenuModule,
} from '@nebular/theme';

import { DashboardComponent } from './dashboard.component';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { TodoComponent } from './todo/todo.component';
import { TodoListComponent } from './todo-list/todo-list.component';
import { NbAuthModule } from './../auth/auth.module';
import { WhatIsItComponent } from './what-is-it/what-is-it.component';

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
    NbContextMenuModule,
    CommonModule
  ],
  declarations: [
    DashboardComponent,
    TodoComponent,
    TodoListComponent,
    WhatIsItComponent
  ],
  exports: [

  ],
  providers: [
  ],
})
export class DashboardModule {
}
