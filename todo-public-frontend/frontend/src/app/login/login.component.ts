import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { AuthService } from "./../auth.service";
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm = new FormGroup({
    username: new FormControl(),
    password: new FormControl(),
  })

  constructor(private authService: AuthService, private router: Router) {
  }

  ngOnInit(): void {
  }

  onLogin() {
    this.authService.login(this.loginForm.value.username, this.loginForm.value.password)
      .subscribe({
          next: (value) => {
            this.router.navigate(['/todos'])
          },
          error: (err) => console.error(err)
      });
  }

}
