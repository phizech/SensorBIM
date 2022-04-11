import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {AuthenticationService} from './authentication.service';
import {User} from '../../entities/user';
import {NotificationService} from '../response-notification/notification.service';
import {ResponseStatus} from '../response-notification/response-status';

@Component({
  selector: 'app-login',
  templateUrl: './authentication.component.html',
  styleUrls: ['../CssTemplates/configuration.css']
})
export class AuthenticationComponent implements OnInit {
  form = new FormGroup({
    username: new FormControl('', [
      Validators.required,
      Validators.minLength(3)
    ]),
    password: new FormControl('', [
      Validators.minLength(3),
      Validators.required
    ])
  });

  constructor(private http: HttpClient,
              private loginService: AuthenticationService,
              private router: Router,
              private notify: NotificationService) {
  }

  get username() {
    return this.form.value.username;
  }

  get password() {
    return this.form.value.password;
  }

  login(username, password) {
    const body = {
      username: username,
      password: password
    };
    this.loginService.login(body).subscribe(
      (response: JSON) => {
        if (response) {
          const currentUser: User = response['user'];
          this.loginService.setToken(response['token']);
          this.loginService.setUser(JSON.stringify(currentUser));
          this.notify.notify(ResponseStatus.SUCCESS, 'login.success');
          this.router.navigate(['/home']);
          return true;
        }
        this.notify.notify(ResponseStatus.FAILURE, 'login.wrongPW');
        return false;
      },
      () => {
        this.notify.notify(ResponseStatus.FAILURE, 'login.serverConFailed');
      }
    );
  }

  ngOnInit() {
  }

}
