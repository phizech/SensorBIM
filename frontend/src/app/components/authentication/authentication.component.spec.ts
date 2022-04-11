import {async, ComponentFixture, fakeAsync, inject, TestBed, tick} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {AuthenticationService} from './authentication.service';
import {environment} from '../../../environments/environment';
import {ResponseMessage} from '../response-notification/response';
import {AuthenticationComponent} from './authentication.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatTableModule} from '@angular/material/table';
import {RouterTestingModule} from '@angular/router/testing';
import {MatPaginatorModule} from '@angular/material/paginator';
import {ResponseStatus} from '../response-notification/response-status';
import {By} from '@angular/platform-browser';
import {DebugElement} from '@angular/core';

describe('AuthenticationComponent', () => {

  let component: AuthenticationComponent;
  let fixture: ComponentFixture<AuthenticationComponent>;
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
      providers: [AuthenticationService],
      declarations: [AuthenticationComponent]
    }).compileComponents().then(() => {
      fixture = TestBed.createComponent(AuthenticationComponent);
      component = fixture.componentInstance;
    });
  }));

  it(
    'should be initialized', () => {
      expect(component).toBeTruthy();
    });

  it('Notiflix is exist in the page.', async () => {
    de = fixture.debugElement.query(By.css('#Login'));
    expect(de).toBeTruthy();
  });

  it(
    'should perform login correctly',
    fakeAsync(
      inject(
        [AuthenticationService, HttpTestingController],
        (authService: AuthenticationService, backend: HttpTestingController) => {
          const user = {
            username: 'steverogers',
            password: 'password'
          };
          const url = `${environment.apiBaseUrl}/auth/authenticate`;
          const responseObject = {
            responseStatus: ResponseStatus.SUCCESS,
            message: 'Login erfolgreich',
            body: user
          };

          let response = null;
          authService.login(user).subscribe(
            (receivedResponse: ResponseMessage) => {
              response = receivedResponse;
            },
            () => {
            }
          );
          const requestWrapper = backend.expectOne({url: `${environment.apiBaseUrl}/auth/authenticate`});
          requestWrapper.flush(responseObject);
          tick();
          expect(requestWrapper.request.method).toEqual('POST');
          expect(response.responseStatus).toEqual(ResponseStatus.SUCCESS);
          expect(response.message).toEqual('Login erfolgreich');
          expect(response.body).toBe(user);
        }
      )
    )
  );
  it(
    'should fail login correctly',
    fakeAsync(
      inject(
        [AuthenticationService, HttpTestingController],
        (authService: AuthenticationService, backend: HttpTestingController) => {
          const url = `${environment.apiBaseUrl}/auth/authenticate`;
          const responseObject = {
            responseStatus: ResponseStatus.FAILURE,
            message: 'email and password combination is wrong',
            body: null
          };
          const user = {
            username: 'steverogers',
            password: 'wrongPassword'
          };
          let response: ResponseMessage = null;
          authService.login(user).subscribe(
            (receivedResponse: any) => {
              response = receivedResponse;
            },
            (error: any) => {
            }
          );
          const requestWrapper = backend.expectOne({url: `${environment.apiBaseUrl}/auth/authenticate`});
          requestWrapper.flush(responseObject);
          tick();
          expect(requestWrapper.request.method).toEqual('POST');
          expect(response.responseStatus).toEqual(ResponseStatus.FAILURE);
          expect(response.message).toBe('email and password combination is wrong');
          expect(response.body).toBe(null);
        }
      )
    )
  );
  it('should return false as no user is logged in',
    fakeAsync(
      inject(
        [AuthenticationService, HttpTestingController],
        (authService: AuthenticationService, backend: HttpTestingController) => {
          const isLoggedIn = authService.isLoggedIn();
          expect(isLoggedIn).toEqual(false);
        }
      )
    )
  );
  it('should return current user = null as no user is logged in',
    fakeAsync(
      inject(
        [AuthenticationService, HttpTestingController],
        (authService: AuthenticationService, backend: HttpTestingController) => {
          const currentUser = authService.getCurrentUser();
          expect(currentUser).toEqual(null);
        }
      )
    )
  );
  it('should return token = null as no user is logged in',
    fakeAsync(
      inject(
        [AuthenticationService, HttpTestingController],
        (authService: AuthenticationService, backend: HttpTestingController) => {
          const token = authService.getToken();
          expect(token).toEqual(null);
        }
      )
    )
  );
  it('should return current user = null as no user is logged in',
    fakeAsync(
      inject(
        [AuthenticationService, HttpTestingController],
        (authService: AuthenticationService, backend: HttpTestingController) => {
          const user = {
            username: 'steverogers',
            password: 'wrongPassword'
          };
          let response: ResponseMessage = null;
          authService.login(user).subscribe(
            (receivedResponse: any) => {
              response = receivedResponse;
            },
            (error: any) => {
            }
          );
          tick();
          expect(response).toEqual(null);
        }
      )
    )
  );
});
