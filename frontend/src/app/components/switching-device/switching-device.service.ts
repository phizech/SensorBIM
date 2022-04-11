import {Inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from 'src/environments/environment';
import {DOCUMENT} from '@angular/common';
import {SwitchingDevice} from '../../entities/switchingDevice';
import {SwitchingDeviceResult} from '../../entities/switching-device-result';

@Injectable({
  providedIn: 'root'
})
export class SwitchingDeviceService {

  constructor(private http: HttpClient,
              @Inject(DOCUMENT) private document: Document) {
  }

  public switchDevice(switchingDevice: SwitchingDevice) {
    return this.http.post<SwitchingDeviceResult>(`${environment.apiBaseUrl}/switchingDevice/switch`, switchingDevice);
  }

  public getSwitchingDevices(buildingId: number) {
    return this.http.get<SwitchingDeviceResult[]>(`${environment.apiBaseUrl}/switchingDevice/${buildingId}`);
  }

  switchMode(deviceId: number, automatic: boolean) {
    return this.http.get<SwitchingDeviceResult[]>
    (`${environment.apiBaseUrl}/switchingDevice/switchMode/${deviceId}/${automatic}`);
  }
}
