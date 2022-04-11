import {async, ComponentFixture, fakeAsync, inject, TestBed, tick} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {BuildingService} from './building.service';
import {environment} from '../../../environments/environment';
import {BuildingOverviewComponent} from './building-overview/building-overview.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatTableModule} from '@angular/material/table';
import {RouterTestingModule} from '@angular/router/testing';
import {MatPaginatorModule} from '@angular/material/paginator';
import {ResponseStatus} from '../response-notification/response-status';
import {Building} from '../../entities/building';

describe('EditBuildingComponent', () => {

  let component: BuildingOverviewComponent;
  let fixture: ComponentFixture<BuildingOverviewComponent>;

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
      providers: [BuildingService],
      declarations: [BuildingOverviewComponent]
    }).compileComponents().then(() => {
      fixture = TestBed.createComponent(BuildingOverviewComponent);
      component = fixture.componentInstance;
    });
  }));

  it(
    'should be initialized', () => {
      expect(component).toBeTruthy();
    });

  it(
    'should perform login correctly',
    fakeAsync(
      inject(
        [BuildingService, HttpTestingController],
        (buildingService: BuildingService, backend: HttpTestingController) => {
          const buildings: Building[] = [];
          const userId = 1;
          const url = `${environment.apiBaseUrl}/building/all/${userId}`;
          const responseObject = {
            responseStatus: ResponseStatus.SUCCESS,
            message: 'Gebäude erfolgreich geladen',
            body: buildings
          };

          let response = null;
          buildingService.getBuildings(userId).subscribe(
            (receivedResponse) => {
              response = receivedResponse;
            },
            () => {
            }
          );
          const requestWrapper = backend.expectOne(`${environment.apiBaseUrl}/building/all/${userId}`);
          requestWrapper.flush(responseObject);
          tick();
          expect(requestWrapper.request.method).toEqual('GET');
          expect(response.responseStatus).toEqual(ResponseStatus.SUCCESS);
          expect(response.message).toEqual('Gebäude erfolgreich geladen');
          expect(response.body).toBe(buildings);
        }
      )
    )
  );
});
