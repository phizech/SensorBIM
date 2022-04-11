import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from 'src/environments/environment';
import {JwtHelperService} from '@auth0/angular-jwt';
import {User} from '../../entities/user';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  username: String;

  constructor(private http: HttpClient) {
  }

  public login(body) {
    return this.http.post(`${environment.apiBaseUrl}/auth/authenticate`, body);
  }

  public logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  public isLoggedIn() {
    const jwtHelper = new JwtHelperService();
    const token = localStorage.getItem('token');
    if (!token) {
      return false;
    }
    const isExpired = jwtHelper.isTokenExpired(token);
    return !isExpired;
  }

  public getCurrentUser() {
    const token = localStorage.getItem('token');
    if (!token) {
      return null;
    }
    return new JwtHelperService().decodeToken(token);
  }

  public getUserId() {
    const user: User = JSON.parse(localStorage.getItem('user'));
    return user.id;
  }

  public getToken() {
    return localStorage.getItem('token');
  }

  public setToken(token: string) {
    localStorage.setItem('token', token);
  }

  public setUser(user: string) {
    localStorage.setItem('user', user);
  }

  public isAdmin() {
    return this.getCurrentUser().roles === 'ADMIN';
  }
}
