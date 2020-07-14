import { Component, OnInit } from '@angular/core';
import { UserService, UserDTO } from 'src/generated';

@Component({
  selector: 'app-connected-user',
  templateUrl: './connected-user.component.html',
  styleUrls: ['./connected-user.component.css']
})
export class ConnectedUserComponent implements OnInit {

  user: UserDTO;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.usersMeGet()
      .subscribe({
        next: user => {
          console.info(user);
          this.user = user;
        },
        error: error => {
          console.warn(error);
        }
      });
  }

}
