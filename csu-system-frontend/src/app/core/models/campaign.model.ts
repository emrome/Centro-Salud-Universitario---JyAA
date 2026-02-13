import { Survey } from '@core/models/survey.model';

export interface Campaign {
  id?: number;
  name: string;
  startDate: string;
  endDate: string;
  neighborhoodId: number;
  survey?: Survey;
}
