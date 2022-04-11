import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Notify} from 'notiflix';
import {ResponseStatus} from './response-status';
import {TranslateService} from '@ngx-translate/core';


@Injectable({
  providedIn: 'root'
})
export class NotificationService {


  constructor(public http: HttpClient,
              private translateService: TranslateService) {
  }

  public notify(type: ResponseStatus, message: string) {
    message = this.translateService.instant(message);
    switch (type.valueOf()) {
      case 'SUCCESS':
        Notify.success(message);
        break;
      case 'FAILURE':
        Notify.failure(message);
        break;
      case 'INFO':
        Notify.info(message);
        break;
      case 'WARNING':
        Notify.warning(message);
        break;
    }
  }

}
