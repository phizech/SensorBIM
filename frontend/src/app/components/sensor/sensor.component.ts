import {Component, OnInit, Renderer2} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {SensorService} from './sensor.service';
import {MeasurePoint} from './measurePoint';
import {Sensor} from '../../entities/sensor';
import {SensorType} from '../../entities/sensor-type';
import {FormControl} from '@angular/forms';
import {Chart} from 'chart.js';
import {ResponseStatus} from '../response-notification/response-status';
import {NotificationService} from '../response-notification/notification.service';

@Component({
  selector: 'app-sensor',
  templateUrl: './sensor.component.html',
  styleUrls: ['../CssTemplates/tables.css', './sensor.css']
})
export class SensorComponent implements OnInit {

  public buildingId;
  public selectedSensors: Sensor[];
  public levelId;
  public roomUri;
  public sensors: Sensor[];
  public measurements: Map<String, MeasurePoint[]>;
  public avgMeasurement: MeasurePoint;
  public maxMeasurement: MeasurePoint;
  public minMeasurement: MeasurePoint;
  public sensorTypesInRoom = new Set<SensorType>([]);
  public selectedSensorType: SensorType;
  public today = new Date().toISOString().slice(0, 10);
  public date = new FormControl(new Date().toISOString().slice(0, 10));
  public mode = ['analysis', 'control'];
  public selectedMode = this.mode[0];
  public chart: any;
  public ctx: any;

  public chartType = 'line';
  headers = {
    sensorName: 'Sensorname',
    sensorType: 'Sensortyp',
    time: 'Zeitpunkt',
    startTime: 'Beginn',
    stopTime: 'Ende',
    value: 'Messung',
    measurement: 'Messung',
  };
  loading: boolean;


  constructor(
    private renderer2: Renderer2,
    private http: HttpClient,
    private sensorService: SensorService,
    private route: ActivatedRoute,
    private notify: NotificationService,
    private router: Router) {
  }

  createChart(rounding, days, unit) {
    if (this.chart) {
      this.chart.destroy();
    }
    this.ctx = document.getElementById('myChart');
    const data = this.getXAndYValues();
    const graphParams = {
      type: 'line',
      data: {
        datasets: data,
      },
      options: {
        maintainAspectRatio: true,
        scales: {
          xAxes: [{
            type: 'time',
            distribution: 'series',
            time: {
              unit: unit,
              round: rounding,
            },
          }],
        }
      }

    };
    this.chart = new Chart(this.ctx, graphParams);
  }

  ngOnInit() {
    this.route.paramMap
      .subscribe(params => {
          this.levelId = +params.get('levelId');
          this.roomUri = params.get('roomUri');
          this.buildingId = params.get('buildingId');
        }
      );
    this.getSensorsForRoom();
    this.selectedMode = this.mode[0];
    this.showSelectedMode('analysis');
  }

  public onDate(): void {
    const difference = (Date.parse(this.today) - Date.parse(this.date.toString())) / (1000 * 3600 * 24);
    let unit = 'hour';
    if (difference > 0) {
      unit = 'day';
    }
    this.getMeasurementsForSensor(true, difference, unit);
  }

  public getDataForRoom(res) {
    const filtered = res.filter(Boolean);
    if (filtered.length === 0) {
      return null;
    }
    this.maxMeasurement = filtered.reduce((acc, cur) => ((acc.value > cur.value) ? acc : cur));
    this.maxMeasurement.value = Math.round(this.maxMeasurement.value * 100) / 100;
    this.minMeasurement = filtered.reduce((acc, cur) => ((acc.value < cur.value) ? acc : cur));
    this.minMeasurement.value = Math.round(this.minMeasurement.value * 100) / 100;
    this.avgMeasurement = filtered.reduce((acc, cur, idx, arr) => {
      let sum = acc.value + cur.value;
      const no = idx + 1;
      if (no === arr.length) {
        sum = sum / no;
      }
      return {'time': cur.time, 'value': (Math.round(sum * 100) / 100)};
    });

  }

  convertToCSV_(objArray) {
    const array = typeof objArray !== 'object' ? JSON.parse(objArray) : objArray;
    let str = '';
    for (let i = 0; i < array.length; i++) {
      let line = '';
      // tslint:disable-next-line:forin
      for (const index in array[i]) {
        if (line !== '') {
          line += ',';
        }
        line += array[i][index];
      }
      str += line + '\r\n';
    }
    return str;
  }

  exportCSVFile() {
    const data = ([]).concat(...Object.values(Array.from(this.measurements.values())));
    if (this.headers) {
      data.unshift(this.headers);
    }
    const jsonObject = JSON.stringify(data);
    const csv = this.convertToCSV_(jsonObject);
    const exportedFilenmae = 'Sensorwerte von ' + this.today + ' bis ' + this.date.value + '.csv';

    const blob = new Blob([csv], {type: 'text/csv;charset=utf-8;'});
    if (navigator.msSaveBlob) { // IE 10+
      navigator.msSaveBlob(blob, exportedFilenmae);
    } else {
      const link = document.createElement('a');
      if (link.download !== undefined) {
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', exportedFilenmae);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }
    }
  }

  public switchType(btn: SensorType) {
    this.selectedSensorType = btn;
    this.selectedSensors = this.sensors.filter(i => i.sensorType === btn);
    this.getMeasurementsForSensor(true, 0, 'hour');
  }

  public showSelectedMode(mode) {
    this.selectedMode = mode;
    if (mode === 'control') {
      document.getElementById('analysis').style.display = 'none';
      document.getElementById('control').style.display = 'block';
    } else {
      document.getElementById('control').style.display = 'none';
      document.getElementById('analysis').style.display = 'block';
    }
  }

  navigateToControl() {
    this.router.navigate(['/switchingDevices/' + this.buildingId]);
  }

  private getSensorsForRoom() {
    this.sensorService.getSensorForRoom(this.levelId, this.roomUri).subscribe(res => {
      this.sensors = res;
      const types = res.map(a => a.sensorType);
      types.forEach(item => this.addItem(item));
      this.selectedSensors = res.filter(i => i.sensorType === this.sensorTypesInRoom.values().next().value);
      this.selectedSensorType = this.sensorTypesInRoom.keys().next().value;
      this.getMeasurementsForSensor(true, 0, 'hour');
    }, err => console.log(err));
  }

  private addItem(item: SensorType) {
    // @ts-ignore
    if (SensorType[item] === SensorType.MULTI) {
      this.sensorTypesInRoom.add(SensorType.TEMPERATURE);
      this.sensorTypesInRoom.add(SensorType.HUMIDITY);
    } else {
      this.sensorTypesInRoom.add(item);
    }
  }

  private async getMeasurementsForSensor(rounding, days, unit) {
    let dateFrom: string;
    if (this.date.untouched) {
      dateFrom = this.date.value.toString();
    } else {
      dateFrom = this.date.toString();
    }
    this.loading = true;
    this.sensorService.getMeasurementsForSensors(this.levelId, this.roomUri, this.selectedSensorType, dateFrom).subscribe(res => {
      res.forEach((response) => {
        if (response.responseStatus === ResponseStatus.SUCCESS) {
          this.measurements = new Map(Object.entries(response.body));
          this.getDataForRoom(([]).concat(...Object.values(response.body)));
          this.createChart(rounding, days, unit);
          this.notify.notify(ResponseStatus.SUCCESS, response.message);
        } else {
          this.notify.notify(ResponseStatus.WARNING, response.message);
        }
      });
      this.loading = false;
    }, () => {
      this.notify.notify(ResponseStatus.FAILURE, 'influx.connection.failed');
      this.loading = false;
    });
  }

  private getXAndYValues() {
    const data = [];
    this.measurements.forEach((value: MeasurePoint[], key: String) => {
      if (value != null) {
        const sensorData = [];
        const n = Math.round(value.length / 38);
        value.filter((measurement, index) => {
          if (index % n === 0) {
            sensorData.push({x: measurement.time, y: measurement.value});
          }
        });
        data.push(
          {
            'data': sensorData,
            'label': key,
            'backgroundColor': 'transparent',
            'borderColor': '#' + Math.floor(Math.random() * 16777215).toString(16)
          });
      }
    });
    return data;
  }
}
