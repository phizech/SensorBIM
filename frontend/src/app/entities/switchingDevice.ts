import {Room} from './room';

export interface SwitchingDevice {
  id: number;
  name: string;
  token: string;
  ip: string;
  onPath: string;
  offPath: string;
  slug: string;
  statusPath: string;
  status: boolean;
  room: Room;
  automatic: boolean;
  comment: string;
}
