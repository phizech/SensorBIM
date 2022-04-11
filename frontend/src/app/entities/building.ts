import {Level} from './level';
import {User} from './user';

export interface Building {
  id: number;
  name: string;
  influxDatabaseUrl: string;
  influxDBToken: string;
  levels: Level[];
  organizationName: string;
  user: User;
}
