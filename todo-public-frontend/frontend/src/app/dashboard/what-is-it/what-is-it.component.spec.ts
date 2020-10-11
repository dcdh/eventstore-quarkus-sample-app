import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WhatIsItComponent } from './what-is-it.component';

describe('WhatIsItComponent', () => {
  let component: WhatIsItComponent;
  let fixture: ComponentFixture<WhatIsItComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WhatIsItComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WhatIsItComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
