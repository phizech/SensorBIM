import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {InfoComponent} from './info.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatTableModule} from '@angular/material/table';
import {RouterTestingModule} from '@angular/router/testing';
import {MatPaginatorModule} from '@angular/material/paginator';

describe('InfoComponent', () => {

  let component: InfoComponent;
  let fixture: ComponentFixture<InfoComponent>;

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
      providers: [],
      declarations: [InfoComponent]
    }).compileComponents().then(() => {
      fixture = TestBed.createComponent(InfoComponent);
      component = fixture.componentInstance;
    });
  }));

  it(
    'should be initialized', () => {
      expect(component).toBeTruthy();
    });
});
