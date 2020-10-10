import { Component, OnInit, Input } from '@angular/core';

import { TodoService } from 'src/generated';
import { TodoDTO } from 'src/generated';
import { NotificationService } from './../../notification/notification.service';

@Component({
  selector: 'todo',
  templateUrl: './todo.component.html',
  styleUrls: ['./todo.component.css']
})
export class TodoComponent implements OnInit {

  @Input()
  todo: TodoDTO = {};

  constructor(
    private todoService: TodoService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
  }

  markTodoAsCompleted(todo: TodoDTO): void {
    this.todoService.todosMarkTodoAsCompletedPost(todo.todoId)
      .subscribe((todo: TodoDTO) => {
        this.notificationService.success('Todo marked as completed !');
        this.todo = todo;
      });
  }

}
