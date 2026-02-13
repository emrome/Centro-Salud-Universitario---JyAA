import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';
import { Observable } from 'rxjs';
import { ReportRequest } from '@core/models/report-request.model';

@Injectable({ providedIn: 'root' })
export class ReportRequestService {
  private baseUrl = `${environment.apiUrl}/report-requests`;

  constructor(private http: HttpClient) {}

  getById(id: number): Observable<ReportRequest> {
    return this.http.get<ReportRequest>(`${this.baseUrl}/${id}`);
  }

  getByRequesterMe(): Observable<ReportRequest[]> {
    return this.http.get<ReportRequest[]>(`${this.baseUrl}/requester/me`);
  }

  getPendingForHealth(): Observable<ReportRequest[]> {
    return this.http.get<ReportRequest[]>(`${this.baseUrl}/pending`);
  }

  create(data: { description: string }): Observable<ReportRequest> {
    return this.http.post<ReportRequest>(this.baseUrl, data);
  }

  reject(id: number, reason?: string): Observable<ReportRequest> {
    return this.http.post<ReportRequest>(`${this.baseUrl}/${id}/reject`, reason ? { reason } : {});
  }

  completeWithReport(
    id: number,
    opts: {
      name: string;
      description: string;
      visibleToAllHealthStaff: boolean;
      isPublic: boolean;
      file: File;
    }
  ) {
    const form = new FormData();
    form.append('name', opts.name);
    form.append('description', opts.description);
    form.append('visibleToAllHealthStaff', String(opts.visibleToAllHealthStaff));
    form.append('isPublic', String(opts.isPublic));
    form.append('file', opts.file);
    return this.http.post<ReportRequest>(`${this.baseUrl}/${id}/complete`, form);
  }

  downloadCompletedReportFile(reportId: number): Observable<Blob> {
    return this.http.get(`${environment.apiUrl}/reports/${reportId}/file`, { responseType: 'blob' });
  }
  getResolvedForHealth() {
    return this.http.get<ReportRequest[]>(`${this.baseUrl}/resolved`);
  }

  getRejectedForHealth() {
    return this.http.get<ReportRequest[]>(`${this.baseUrl}/rejected`);
  }
}
