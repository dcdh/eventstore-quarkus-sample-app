export * from './registration.service';
import { RegistrationService } from './registration.service';
export * from './todo.service';
import { TodoService } from './todo.service';
export * from './user.service';
import { UserService } from './user.service';
export const APIS = [RegistrationService, TodoService, UserService];
