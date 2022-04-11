import {CanActivateChild, Router, RouterStateSnapshot} from '@angular/router';
import {Injectable} from '@angular/core';
import {AuthenticationService} from './authentication.service';

@Injectable()
export class AuthGuard implements CanActivateChild {

  constructor(private authService: AuthenticationService, private router: Router) {
  }

  canActivateChild(route, state: RouterStateSnapshot) {
    if (this.authService.isLoggedIn()) {
      return true;
    }
    this.router.navigate(['/login']);
  }

}
