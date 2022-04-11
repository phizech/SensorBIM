import {Building} from './building';

export interface Level {
  id: number;
  name: string;
  uri: string;
  building: Building;
}
