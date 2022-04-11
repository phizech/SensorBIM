import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import {SwitchingDeviceService} from './switching-device.service';
import {SwitchingDeviceResult} from '../../entities/switching-device-result';

@Component({
  selector: 'app-switching-device',
  templateUrl: './switching-device.component.html',
  styleUrls: ['../CssTemplates/tables.css', './switching-device.component.css', '../CssTemplates/buttons.css']
})
export class SwitchingDeviceComponent implements OnInit {

  public dataSource: SwitchingDeviceResult[];
  displayedColumns: string[] = ['device', 'lastChanged', 'switch', 'status'];
  public loading = false;
  public selectedDevice: SwitchingDeviceResult;
  baseButton = 'baseButton';
  private buildingId: number;

  constructor(
    private http: HttpClient,
    private switchingDeviceService: SwitchingDeviceService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit() {
    this.route.paramMap
      .subscribe(params => {
          this.buildingId = +params.get('buildingId');
        }
      );
    this.loading = true;
    this.getSwitchingDevicesForRoom();
  }

  public getSwitchingDevicesForRoom() {
    this.switchingDeviceService.getSwitchingDevices(this.buildingId).subscribe(res => {
        this.loading = false;
        this.dataSource = res;
        /*
        this.dataSource[0].device.name = 'Temperature Switcher';
        this.dataSource[0].latestMeanMeasurement = 12;
        this.dataSource[0].lastChanged = '2022-03-04T10:33:15.99Z';
       tslint:disable-next-line:prefer-const
        let device1: SwitchingDeviceResult = {};
        device1.latestMeanMeasurement = 23;
        device1.active = true;
        device1.device = {};
        device1.device.status = 'ON';
        device1.device.name = 'SD Room 1';
        device1.device.comment = '';
        device1.device.automatic = false;
        device1.lastChanged = '2022-02-28T10:08:39.8Z';

      let device5: SwitchingDeviceResult = {};
      device5.latestMeanMeasurement = 23;
      device5.active = false;
      device5.device = {};
      device5.device.status = 'ON';
      device5.device.name = 'SD 1st Floor';
      device5.device.comment = '';
      device5.device.automatic = true;
      device5.lastChanged = '2022-03-01T05:33:24.376Z';

      let device2: SwitchingDeviceResult = {};
      device2.latestMeanMeasurement = 23;
      device2.active = true;
      device2.device = {};
      device2.device.status = 'ON';
      device2.device.name = 'SD 2nd Floor';
      device2.device.comment = '';
      device2.device.automatic = true;
      device2.lastChanged = '2022-02-12T08:07:15.356Z';

      let device3: SwitchingDeviceResult = {};
      device3.latestMeanMeasurement = 23;
      device3.active = true;
      device3.device = {};
      device3.device.status = 'OFF';
      device3.device.name = 'SD Room 17';
      device3.device.comment = '';
      device3.device.automatic = false;
      device3.lastChanged = '2022-03-04T06:02:28.584Z';

      let device4: SwitchingDeviceResult = {};
      device4.latestMeanMeasurement = 23;
      device4.active = true;
      device4.device = {};
      device4.device.status = 'OFF';
      device4.device.name = 'Humidity Switcher';
      device4.device.comment = '';
      device4.device.automatic = true;
      device4.lastChanged = '2022-03-04T02:45:28:698Z';

      let device6: SwitchingDeviceResult = {};
      device6.latestMeanMeasurement = 23;
      device6.active = true;
      device6.device = {};
      device6.device.status = 'OFF';
      device6.device.name = 'SD Room 21';
      device6.device.comment = '';
      device6.device.automatic = true;
      device6.lastChanged = '2022-03-12T12:18:03:985Z';

      let device7: SwitchingDeviceResult = {};
      device7.latestMeanMeasurement = 23;
      device7.active = false;
      device7.device = {};
      device7.device.status = 'OFF';
      device7.device.name = 'Humidity Switcher 2';
      device7.device.comment = '';
      device7.device.automatic = false;
      device7.lastChanged = '2022-03-04T02:45:28:698Z';

        this.dataSource.push(device1, device2, device3, device4, device5, device6, device7);

         */
      }
    );
  }

  public switchDevice() {
    this.switchingDeviceService.switchDevice(this.selectedDevice.device).subscribe(res => {
        this.selectedDevice = res;
        this.getSwitchingDevicesForRoom();
      }
    );
  }

  setSelectedDevice(device) {
    this.selectedDevice = device;
  }

  getButtonClass(swResult) {
    let classButton = this.baseButton;
    if (swResult.device.automatic) {
      classButton += ' automaticSwitch ';
    }
    if (swResult.device.status === 'ON') {
      classButton += ' onEnabled';
    } else if (swResult.device.status === 'OFF') {
      classButton += ' offEnabled';
    } else if ((swResult.device.status === 'UNKNOWN')) {
      classButton += ' disabled';
    }
    return classButton;
  }

  getButtonClassAutomatic(device) {
    let classButton = this.baseButton;
    if (device.device.automatic) {
      classButton += ' automatic';
    } else {
      classButton += ' manual';
    }
    if ((device.device.status === 'UNKNOWN')) {
      classButton += ' disabled';
    }
    return classButton;
  }

  switchMode() {
    this.switchingDeviceService.switchMode(this.selectedDevice.device.id, !this.selectedDevice.device.automatic).subscribe(res => {
        this.getSwitchingDevicesForRoom();
      }
    );
  }

  getComment() {
    if (this.selectedDevice) {
      return this.selectedDevice.device.comment;
    }
    return '';
  }
}
