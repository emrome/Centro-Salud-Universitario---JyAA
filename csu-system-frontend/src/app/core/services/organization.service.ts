// src/app/core/services/organization.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';
import { Observable } from 'rxjs';
import { Organization } from '@core/models/organization.model';

@Injectable({ providedIn: 'root' })
export class OrganizationService {
  private baseUrl = `${environment.apiUrl}/organizations`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Organization[]> {
    return this.http.get<Organization[]>(this.baseUrl);
  }

  getById(id: number): Observable<Organization> {
    return this.http.get<Organization>(`${this.baseUrl}/${id}`);
  }

  create(data: Organization): Observable<Organization> {
    return this.http.post<Organization>(this.baseUrl, data);
  }

  update(id: number, data: Organization): Observable<Organization> {
    return this.http.put<Organization>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
