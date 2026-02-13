import { GroupCountDTO } from './group-count.model';

export interface DemographicsSummaryDTO {
  ageBuckets: GroupCountDTO[];
  gender:     GroupCountDTO[];
  job:        GroupCountDTO[];
  education:  GroupCountDTO[];
  coverage:   GroupCountDTO[];
}
