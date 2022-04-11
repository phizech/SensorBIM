import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User} from '../../entities/user';
import {environment} from 'src/environments/environment';
import {ResponseMessage} from '../response-notification/response';
import {NewUser} from '../../entities/new-user';
import {TranslateService} from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {

  constructor(private http: HttpClient,
              private translate: TranslateService) {
  }

  public getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.apiBaseUrl}/user/all`);
  }

  public getUserById(userId: number): Observable<User> {
    return this.http.get<User>(`${environment.apiBaseUrl}/user/find/${userId}`);
  }

  public updateUser(user: User): Observable<ResponseMessage> {
    return this.http.post<ResponseMessage>(`${environment.apiBaseUrl}/user/update`, user);
  }

  public deleteUser(user: User): Observable<void> {
    return this.http.post<void>(`${environment.apiBaseUrl}/user/delete`, user);
  }

  public addUser(body) {
    return this.http.post<ResponseMessage>(`${environment.apiBaseUrl}/user/add`, body);
  }

  resetPassword(email: any) {
    return this.http.get<ResponseMessage>(`${environment.apiBaseUrl}/auth/resetPassword/${email}`);
  }

  createAccount(body: NewUser) {
    return this.http.post<ResponseMessage>(`${environment.apiBaseUrl}/auth/createAccount`, body);
  }

  updatePassword(body) {
    return this.http.post<ResponseMessage>(`${environment.apiBaseUrl}/user/resetPassword`, body);
  }
}
