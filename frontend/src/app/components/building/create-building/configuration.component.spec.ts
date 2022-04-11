import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ConfigurationService} from './configuration.service';
import {ConfigurationComponent} from './configuration.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatTableModule} from '@angular/material/table';
import {RouterTestingModule} from '@angular/router/testing';
import {MatPaginatorModule} from '@angular/material/paginator';

describe('ConfigurationComponent', () => {

  let component: ConfigurationComponent;
  let fixture: ComponentFixture<ConfigurationComponent>;

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
      providers: [ConfigurationService],
      declarations: [ConfigurationComponent]
    }).compileComponents().then(() => {
      fixture = TestBed.createComponent(ConfigurationComponent);
      component = fixture.componentInstance;
    });
  }));

  it(
    'should be initialized', () => {
      expect(component).toBeTruthy();
    });
});
