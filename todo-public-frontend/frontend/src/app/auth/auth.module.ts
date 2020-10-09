import { Injector, ModuleWithProviders, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpRequest } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from "./auth.service";
import { AuthGuard } from "./auth.guard";
import { AuthInterceptor } from "./auth.interceptor";
import { HTTP_INTERCEPTORS } from '@angular/common/http';

import {
  NbAlertModule,
  NbButtonModule,
  NbCardModule,
  NbCheckboxModule,
  NbIconModule,
  NbInputModule,
  NbLayoutModule,
} from '@nebular/theme';

import { NbAuthComponent } from './auth.component';
import { NbAuthBlockComponent } from './auth-block/auth-block.component';
import { LoginComponent } from './login/login.component';

@NgModule({
  imports: [
    CommonModule,
    NbLayoutModule,
    NbCardModule,
    NbCheckboxModule,
    NbAlertModule,
    NbInputModule,
    NbButtonModule,
    RouterModule,
    NbIconModule,
    ReactiveFormsModule
  ],
  declarations: [
    NbAuthComponent,
    NbAuthBlockComponent,
    LoginComponent,
  ],
  exports: [
    NbAuthComponent,
    NbAuthBlockComponent,
  ],
  providers: [
    AuthService,
    AuthGuard,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}
  ],
})
export class NbAuthModule {
}
