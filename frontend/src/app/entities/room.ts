import {Sensor} from './sensor';

export interface Room {
  id: number;
  name: string;
  sensors: Sensor[];
}
