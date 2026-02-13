import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HealthStaff } from '@core/models/users/health-staff.model';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';

@Injectable({ providedIn: 'root' })
export class HealthStaffService {
  private apiUrl = `${environment.apiUrl}/healthstaff`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<HealthStaff[]> {
    return this.http.get<HealthStaff[]>(this.apiUrl);
  }

  getById(id: number): Observable<HealthStaff> {
    return this.http.get<HealthStaff>(`${this.apiUrl}/${id}`);
  }

  create(user: HealthStaff): Observable<HealthStaff> {
    return this.http.post<HealthStaff>(this.apiUrl, user);
  }

  update(id: number, user: HealthStaff): Observable<HealthStaff> {
    return this.http.put<HealthStaff>(`${this.apiUrl}/${id}`, user);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
