import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Zone } from '@core/models/zone.model';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';

@Injectable({ providedIn: 'root' })
export class ZoneService {
  private baseUrl = `${environment.apiUrl}/neighborhoods`;

  constructor(private http: HttpClient) {}

  getByNeighborhood(neighborhoodId: number): Observable<Zone[]> {
    return this.http.get<Zone[]>(`${this.baseUrl}/${neighborhoodId}/zones`);
  }

  delete(neighborhoodId: number, zoneId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${neighborhoodId}/zones/${zoneId}`);
  }

  getById(neighborhoodId: number, zoneId: number): Observable<Zone> {
    return this.http.get<Zone>(`${this.baseUrl}/${neighborhoodId}/zones/${zoneId}`);
  }

  update(neighborhoodId: number, zoneId: number, data: Zone): Observable<Zone> {
    return this.http.put<Zone>(`${this.baseUrl}/${neighborhoodId}/zones/${zoneId}`, data);
  }

  create(neighborhoodId: number, data: Zone): Observable<Zone> {
    return this.http.post<Zone>(`${this.baseUrl}/${neighborhoodId}/zones`, data);
  }
}

