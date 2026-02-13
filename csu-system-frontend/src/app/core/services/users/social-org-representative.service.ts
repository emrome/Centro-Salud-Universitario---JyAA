import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';
import { Observable } from 'rxjs';
import { SocialOrgRepresentative } from '@core/models/users/social-org-representative.model';

@Injectable({ providedIn: 'root' })
export class SocialOrgRepresentativeService {
  private baseUrl = `${environment.apiUrl}/representatives`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<SocialOrgRepresentative[]> {
    return this.http.get<SocialOrgRepresentative[]>(this.baseUrl);
  }

  getById(id: number): Observable<SocialOrgRepresentative> {
    return this.http.get<SocialOrgRepresentative>(`${this.baseUrl}/${id}`);
  }

  getByOrganization(orgId: number): Observable<SocialOrgRepresentative[]> {
    return this.http.get<SocialOrgRepresentative[]>(
      `${environment.apiUrl}/organizations/${orgId}/representatives`
    );
  }

  create(dto: SocialOrgRepresentative): Observable<SocialOrgRepresentative> {
    return this.http.post<SocialOrgRepresentative>(this.baseUrl, dto);
  }

  update(id: number, dto: SocialOrgRepresentative): Observable<SocialOrgRepresentative> {
    return this.http.put<SocialOrgRepresentative>(`${this.baseUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
