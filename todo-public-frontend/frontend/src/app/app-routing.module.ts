import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TodoListComponent } from './todo-list/todo-list.component';
import { AuthGuard } from "./auth/auth.guard";

export const routes: Routes = [
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
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
