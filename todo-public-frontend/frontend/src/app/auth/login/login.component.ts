import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from "./../../auth.service";
import { Router } from '@angular/router';
import { HttpErrorResponse } from "@angular/common/http";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  errors: string[] = [];
  submitted: boolean = false;

  constructor(private authService: AuthService,
              private router: Router,
              private formBuilder: FormBuilder,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  get username() {
    return this.loginForm.get('username');
  }

  get password() {
    return this.loginForm.get('password');
  }

  ngOnInit(): void {
    const usernameValidators = [
      Validators.required
    ];
    const passwordValidators = [
      Validators.required
    ];
    this.loginForm = this.formBuilder.group({
      username: this.formBuilder.control('', [...usernameValidators]),
      password: this.formBuilder.control('', [...passwordValidators]),
    });
  }

  login() {
    this.submitted = true;
    this.errors = [];
    this.authService.login(this.loginForm.value.username, this.loginForm.value.password)
      .subscribe({
          next: (value) => {
            this.submitted = false;
            this.router.navigate(['/todos'])
          },
          error: (httpErrorResponse: HttpErrorResponse) => {
            this.submitted = false;
            this.errors = [httpErrorResponse.error];
            this.changeDetectorRef.detectChanges();
          }
      });
  }

}
