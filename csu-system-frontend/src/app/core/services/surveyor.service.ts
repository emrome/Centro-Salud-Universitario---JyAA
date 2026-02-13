import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Surveyor } from '@core/models/surveyor.model';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';

@Injectable({
  providedIn: 'root'
})
export class SurveyorService {
  private apiUrl = `${environment.apiUrl}/surveyors`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Surveyor[]> {
    return this.http.get<Surveyor[]>(this.apiUrl);
  }

  getById(id: number): Observable<Surveyor> {
    return this.http.get<Surveyor>(`${this.apiUrl}/${id}`);
  }

  create(surveyor: Surveyor): Observable<Surveyor> {
    return this.http.post<Surveyor>(this.apiUrl, surveyor);
  }

  update(id: number, surveyor: Surveyor): Observable<Surveyor> {
    return this.http.put<Surveyor>(`${this.apiUrl}/${id}`, surveyor);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
