import {CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Injectable} from '@angular/core';
import {AuthenticationService} from './authentication.service';

@Injectable()
export class AuthAdminGuard implements CanActivate {

  constructor(private authService: AuthenticationService, private router: Router) {
  }

  canActivate(route, state: RouterStateSnapshot) {
    if (this.authService.isAdmin()) {
      return true;
    }
    if (route.routeConfig.path === 'allBuildings') {
      this.router.navigate(['/myBuildings']);
    }
  }
}
