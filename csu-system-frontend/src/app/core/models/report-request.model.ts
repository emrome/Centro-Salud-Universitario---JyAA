export type RequestStatus = 'PENDING' | 'COMPLETED' | 'REJECTED';

export interface ReportRequest {
  id?: number;
  reportId?: number | null;
  requesterId?: number | null;
  requesterName?: string | null;
  status: RequestStatus;
  description?: string | null;
  resolvedById?: number | null;
  resolvedByName?: string | null;
  resolvedAt?: string | null;
  createdAt?: string | null;
}
