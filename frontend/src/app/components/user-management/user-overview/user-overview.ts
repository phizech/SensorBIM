import {Component, OnInit, ViewChild} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {UserManagementService} from '../user-management.service';
import {User} from '../../../entities/user';
import {NotificationService} from '../../response-notification/notification.service';
import {ResponseStatus} from '../../response-notification/response-status';
import {UserRole} from '../../../entities/user-role';
import {AuthenticationService} from '../../authentication/authentication.service';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-overview.html',
  styleUrls: ['../../CssTemplates/tables.css']
})
export class UserOverviewComponent implements OnInit {

  displayedColumns: string[] = ['id', 'username', 'firstName', 'lastName', 'userRole', 'icons'];
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  private users;
  dataSource = new MatTableDataSource<User>(this.users);
  private selectedUser: User;

  constructor(private http: HttpClient,
              private userManagementService: UserManagementService,
              private notification: NotificationService,
              private notify: NotificationService,
              private authService: AuthenticationService) {
  }

  ngOnInit() {
    this.getUsers();
  }

  public getUsers(): void {
    this.userManagementService.getUsers().subscribe(
      (response: any) => {
        this.users = response;
        this.initDataSource();
      },
      () => {
        this.notification.notify(ResponseStatus.FAILURE, 'user.loadUserFailed');
      }
    );
  }

  async clickMethod() {
    await this.deleteUser(this.selectedUser);
  }

  setSelectedUser(user: User) {
    this.selectedUser = user;
  }

  getRole(userRole: any) {
    return UserRole[userRole];
  }

  currentUserId() {
    return this.authService.getUserId();
  }

  initDataSource() {
    this.dataSource = new MatTableDataSource<User>(this.users);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  private async deleteUser(selectedUser: User) {
    this.userManagementService.deleteUser(selectedUser).subscribe(
      () => {
        this.users = this.getUsers();
        this.notify.notify(ResponseStatus.SUCCESS, 'user.delete.success');
      },
      () => {
        this.notify.notify(ResponseStatus.FAILURE, 'user.delete.failed');
      }
    );
  }
}
