import {Component, OnInit, ViewChild} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {BuildingService} from '../building.service';
import {AuthenticationService} from '../../authentication/authentication.service';
import {Building} from '../../../entities/building';
import {ActivatedRoute} from '@angular/router';
import {NotificationService} from '../../response-notification/notification.service';
import {ResponseStatus} from '../../response-notification/response-status';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';

@Component({
  selector: 'app-building',
  templateUrl: './building-overview.component.html',
  styleUrls: ['../../CssTemplates/tables.css']
})
export class BuildingOverviewComponent implements OnInit {
  displayedColumns: string[] = ['id', 'name', 'Stockwerke', 'icons'];
  displayedColumnsAll: string[] = ['id', 'name', 'Stockwerke', 'Benutzer', 'icons'];
  public buildings;
  dataSource = new MatTableDataSource<Building>(this.buildings);
  public buildingPath: string;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  private selectedBuilding: Building;

  constructor(private http: HttpClient,
              private buildingService: BuildingService,
              private authService: AuthenticationService,
              private route: ActivatedRoute,
              private notify: NotificationService) {
  }

  ngOnInit() {
    this.route.url
      .subscribe(url => {
          this.buildingPath = url[0].path;
          this.getBuildings();
        }
      );
  }

  public getBuildingsForUser(): void {
    this.buildingService.getBuildings(this.authService.getUserId()).subscribe(
      (response: any) => {
        this.buildings = response;
        this.initDataSource();
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  public deleteBuilding(building: Building): void {

    this.buildingService.deleteBuilding(building).subscribe(
      () => {
        this.getBuildings();
        this.notify.notify(ResponseStatus.SUCCESS, 'building.deleting.success');
      },
      (error: HttpErrorResponse) => {
        this.notify.notify(ResponseStatus.FAILURE, 'building.deleting.failure');
      }
    );
  }

  async clickMethod() {
    await this.deleteBuilding(this.selectedBuilding);
  }

  setSelectedBuilding(building: Building) {
    this.selectedBuilding = building;
  }

  initDataSource() {
    this.dataSource = new MatTableDataSource<Building>(this.buildings);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  private getAllBuildings() {
    this.buildingService.getAllBuildings().subscribe(
      (response: any) => {
        this.buildings = response;
        this.initDataSource();
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  private getBuildings() {
    if (this.buildingPath === 'allBuildings') {
      this.getAllBuildings();
    } else {
      this.getBuildingsForUser();
    }
  }
}
