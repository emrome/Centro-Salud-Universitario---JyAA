import { MainActivityOption } from '../enums/main-activity.enum';

export class Organization {
  id?: number;
  name!: string;
  address?: string;
  mainActivity!: MainActivityOption['value']
  neighborhoodId?: number;
}
