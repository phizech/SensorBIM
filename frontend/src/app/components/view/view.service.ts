import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tempToColor} from 'temp-color';
import {environment} from '../../../environments/environment';
import {Level} from '../../entities/level';
import {Room} from '../../entities/room';
import {Sensor} from '../../entities/sensor';
import {SensorType} from '../../entities/sensor-type';
import {NotificationService} from '../response-notification/notification.service';
import {ResponseStatus} from '../response-notification/response-status';
import {ResponseMessage} from '../response-notification/response';
import {TranslateService} from '@ngx-translate/core';

declare var require: any;
const Color = require('color');

@Injectable({
  providedIn: 'root'
})
export class ViewService {

  public selectedSensor: Sensor;
  public sensorTypes = new Set<SensorType>([]);
  public selectedSensorType: SensorType;
  public temperature: number;

  constructor(public http: HttpClient,
              public notification: NotificationService,
              private translate: TranslateService) {
  }

  public getLevels(buildingId): Observable<Level[]> {
    return this.http.get<Level[]>(`${environment.apiBaseUrl}/level/all/${buildingId}`);
  }

  getRoomsForLevel(levelId: number) {
    return this.http.get<Room[]>(`${environment.apiBaseUrl}/room/rooms/${levelId}`);
  }

  public async _roomsToGeoJSON(rooms, selectedSensorType: String) {
    const geoJSON = {type: 'FeatureCollection', features: []};
    for (const d of rooms) {
      const uri: string = d.name;
      let color;
      let name;
      name = d.name;
      if (d.sensors.length === 0) {
        color = '#E8E8E8';
      } else {
        this.getLatestMeasurementForSensor(d.id, selectedSensorType).subscribe(result => {
          if (result.responseStatus === ResponseStatus.FAILURE) {
            this.notification.notify(ResponseStatus.FAILURE, result.message);
            color = '#3c3c3c';
          } else {
            this.notification.notify(ResponseStatus.INFO, result.message);
            color = this.getColor(result.body.value, d);
            this.temperature = Math.round(result.body.value * 100) / 100;
            name = d.name + ': ' + this.temperature + result.body.unit;
          }
        }, err => this.notification.notify(ResponseStatus.FAILURE, 'influx.connection.failed'));
        await this.delay(500);
      }
      const geometry = {type: 'Polygon', coordinates: [this.geometryStringToArray(d.geometry)]};
      const properties = {name: name, uri: uri, color: color};
      geoJSON.features.push({type: 'Feature', id: uri, geometry: geometry, properties: properties});
    }
    return geoJSON;
  }

  delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  private getLatestMeasurementForSensor(roomId: number, selectedSensorType: String) {
    return this.http.get<ResponseMessage>(`${environment.apiBaseUrl}/influx/getLatestSensorMeasurements/${roomId}/${selectedSensorType}`);
  }

  private geometryStringToArray(geometry): number[][] {
    const regex = new RegExp('[0-9]{1,13}(.[0-9]*)?', 'g');
    let x, y: number;
    const coordinates: number[][] = [];
    do {
      try {
        x = Number(regex.exec(geometry)[0]);
        y = Number(regex.exec(geometry)[0]);
      } catch (e) {
        return coordinates;

      }
      if (x) {
        coordinates.push([x, y]);
      }
    } while (x);
    return coordinates;
  }

  private getColor(temperature, d) {
    const {r, g, b} = tempToColor(temperature, d.sensors[0].minValue, d.sensors[0].maxValue);
    const color = Color.rgb(r, g, b);
    return color.fade(0.6);
  }

}


