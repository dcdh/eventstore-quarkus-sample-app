import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { DashboardComponent } from './dashboard.component';
import { AuthGuard } from "./../auth/auth.guard";
import { TodoListComponent } from './todo-list/todo-list.component';
import { WhatIsItComponent } from './what-is-it/what-is-it.component';

export const routes: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'todos',
        component: TodoListComponent,
        canActivate: [AuthGuard]
      },
      {
        path: '',
        redirectTo: 'todos',
        pathMatch: 'full'
      },
      {
        path: 'what-is-it',
        component: WhatIsItComponent
      }
    ],
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DashboardRoutingModule {
}
