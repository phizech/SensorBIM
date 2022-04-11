import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {User} from '../../../entities/user';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {NotificationService} from '../../response-notification/notification.service';
import {ResponseStatus} from '../../response-notification/response-status';
import {ResponseMessage} from '../../response-notification/response';
import {UserManagementService} from '../user-management.service';
import {NewUser} from '../../../entities/new-user';

@Component({
  selector: 'app-add-user',
  templateUrl: './register-user.component.html',
  styleUrls: ['../../CssTemplates/configuration.css']
})
export class RegisterUserComponent implements OnInit {

  selectedUser: User;

  form = new FormGroup({
    username: new FormControl('', [
      Validators.required,
      Validators.minLength(3)
    ]),
    email: new FormControl('', [
      Validators.minLength(3),
      Validators.required
    ]),
    firstName: new FormControl('', [
      Validators.required,
      Validators.minLength(3)
    ]),
    lastName: new FormControl('', [
      Validators.required,
      Validators.minLength(3)
    ]),
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(3)
    ]),
    repeatedPassword: new FormControl('', [
      Validators.required,
      Validators.minLength(3)
    ]),
  });

  constructor(private http: HttpClient,
              private userService: UserManagementService,
              private router: Router,
              private notification: NotificationService) {
  }

  get username() {
    return this.form.value.username;
  }

  get email() {
    return this.form.value.email;
  }

  get firstName() {
    return this.form.value.firstName;
  }

  get lastName() {
    return this.form.value.lastName;
  }

  get password() {
    return this.form.value.password;
  }

  get repeatedPassword() {
    return this.form.value.repeatedPassword;
  }

  save(username, lastName, firstName, email, password, repeatedPassword) {
    const body: NewUser = {
      firstName: firstName,
      lastName: lastName,
      username: username,
      password: password,
      repeatedPassword: repeatedPassword,
      email: email
    };
    this.userService.createAccount(body).subscribe(
      (response: ResponseMessage) => {
        if (response.responseStatus === ResponseStatus.SUCCESS.valueOf()) {
          this.notification.notify(ResponseStatus.SUCCESS, response.message);
          this.router.navigate(['/login']);
        } else {
          this.notification.notify(response.responseStatus, response.message);
        }
      },
      () => {
        this.notification.notify(ResponseStatus.FAILURE, 'user.savingFailed');
      }
    );
  }

  ngOnInit() {
  }

}
