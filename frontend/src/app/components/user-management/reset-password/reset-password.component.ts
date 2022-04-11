import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ResponseMessage} from '../../response-notification/response';
import {ResponseStatus} from '../../response-notification/response-status';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {UserManagementService} from '../user-management.service';
import {Router} from '@angular/router';
import {NotificationService} from '../../response-notification/notification.service';

@Component({
  selector: 'app-add-user',
  templateUrl: './reset-password.component.html',
  styleUrls: ['../../CssTemplates/configuration.css']
})
export class ResetPasswordComponent implements OnInit {

  form = new FormGroup({
    email: new FormControl('', [
      Validators.required,
      Validators.minLength(3)
    ])
  });

  constructor(private http: HttpClient,
              private userService: UserManagementService,
              private router: Router,
              private notification: NotificationService) {
  }

  get email() {
    return this.form.value.email;
  }

  ngOnInit(): void {
  }


  resetEmail(email: any) {
    this.userService.resetPassword(email).subscribe(
      (response: ResponseMessage) => {
        if (response.responseStatus === ResponseStatus.SUCCESS.valueOf()) {
          this.notification.notify(ResponseStatus.SUCCESS, response.message);
          this.router.navigate(['/login']);
        } else {
          this.notification.notify(response.responseStatus, response.message);
        }
      },
      (error: HttpErrorResponse) => {
        console.log(error);
        this.notification.notify(ResponseStatus.FAILURE, 'basicErrorMessage');
      }
    );
  }
}
