import {BrowserModule} from '@angular/platform-browser';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {AppComponent} from './app.component';
import {MatButtonModule, MatNativeDateModule, MatRadioModule, MatSelectModule} from '@angular/material';
import {JwtModule} from '@auth0/angular-jwt';
import {PlanModule} from 'ng-plan';
import {MeshViewerModule} from 'ng-mesh-viewer';
import {NgxPaginationModule} from 'ngx-pagination';
import {AngularDraggableModule} from 'angular2-draggable';
import {MarkdownToHtmlModule} from 'markdown-to-html-pipe';
import {ConfigurationComponent} from './components/building/create-building/configuration.component';
import {ViewComponent} from './components/view/view.component';
import {InfoComponent} from './components/info/info.component';
import {BuildingOverviewComponent} from './components/building/building-overview/building-overview.component';
import {UserOverviewComponent} from './components/user-management/user-overview/user-overview';
import {AddUserComponent} from './components/user-management/add-user/add-user.component';
import {SensorComponent} from './components/sensor/sensor.component';
import {MatIconModule} from '@angular/material/icon';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatTableModule} from '@angular/material/table';
import {AuthenticationComponent} from './components/authentication/authentication.component';
import {TokenInterceptor} from './TokenInterceptor';
import {AuthGuard} from './components/authentication/auth.guard.service';
import {AuthAdminGuard} from './components/authentication/authAdmin.guard.service';
import {ResetPasswordComponent} from './components/user-management/reset-password/reset-password.component';
import {RegisterUserComponent} from './components/user-management/register-user/register-user.component';
import {EditUserComponent} from './components/user-management/edit-user/edit-user.component';
import {EditBuildingComponent} from './components/building/edit-building/edit-building.component';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {MatCardModule} from '@angular/material/card';
import {MatSortModule} from '@angular/material/sort';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {SwitchingDeviceComponent} from './components/switching-device/switching-device.component';

@NgModule({
  declarations: [
    AppComponent,
    ConfigurationComponent,
    ViewComponent,
    InfoComponent,
    BuildingOverviewComponent,
    UserOverviewComponent,
    AddUserComponent,
    SensorComponent,
    AuthenticationComponent,
    ResetPasswordComponent,
    RegisterUserComponent,
    EditUserComponent,
    EditBuildingComponent,
    SwitchingDeviceComponent
  ],
  exports: [
    MatPaginatorModule
  ],
  imports: [
    BrowserModule,
    MatPaginatorModule,
    MatTableModule,
    MatSortModule,
    MatCardModule,
    MatIconModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatNativeDateModule,
    MatNativeDateModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    PlanModule,
    MeshViewerModule,
    AngularDraggableModule,
    MarkdownToHtmlModule,
    MatButtonModule,
    MatSelectModule,
    MatRadioModule,
    NgxPaginationModule,
    RouterModule.forRoot([
      {path: 'login', component: AuthenticationComponent},
      {path: 'resetPassword', component: ResetPasswordComponent},
      {path: 'createAccount', component: RegisterUserComponent},
      {
        path: '', canActivateChild: [AuthGuard], children: [
          {path: 'view/:buildingId', component: ViewComponent},
          {path: 'switchingDevices/:buildingId', component: SwitchingDeviceComponent},
          {path: ':buildingId/sensor/:levelId/:roomUri', component: SensorComponent},
          {path: 'addBuilding', component: ConfigurationComponent},
          {path: 'userManagement', component: UserOverviewComponent, canActivate: [AuthAdminGuard]},
          {path: 'addUser', component: AddUserComponent},
          {path: 'editUser/:userId', component: EditUserComponent},
          {path: 'allBuildings', component: BuildingOverviewComponent, canActivate: [AuthAdminGuard]},
          {path: 'editBuilding/:buildingId', component: EditBuildingComponent},
          {path: 'myBuildings', component: BuildingOverviewComponent},
          {path: 'home', component: InfoComponent},
          {path: '**', component: InfoComponent},
        ]
      }
    ]),
    ReactiveFormsModule,
    MatIconModule,
    MatDatepickerModule,
    MatTableModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    JwtModule.forRoot({
      config: {
        tokenGetter: () => {
          return localStorage.getItem('token');
        },
      },
    }),
    MatSlideToggleModule,
    MatButtonToggleModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    },
    AuthGuard, AuthAdminGuard
  ],
  bootstrap: [AppComponent]
})


export class AppModule {
}


export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http);
}
