/*
 * Copyright (c) Akveo 2019. All Rights Reserved.
 * Licensed under the Single Application / Multi Application License.
 * See LICENSE_SINGLE_APP / LICENSE_MULTI_APP in the 'docs' folder for license information on type of purchased license.
 */
import { Component, Inject, OnInit } from '@angular/core';
import { NbMenuItem } from '@nebular/theme';

@Component({
  selector: 'dashboard',
  styleUrls: ['./dashboard.component.scss'],
  template: `
    <nb-layout windowMode>
      <nb-layout-header fixed>
        <nb-actions size="small">
          <nb-action>
            <app-connected-user></app-connected-user>
          </nb-action>
          <nb-action>
            <app-logout></app-logout>
          </nb-action>
          <nb-action>
            <nb-icon icon="settings-outline" [nbContextMenu]="infrastructures"></nb-icon>
          </nb-action>
        </nb-actions>
      </nb-layout-header>
      <nb-layout-column>
        <router-outlet></router-outlet>
      </nb-layout-column>
    </nb-layout>
  `,
})
export class DashboardComponent implements OnInit {

  // https://akveo.github.io/nebular/docs/components/menu/overview#nbmenuitem
  infrastructures: NbMenuItem[] = [
    {
      title: 'Mailhog',
      url: 'http://localhost:8025/',
      target: '_blank'
    },
    {
      title: 'Keycloak',
      url: 'http://localhost:8087',
      target: '_blank'
    },
    {
      title: 'Jaeger UI',
      url: 'http://localhost:16686',
      target: '_blank'
    },
    {
      title: 'Kibana',
      url: 'http://localhost:5601/',
      target: '_blank'
    },
    {
      title: 'Todo public frontend',
      url: 'http://localhost:8086/swagger-ui/#/default',
      target: '_blank'
    },
    {
      title: 'Todo write app',
      url: 'http://localhost:8084/swagger-ui/#/default',
      target: '_blank'
    },
    {
      title: 'Todo query app',
      url: 'http://localhost:8085/swagger-ui/#/default',
      target: '_blank'
    },
  ];

  ngOnInit(): void {
  }

}
