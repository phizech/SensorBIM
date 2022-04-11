import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class AppService {


  constructor(public http: HttpClient) {
  }


}
