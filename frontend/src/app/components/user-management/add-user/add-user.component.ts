import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
// @ts-ignore
import {environment} from '../../../../environments/environment';
import {User} from '../../../entities/user';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {UserRole} from '../../../entities/user-role';
import {Router} from '@angular/router';
import {NotificationService} from '../../response-notification/notification.service';
import {ResponseStatus} from '../../response-notification/response-status';
import {ResponseMessage} from '../../response-notification/response';
import {UserManagementService} from '../user-management.service';

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['../../CssTemplates/configuration.css']
})
export class AddUserComponent implements OnInit {

  selectedUser: User;
  allRoles;

  form = new FormGroup({
    username: new FormControl('', [
      Validators.required,
      Validators.minLength(5)
    ]),
    email: new FormControl('', [
      Validators.required
    ]),
    firstName: new FormControl('', [
      Validators.required
    ]),
    lastName: new FormControl('', [
      Validators.required
    ]),
    userRole: new FormControl('', [
      Validators.required
    ])
  });

  constructor(private http: HttpClient,
              private userService: UserManagementService,
              private router: Router,
              private notification: NotificationService) {
  }

  get userRole() {
    return this.form.value.userRole;
  }

  set userRole(role) {
    this.form.value.userRole = role;
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

  ngOnInit() {
    this.getAllUserRoles();
  }

  public getAllUserRoles() {
    this.allRoles = Array.from(Object.entries(UserRole));
  }

  save(username, lastName, firstName, email) {
    const body: User = {
      id: null,
      firstName: firstName,
      lastName: lastName,
      username: username,
      password: '',
      email: email,
      userRole: this.userRole[0]
    };
    this.userService.addUser(body).subscribe(
      (response: ResponseMessage) => {
        if (response.responseStatus === ResponseStatus.SUCCESS.valueOf()) {
          this.notification.notify(ResponseStatus.SUCCESS, response.message);
          this.router.navigate(['/userManagement']);
        } else {
          this.notification.notify(response.responseStatus, response.message);
        }
      },
      (error: HttpErrorResponse) => {
        this.notification.notify(ResponseStatus.FAILURE, 'user.savingSuccess');
      }
    );
  }

}
