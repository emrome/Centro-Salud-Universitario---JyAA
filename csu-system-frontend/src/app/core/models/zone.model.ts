import {Coordinate} from '@core/models/coordinate.model';

export interface Zone {
  id?: number;
  name: string;
  description?: string;
  neighborhoodId: number;
  coordinates: Coordinate[];
}
