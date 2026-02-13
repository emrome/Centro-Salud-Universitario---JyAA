import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';

export interface LatLngDTO { lat: number; lng: number; }
export interface ZoneCountDTO { zoneId: number; zoneName: string; count: number; }

@Injectable({ providedIn: 'root' })
export class MapAnalyticsService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/analytics/map`;

  heatByDisease(params: { neighborhoodId: number; condition?: string; minAge?: number; }): Observable<LatLngDTO[]> {
    let p = new HttpParams().set('neighborhoodId', String(params.neighborhoodId));
    if (params.condition) p = p.set('condition', params.condition);
    if (params.minAge != null) p = p.set('minAge', String(params.minAge));
    return this.http.get<LatLngDTO[]>(`${this.base}/heat-disease`, { params: p });
  }

  zoneCounts(params: { neighborhoodId: number; condition?: string; minAge?: number; }): Observable<ZoneCountDTO[]> {
    let p = new HttpParams().set('neighborhoodId', String(params.neighborhoodId));
    if (params.condition) p = p.set('condition', params.condition);
    if (params.minAge != null) p = p.set('minAge', String(params.minAge));
    return this.http.get<ZoneCountDTO[]>(`${this.base}/zone-counts`, { params: p });
  }
}

