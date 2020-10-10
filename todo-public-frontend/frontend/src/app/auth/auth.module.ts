import { Injector, ModuleWithProviders, NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from "./auth.service";
import { AuthGuard } from "./auth.guard";
import { AuthInterceptor } from "./auth.interceptor";
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { ConnectedUserComponent } from './connected-user/connected-user.component';
import { CommonModule } from '@angular/common';

import {
  NbAlertModule,
  NbButtonModule,
  NbCardModule,
  NbCheckboxModule,
  NbIconModule,
  NbInputModule,
  NbLayoutModule,
  NbUserModule,
} from '@nebular/theme';

import { NbAuthComponent } from './auth.component';
import { NbAuthBlockComponent } from './auth-block/auth-block.component';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { AuthRoutingModule } from './auth-routing.module';

@NgModule({
  imports: [
    AuthRoutingModule,
    NbLayoutModule,
    NbCardModule,
    NbCheckboxModule,
    NbAlertModule,
    NbInputModule,
    NbButtonModule,
    NbIconModule,
    ReactiveFormsModule,
    NbUserModule,
    CommonModule
  ],
  declarations: [
    NbAuthComponent,
    NbAuthBlockComponent,
    LoginComponent,
    ConnectedUserComponent,
    LogoutComponent
  ],
  exports: [
    ConnectedUserComponent,
    LogoutComponent
  ],
  providers: [
    AuthService,
    AuthGuard,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}
  ],
})
export class NbAuthModule {
}
