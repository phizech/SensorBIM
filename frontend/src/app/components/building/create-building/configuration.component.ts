import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {ConfigurationService} from './configuration.service';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {ResponseMessage} from '../../response-notification/response';
import {ResponseStatus} from '../../response-notification/response-status';
import {NotificationService} from '../../response-notification/notification.service';

@Component({
  selector: 'app-configuration',
  templateUrl: './configuration.component.html',
  styleUrls: ['../../CssTemplates/configuration.css']
})
export class ConfigurationComponent implements OnInit {
  public uploaded: boolean;
  public filename: string;
  form = new FormGroup({
    buildingName: new FormControl('', [
      Validators.required,
      Validators.minLength(5)
    ]),
    databaseUrl: new FormControl('', [
      Validators.required
    ]),
    token: new FormControl('', Validators.required),
    organizationName: new FormControl('', Validators.required)
  });
  private uploading: boolean;

  constructor(
    private http: HttpClient,
    private _s: ConfigurationService,
    private router: Router,
    private notification: NotificationService
  ) {
  }

  get databaseUrl() {
    return this.form.value.databaseUrl;
  }

  get token() {
    return this.form.value.token;
  }

  get organizationName() {
    return this.form.value.organizationName;
  }

  get buildingName() {
    return this.form.value.buildingName;
  }

  ngOnInit(): void {
    this.uploading = false;
    this.uploaded = false;
  }

  public openFile(event): void {
    const re = /(?:\.([^.]+))?$/;
    const type = re.exec(event.target.files[0].name)[1];
    if ((type === 'ifc') || (type === 'IFC')) {
      this._s.uploadFile(event).subscribe(
        (response: ResponseMessage) => {
          if (response.responseStatus === ResponseStatus.SUCCESS.valueOf()) {
            this.notification.notify(ResponseStatus.SUCCESS, response.message);
            this.uploaded = true;
            this.filename = this._s.filename;
          } else {
            this.notification.notify(response.responseStatus, response.message);
            this.uploading = false;
          }
        },
        (error: HttpErrorResponse) => {
          alert(error.message);
        }
      );
    } else {
      this.notification.notify(ResponseStatus.FAILURE, 'upload.file.noIFC');
    }
  }

  public saveAndConvert(): void {
    this.uploading = true;
    this._s.saveAndConvert(this.buildingName, this.databaseUrl, this.token, this.organizationName).subscribe(
      (response: ResponseMessage) => {
        if (!(response.responseStatus === ResponseStatus.FAILURE.valueOf()) && !(response.responseStatus === ResponseStatus.WARNING.valueOf())) {
          this.router.navigate(['/myBuildings']);
        }
        this.notification.notify(response.responseStatus, response.message);
        this.uploading = false;
      },
      () => {
        this.notification.notify(ResponseStatus.FAILURE, 'basicErrorMessage');
        this.uploading = false;
      }
    );
  }

  getUploading() {
    return this.uploading;
  }

}
