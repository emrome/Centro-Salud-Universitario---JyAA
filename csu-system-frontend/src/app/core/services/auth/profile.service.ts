import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';

interface MeResponse<T> { type: 'ADMIN'|'HEALTHSTAFF'|'REPRESENTATIVE'; data: T; }

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private base = `${environment.apiUrl}/me`; //

  constructor(private http: HttpClient) {}

  getMe<T>(): Observable<MeResponse<T>> {
    return this.http.get<MeResponse<T>>(this.base);
  }

  updateMe<T>(payload: T): Observable<T> {
    return this.http.put<T>(this.base, payload);
  }

  changePassword(body: { oldPassword: string; newPassword: string; confirmNewPassword: string; }) {
    return this.http.put(`${this.base}/password`, body);
  }
}

