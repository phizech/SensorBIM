import {Inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from 'src/environments/environment';
import {DOCUMENT} from '@angular/common';
import {Sensor} from '../../entities/sensor';
import {ResponseMessage} from '../response-notification/response';

@Injectable({
  providedIn: 'root'
})
export class SensorService {

  constructor(private http: HttpClient,
              @Inject(DOCUMENT) private document: Document) {
  }

  public getChart() {
  }

  public getSensorForRoom(levelId: number, roomUri: string) {
    return this.http.get<Sensor[]>(`${environment.apiBaseUrl}/sensor/${levelId}/${roomUri}`);
  }

  public getMeasurements(sensorId: number, dateFrom) {
    return this.http.get<ResponseMessage>(`${environment.apiBaseUrl}/influx/getMeasurements/${sensorId}/${dateFrom}`);
  }

  public getMeasurementsForSensors(levelId: number, roomUri: string, measurement, dateFrom) {
    return this.http.get<ResponseMessage[]>
    (`${environment.apiBaseUrl}/influx/getSensorMeasurements/${levelId}/${roomUri}/${measurement}/${dateFrom}`);
  }

  public getLatestMeasurementsForSensor(buildingId: number, levelId: number, roomName: string, selectedSensorType: String) {
    return this.http.get<ResponseMessage>
    (`${environment.apiBaseUrl}/influx/getLatestSensorMeasurements/${buildingId}/${levelId}/${roomName}/${selectedSensorType}`);
  }
}
