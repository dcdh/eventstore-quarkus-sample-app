import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TodoListComponent } from './todo-list.component';
import { TodoService } from 'src/generated';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { defer } from 'rxjs';

describe('TodoListComponent', () => {
  let component: TodoListComponent;
  let fixture: ComponentFixture<TodoListComponent>;

  const todoServiceSpy = jasmine.createSpyObj('TodoService', ['todosGet', 'todosCreateNewTodoPost']);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TodoListComponent ],
      imports: [ HttpClientTestingModule ],
      providers: [ { provide: TodoService, useValue: todoServiceSpy } ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    todoServiceSpy.todosGet.and.callFake(function() {
      return defer(() => Promise.resolve([{ todoId: 'todoId', description: 'lorem', todoStatus: 'IN_PROGRESS', canMarkTodoAsCompleted: false, version: 0 }]));
    });
    fixture = TestBed.createComponent(TodoListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and get all todos at component initialization', (async () => {
    await fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(component.listTodoDTO).toEqual([{ todoId: 'todoId', description: 'lorem', todoStatus: 'IN_PROGRESS', canMarkTodoAsCompleted: false, version: 0 }]);
    expect(todoServiceSpy.todosGet).toHaveBeenCalled();
  }));

  it('should create new todo call remote service', (async () => {
    // Given
    todoServiceSpy.todosCreateNewTodoPost.and.callFake(function() {
      return defer(() => Promise.resolve({ todoId: 'todoId2', description: 'ipsum', todoStatus: 'IN_PROGRESS', canMarkTodoAsCompleted: false, version: 0 }));
    });

    // When
    await component.createNewTodo('ipsum');

    // Then
    expect(component.listTodoDTO).toEqual([{ todoId: 'todoId', description: 'lorem', todoStatus: 'IN_PROGRESS', canMarkTodoAsCompleted: false, version: 0 },
      { todoId: 'todoId2', description: 'ipsum', todoStatus: 'IN_PROGRESS', canMarkTodoAsCompleted: false, version: 0 }]);
    expect(todoServiceSpy.todosCreateNewTodoPost).toHaveBeenCalled();
  }));

});
