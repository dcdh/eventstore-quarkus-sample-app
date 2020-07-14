import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TodoComponent } from './todo.component';
import { TodoService } from 'src/generated';
import { TodoDTO } from 'src/generated';
import { TodoStatus } from 'src/generated';

import { HttpClientTestingModule } from '@angular/common/http/testing';

import { DebugElement } from "@angular/core";
import { By } from "@angular/platform-browser";

import { defer } from 'rxjs';

describe('TodoComponent', () => {
  let component: TodoComponent;
  let fixture: ComponentFixture<TodoComponent>;
  let buttonEl: DebugElement;

  const todoServiceSpy = jasmine.createSpyObj('TodoService', ['todosMarkTodoAsCompletedPost']);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TodoComponent ],
      imports: [ HttpClientTestingModule ],
      providers: [ { provide: TodoService, useValue: todoServiceSpy } ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TodoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    buttonEl = fixture.debugElement.query(By.css('button'));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should mark todo as completed call remote service and assign response to todo variable', () => {
    // Given
    todoServiceSpy.todosMarkTodoAsCompletedPost.and.callFake(function() {
      return defer(() => Promise.resolve({ todoId: 'todoId', description: 'lorem', todoStatus: 'COMPLETED', canMarkTodoAsCompleted: false, version: 1 }));
    });

    // When
    component.markTodoAsCompleted({ todoId: 'todoId', description: 'lorem', todoStatus: 'IN_PROGRESS', canMarkTodoAsCompleted: true, version: 0 });

    // Then
    fixture.whenStable().then(() => {
      expect(component.todo).toEqual({ todoId: 'todoId', description: 'lorem', todoStatus: 'COMPLETED', canMarkTodoAsCompleted: false, version: 1 });
      expect(todoServiceSpy.todosMarkTodoAsCompletedPost).toHaveBeenCalled();
    });
  });

  it('should button mark as completed be disabled if todo cannot be marked as completed', () => {
    // Given
    component.todo = { canMarkTodoAsCompleted: true };

    // When
    fixture.detectChanges();

    // Then
    expect(buttonEl.nativeElement.disabled).toBeFalsy();
  });

  it('should button mark as completed be enabled if todo can be marked as completed', () => {
    // Given
    component.todo = { canMarkTodoAsCompleted: false };

    // When
    fixture.detectChanges();

    // Then
    expect(buttonEl.nativeElement.disabled).toBeTruthy();
  });

  it('should click on button mark as completed call the mark as completed remote service', () => {
    // Given
    component.todo = { canMarkTodoAsCompleted: true };
    todoServiceSpy.todosMarkTodoAsCompletedPost.and.callFake(function() {
      return defer(() => Promise.resolve({ todoId: 'todoId', description: 'lorem', todoStatus: 'COMPLETED', canMarkTodoAsCompleted: false, version: 1 }));
    });

    // When
    buttonEl.triggerEventHandler('click', null);

    // Then
    expect(todoServiceSpy.todosMarkTodoAsCompletedPost).toHaveBeenCalled();
  });

});
