import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';
import { Observable } from 'rxjs';
import { Report } from '@core/models/report.model';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private baseUrl = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Report[]> {
    return this.http.get<Report[]>(this.baseUrl);
  }

  getById(id: number): Observable<Report> {
    return this.http.get<Report>(`${this.baseUrl}/${id}`);
  }

  create(data: Report): Observable<Report> {
    return this.http.post<Report>(this.baseUrl, data);
  }

  update(id: number, data: Report): Observable<Report> {
    return this.http.put<Report>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  attachFile(id: number, file: File): Observable<Report> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<Report>(`${this.baseUrl}/${id}/file`, form);
  }

  downloadFile(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${id}/file`, { responseType: 'blob' });
  }

  shareWithRepresentative(id: number, representativeId: number): Observable<Report> {
    return this.http.post<Report>(`${this.baseUrl}/${id}/share/${representativeId}`, {});
  }

  revokeShare(id: number, representativeId: number): Observable<Report> {
    return this.http.delete<Report>(`${this.baseUrl}/${id}/share/${representativeId}`);
  }

  getPublic(): Observable<Report[]> {
    return this.http.get<Report[]>(`${this.baseUrl}/public`);
  }

}
