import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AppService} from './app.service';
import {AuthenticationService} from './components/authentication/authentication.service';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [AppService]
})
export class AppComponent implements OnInit {

  public selectedLang;

  constructor(
    private http: HttpClient,
    private _s: AppService,
    public loginService: AuthenticationService,
    private translate: TranslateService
  ) {
  }

  ngOnInit() {
    // if no language is set, set it to english
    if ((localStorage.getItem('lang') === 'undefined') || (localStorage.getItem('lang') === 'null')) {
      // default language is english
      this.selectedLang = 'gb';
      localStorage.setItem('lang', 'gb');
    } else {
      this.selectedLang = localStorage.getItem('lang');
    }
    this.changeLang(this.selectedLang);
  }

  changeLang(lang: string) {
    this.translate.setDefaultLang(lang);
    this.selectedLang = lang;
    localStorage.setItem('lang', lang);
  }


}
