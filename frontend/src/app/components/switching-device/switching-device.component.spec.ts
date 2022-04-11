import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SwitchingDeviceComponent} from './switching-device.component';

describe('SwitchingDeviceComponent', () => {
  let component: SwitchingDeviceComponent;
  let fixture: ComponentFixture<SwitchingDeviceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SwitchingDeviceComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SwitchingDeviceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
