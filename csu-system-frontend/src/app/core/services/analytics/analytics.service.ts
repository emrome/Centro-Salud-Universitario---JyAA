import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '@env/environment';

// Usa los MODELOS del core (no declarar interfaces acá)
import { DemographicsSummaryDTO } from '@core/models/survey-analytics/demographics-summary.model';
import { GroupedCountDTO } from '@core/models/survey-analytics/grouped-count.model';
import { GroupCountDTO } from '@core/models/survey-analytics/group-count.model';

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/analytics/demographics`;

  getSummary(params: {
    neighborhoodId?: number;
    campaignId?: number;
    zoneId?: number;
  }): Observable<DemographicsSummaryDTO> {
    let p = new HttpParams();
    if (params.neighborhoodId != null) p = p.set('neighborhoodId', String(params.neighborhoodId));
    if (params.campaignId != null)     p = p.set('campaignId', String(params.campaignId));
    if (params.zoneId != null)         p = p.set('zoneId', String(params.zoneId));

    // Normalizamos por si el backend devuelve "ages" en lugar de "ageBuckets"
    return this.http.get<unknown>(`${this.baseUrl}/summary`, { params: p }).pipe(
      map(raw => normalizeSummary(raw))
    );
  }

  getAgePyramid(params: {
    neighborhoodId?: number;
    campaignId?: number;
    zoneId?: number;
  }): Observable<GroupedCountDTO[]> {
    let p = new HttpParams();
    if (params.neighborhoodId != null) p = p.set('neighborhoodId', String(params.neighborhoodId));
    if (params.campaignId != null)     p = p.set('campaignId', String(params.campaignId));
    if (params.zoneId != null)         p = p.set('zoneId', String(params.zoneId));

    // Normalizamos por si el backend devuelve "subGroup" en lugar de "subgroup"
    return this.http.get<unknown[]>(`${this.baseUrl}/age-pyramid`, { params: p }).pipe(
      map(rows => normalizePyramid(rows))
    );
  }
}

/* =========================
 * Normalizadores de payload
 * ========================= */

// El backend puede devolver {group,label,count}. Nos quedamos con "group".
type ApiGroupCount = Partial<GroupCountDTO> & { label?: string; count?: number };

function normalizeGroupCountArray(arr: any): GroupCountDTO[] {
  if (!Array.isArray(arr)) return [];
  return arr.map((x: ApiGroupCount) => ({
    group: (x.group ?? x.label ?? 'N/D'),
    count: Number(x.count ?? 0)
  }));
}

function normalizeSummary(raw: any): DemographicsSummaryDTO {
  const ageBuckets = normalizeGroupCountArray(raw?.ageBuckets ?? raw?.ages);
  const gender     = normalizeGroupCountArray(raw?.gender);
  const job        = normalizeGroupCountArray(raw?.job);
  const education  = normalizeGroupCountArray(raw?.education);
  const coverage   = normalizeGroupCountArray(raw?.coverage);

  return { ageBuckets, gender, job, education, coverage };
}

type ApiGrouped = Partial<GroupedCountDTO> & { subGroup?: string; subgroup?: string; count?: number };

function normalizePyramid(rows: any): GroupedCountDTO[] {
  if (!Array.isArray(rows)) return [];
  return rows.map((r: ApiGrouped) => ({
    group: r.group ?? 'N/D',
    subgroup: r.subgroup ?? r.subGroup ?? 'N/D',
    count: Number(r.count ?? 0)
  }));
}
