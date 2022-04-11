import {UserRole} from './user-role';

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  password: string;
  email: string;
  userRole: UserRole;
}
