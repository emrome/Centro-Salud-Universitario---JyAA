import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';
import { Observable } from 'rxjs';
import { Event } from '@core/models/event.model';
import { Surveyor } from '@core/models/surveyor.model';

@Injectable({ providedIn: 'root' })
export class EventService {
  private baseUrl = `${environment.apiUrl}/campaign`;

  constructor(private http: HttpClient) {}

  getByCampaignId(campaignId: number): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.baseUrl}/${campaignId}/events`);
  }

  getById(campaignId: number, id: number): Observable<Event> {
    return this.http.get<Event>(`${this.baseUrl}/${campaignId}/events/${id}`);
  }

  create(campaignId: number, data: Event): Observable<Event> {
    return this.http.post<Event>(`${this.baseUrl}/${campaignId}/events`, data);
  }

  update(campaignId: number, id: number, data: Event): Observable<Event> {
    return this.http.put<Event>(`${this.baseUrl}/${campaignId}/events/${id}`, data);
  }

  delete(campaignId: number, id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${campaignId}/events/${id}`);
  }

  getSurveyors(campaignId: number, id: number): Observable<Surveyor[]> {
    return this.http.get<Surveyor[]>(`${this.baseUrl}/${campaignId}/events/${id}/surveyors`);
  }
}
