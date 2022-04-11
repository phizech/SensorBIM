import {Room} from './room';
import {TransmissionType} from './transmission-type';
import {SensorType} from './sensor-type';

export interface Sensor {
  id: number;
  name: string;
  uri: string;
  room: Room;
  maxValue: number;
  minValue: number;
  sensorType: SensorType;
  measuringUnit: string;
  transmissionType: TransmissionType;
  samplingRate: number;
  bucketName: string;
}
