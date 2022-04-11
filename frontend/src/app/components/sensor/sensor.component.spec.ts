import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SensorService} from './sensor.service';
import {SensorComponent} from './sensor.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatTableModule} from '@angular/material/table';
import {RouterTestingModule} from '@angular/router/testing';
import {MatPaginatorModule} from '@angular/material/paginator';
import {DebugElement} from '@angular/core';

describe('SensorComponent', () => {

  let component: SensorComponent;
  let fixture: ComponentFixture<SensorComponent>;
  let de: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule,
        MatIconModule,
        BrowserAnimationsModule,
        MatTableModule,
        HttpClientTestingModule,
        RouterTestingModule,
        MatPaginatorModule,
        ReactiveFormsModule],
      providers: [SensorService],
      declarations: [SensorComponent]
    }).compileComponents().then(() => {
      fixture = TestBed.createComponent(SensorComponent);
      component = fixture.componentInstance;
    });
  }));

  it(
    'should be initialized', () => {
      expect(component).toBeTruthy();
    });

});
