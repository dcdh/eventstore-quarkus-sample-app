import { Component, OnInit, Input } from '@angular/core';

import { TodoService } from 'src/generated';
import { TodoDTO } from 'src/generated';

@Component({
  selector: 'todo',
  templateUrl: './todo.component.html',
  styleUrls: ['./todo.component.css']
})
export class TodoComponent implements OnInit {

  @Input()
  todo: TodoDTO = {};

  constructor(
    private todoService: TodoService
  ) { }

  ngOnInit(): void {
  }

  markTodoAsCompleted(todo: TodoDTO): void {
    this.todoService.todosMarkTodoAsCompletedPost(todo.todoId)
      .subscribe((todo: TodoDTO) => this.todo = todo);
  }

}
