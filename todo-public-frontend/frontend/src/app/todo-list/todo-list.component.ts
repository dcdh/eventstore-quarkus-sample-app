import { Component, OnInit } from '@angular/core';

import { TodoService } from 'src/generated';
import { ListTodoDTO } from 'src/generated';
import { TodoDTO } from 'src/generated';

@Component({
  selector: 'todo-list',
  templateUrl: './todo-list.component.html',
  styleUrls: ['./todo-list.component.css']
})
export class TodoListComponent implements OnInit {

  listTodoDTO: ListTodoDTO;

  constructor(
    private todoService: TodoService
  ) {
  }

  ngOnInit(): void {
    this.todoService.todosGet()
      .subscribe((listTodoDTO: ListTodoDTO) => this.listTodoDTO = listTodoDTO);
  }

  createNewTodo(description: string): void {
    this.todoService.todosCreateNewTodoPost(description)
      .subscribe((todo: TodoDTO) => this.listTodoDTO.push(todo));
  }

}
