import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Building} from '../../../entities/building';
import {AuthenticationService} from '../../authentication/authentication.service';
import {ResponseMessage} from '../../response-notification/response';

@Injectable({
  providedIn: 'root'
})
export class ConfigurationService {
  filename: string;

  constructor(private http: HttpClient,
              private loginService: AuthenticationService) {
  }

  public uploadFile(event): Observable<ResponseMessage> {
    this.filename = event.target.files[0].filename;
    return this.http.post<ResponseMessage>(`${environment.apiBaseUrl}/config/upload`, event.target.files[0]);
  }

  public saveAndConvert(buildingName: string, databaseUrl: string, token: string, organizationName: string): Observable<any> {
    const body: Building = {
      id: null, name: buildingName,
      influxDatabaseUrl: databaseUrl,
      influxDBToken: token,
      levels: null,
      organizationName: organizationName,
      user: JSON.parse(localStorage.getItem('user'))
    };
    return this.http.post<ResponseMessage>(`${environment.apiBaseUrl}/config/convert/`, body);
  }

}
