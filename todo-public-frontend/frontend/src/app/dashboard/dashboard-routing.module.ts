import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { DashboardComponent } from './dashboard.component';
import { AuthGuard } from "./../auth/auth.guard";
import { TodoListComponent } from './todo-list/todo-list.component';

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
    ],
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DashboardRoutingModule {
}
