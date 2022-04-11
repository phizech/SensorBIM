import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
// @ts-ignore
import {environment} from '../../../../environments/environment';
import {User} from '../../../entities/user';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {NotificationService} from '../../response-notification/notification.service';
import {ResponseStatus} from '../../response-notification/response-status';
import {ResponseMessage} from '../../response-notification/response';
import {UserManagementService} from '../user-management.service';
import {UserRole} from '../../../entities/user-role';

@Component({
  selector: 'app-add-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['../../CssTemplates/configuration.css']
})
export class EditUserComponent implements OnInit {

  user: User;
  form: FormGroup;
  ownProfile = false;
  changePasswordForm: FormGroup;
  allRoles;

  constructor(private http: HttpClient,
              private userService: UserManagementService,
              private route: ActivatedRoute,
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

  get userRole() {
    return this.form.value.userRole;
  }

  set userRole(role) {
    this.form.value.userRole = role;
  }

  get password() {
    return this.changePasswordForm.value.password;
  }

  get newPassword() {
    return this.changePasswordForm.value.newPassword;
  }

  get repeatedPassword() {
    return this.changePasswordForm.value.repeatedPassword;
  }

  public getAllUserRoles() {
    this.allRoles = Array.from(Object.entries(UserRole));
  }

  ngOnInit() {
    this.getAllUserRoles();
    this.route.paramMap
      .subscribe(params => {
          let userId = +params.get('userId');
          if (userId === 0) {
            this.ownProfile = true;
            userId = JSON.parse(localStorage.getItem('user')).id;
          }
          this.userService.getUserById(userId).subscribe(res => {
            this.user = res;
            this.form = this.getForm();
            this.changePasswordForm = this.getChangePasswordForm();
          }, err => console.log(err));
        }
      );
  }

  public getChangePasswordForm() {
    return new FormGroup({
      password: new FormControl('', [
        Validators.minLength(5),
        Validators.required,
      ]),
      newPassword: new FormControl('', [
        Validators.minLength(5),
        Validators.required
      ]),
      repeatedPassword: new FormControl('', [
        Validators.required,
        Validators.minLength(5)
      ])
    });
  }

  public getForm() {
    return new FormGroup({
      username: new FormControl(this.user.username, [
        Validators.required,
        Validators.minLength(5)
      ]),
      email: new FormControl(this.user.email, [
        Validators.required,
        Validators.email
      ]),
      firstName: new FormControl(this.user.firstName, [
        Validators.required
      ]),
      lastName: new FormControl(this.user.lastName, [
        Validators.required
      ]),
      userRole: new FormControl(this.user.userRole, [
        Validators.required
      ])
    });
  }

  save() {
    const body: User = {
      id: this.user.id,
      firstName: this.firstName,
      lastName: this.lastName,
      username: this.username,
      password: this.user.password,
      email: this.email,
      userRole: this.userRole
    };
    this.userService.updateUser(body).subscribe(
      (response: ResponseMessage) => {
        if (response.responseStatus === ResponseStatus.SUCCESS.valueOf()) {
          this.notification.notify(ResponseStatus.SUCCESS, response.message);
          this.router.navigate(['/userManagement']);
        } else {
          this.notification.notify(response.responseStatus, response.message);
        }
      },
      () => {
        this.notification.notify(ResponseStatus.FAILURE, 'user.savingFailed');
      }
    );
  }

  public updatePassword() {
    const container = document.getElementById('edit-container');
    const button = document.createElement('button');
    button.type = 'button'; // change type from submit to button
    button.style.display = 'none'; // hide button
    button.setAttribute('data-toggle', 'modal');
    container.appendChild(button);
    if (this.ownProfile) {
      button.setAttribute('data-target', '#resetPasswordEntering');
      button.click();
      // open modal to edit profile
    } else {
      button.setAttribute('data-target', '#resetPasswordByMail');
      button.click();
      // open modal to confirm change of password and send email
    }
  }

  public resetPassword() {
    this.userService.updatePassword(
      {
        'userId': this.user.id, 'oldPassword': this.password,
        'newPassword': this.newPassword, 'repeatedPassword': this.repeatedPassword
      })
      .subscribe(
        (response: ResponseMessage) => {
          if (response.responseStatus === ResponseStatus.SUCCESS.valueOf()) {
            this.notification.notify(ResponseStatus.SUCCESS, response.message);
            const modal = document.getElementById('resetPasswordEntering');
            modal.click();

          } else {
            this.notification.notify(response.responseStatus, response.message);
          }
        },
        () => {
          this.notification.notify(ResponseStatus.FAILURE, 'user.password.savingFailed');
        });
  }

  public sendEmailWithNewPassword() {
    this.userService.resetPassword(this.email).subscribe(
      (response: ResponseMessage) => {
        if (response.responseStatus === ResponseStatus.SUCCESS.valueOf()) {
          this.notification.notify(ResponseStatus.SUCCESS, response.message);
        } else {
          this.notification.notify(response.responseStatus, response.message);
        }
      },
      () => {
        this.notification.notify(ResponseStatus.FAILURE, 'user.password.savingFailed');
      });

  }

}
