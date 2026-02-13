import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';
import { Observable } from 'rxjs';
import { Campaign } from '@core/models/campaign.model';

@Injectable({ providedIn: 'root' })
export class CampaignService {
  private baseUrl = `${environment.apiUrl}/campaigns`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Campaign[]> {
    return this.http.get<Campaign[]>(this.baseUrl);
  }

  getById(id: number): Observable<Campaign> {
    return this.http.get<Campaign>(`${this.baseUrl}/${id}`);
  }

  create(data: Campaign): Observable<Campaign> {
    return this.http.post<Campaign>(this.baseUrl, data);
  }

  update(id: number, data: Campaign): Observable<Campaign> {
    return this.http.put<Campaign>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  importSurvey(id: number, data: FormData) {
    return this.http.post(this.surveyUrl(id), data);
  }

  deleteSurvey(id: number): Observable<void> {
    return this.http.delete<void>(this.surveyUrl(id));
  }

  private surveyUrl(id: number): string {
    return `${this.baseUrl}/${id}/survey/`;
  }
}
