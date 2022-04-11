import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BuildingService} from '../building.service';
import {Building} from '../../../entities/building';
import {ActivatedRoute, Router} from '@angular/router';
import {NotificationService} from '../../response-notification/notification.service';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ResponseMessage} from '../../response-notification/response';
import {ResponseStatus} from '../../response-notification/response-status';


@Component({
  selector: 'app-building',
  templateUrl: './edit-building.component.html',
  styleUrls: ['../../CssTemplates/configuration.css']
})
export class EditBuildingComponent implements OnInit {

  building: Building;
  form: FormGroup;
  public buildingPath: string;

  constructor(private http: HttpClient,
              private buildingService: BuildingService,
              private route: ActivatedRoute,
              private router: Router,
              private notification: NotificationService) {
  }

  get buildingName() {
    return this.form.value.buildingName;
  }

  get influxDatabaseUrl() {
    return this.form.value.influxDatabaseUrl;
  }

  get organizationName() {
    return this.form.value.organizationName;
  }

  get influxDBToken() {
    return this.form.value.influxDBToken;
  }

  ngOnInit() {
    this.route.paramMap
      .subscribe(params => {
          const buildingId = +params.get('buildingId');
          this.buildingService.getBuildingById(buildingId).subscribe(res => {
            this.building = res;
            this.form = this.getForm();
          }, err => console.log(err));
        }
      );
  }

  public getForm() {
    return new FormGroup({
      buildingName: new FormControl(this.building.name, [
        Validators.required,
        Validators.minLength(3)
      ]),
      influxDatabaseUrl: new FormControl(this.building.influxDatabaseUrl, [
        Validators.minLength(3),
        Validators.required
      ]),
      organizationName: new FormControl(this.building.organizationName, [
        Validators.required,
        Validators.minLength(3)
      ]),
      influxDBToken: new FormControl(this.building.influxDBToken, [
        Validators.required,
        Validators.minLength(3)
      ])
    });
  }

  save() {
    const body: Building = {
      id: this.building.id,
      name: this.buildingName,
      influxDatabaseUrl: this.influxDatabaseUrl,
      influxDBToken: this.influxDBToken,
      levels: this.building.levels,
      organizationName: this.organizationName,
      user: JSON.parse(localStorage.getItem('user'))
    };
    this.buildingService.updateBuilding(body).subscribe(
      (res) => {
        const response: ResponseMessage = res.body;
        if (response.responseStatus === ResponseStatus.SUCCESS) {
          this.notification.notify(ResponseStatus.SUCCESS, response.message);
          this.router.navigate(['/myBuildings']);
        } else {
          this.notification.notify(response.responseStatus, response.message);
        }
      },
      () => {
        this.notification.notify(ResponseStatus.FAILURE, 'building.update.failure');
      }
    );
  }
}
