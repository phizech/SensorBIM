import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Building} from '../../entities/building';
import {ResponseMessage} from '../response-notification/response';
import {TranslateService} from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class BuildingService {

  constructor(private http: HttpClient,
              private translate: TranslateService) {
  }

  public getBuildings(userId: number): Observable<Building[]> {
    return this.http.get<Building[]>(`${environment.apiBaseUrl}/building/allForUser/${userId}`);
  }

  public addBuilding(building: Building): Observable<Building> {
    return this.http.post<Building>(`${environment.apiBaseUrl}/building/add`, building);
  }

  public deleteBuilding(building: Building): Observable<any> {
    return this.http.post<Building>(`${environment.apiBaseUrl}/building/delete`, building);
  }

  public getAllBuildings(): Observable<Building[]> {
    return this.http.get<Building[]>(`${environment.apiBaseUrl}/building/all`);
  }

  public updateBuilding(building: Building): Observable<any> {
    return this.http.post<ResponseMessage>(`${environment.apiBaseUrl}/building/update`, building);
  }

  public getBuildingById(buildingId: number): Observable<Building> {
    return this.http.get<Building>(`${environment.apiBaseUrl}/building/byId/${buildingId}`);
  }
}
