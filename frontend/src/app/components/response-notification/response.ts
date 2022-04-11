import {ResponseStatus} from './response-status';

export interface ResponseMessage {
  responseStatus: ResponseStatus;
  message: string;
  body: any;
}
