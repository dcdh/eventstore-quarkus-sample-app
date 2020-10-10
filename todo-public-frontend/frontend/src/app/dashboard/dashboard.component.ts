/*
 * Copyright (c) Akveo 2019. All Rights Reserved.
 * Licensed under the Single Application / Multi Application License.
 * See LICENSE_SINGLE_APP / LICENSE_MULTI_APP in the 'docs' folder for license information on type of purchased license.
 */
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'dashboard',
  styleUrls: ['./dashboard.component.scss'],
  template: `
    <nb-layout windowMode>
      <nb-layout-header fixed>
        <app-connected-user></app-connected-user>
      </nb-layout-header>
      <nb-layout-column>
        <router-outlet></router-outlet>
      </nb-layout-column>
    </nb-layout>
  `,
})
export class DashboardComponent implements OnInit {

  ngOnInit(): void {
  }

}
