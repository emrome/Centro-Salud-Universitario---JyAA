import { Coordinate } from '@core/models/coordinate.model';

export interface Neighborhood {
  id?: number;
  name: string;
  description: string;
  geolocation: Coordinate[];
}
