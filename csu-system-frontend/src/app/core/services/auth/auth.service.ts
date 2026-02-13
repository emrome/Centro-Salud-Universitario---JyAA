import { Injectable } from '@angular/core';
import { BehaviorSubject, tap, map } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '@env/environment';
import { TokenService } from '../token.service';
import { AppRoutes } from '@shared/constants/app-routes.constants';
import { LoginResponse } from '@core/models/login-response.model';
import { UserProfile } from '@core/models/users/user-profile.model';
import { HttpClient } from '@angular/common/http';
import { ProfileService } from '@core/services/auth/profile.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject = new BehaviorSubject<UserProfile | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
    private profile: ProfileService
  ) {}

  login(credentials: { email: string; password: string }) {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/login`, credentials).pipe(
      tap(res => TokenService.setToken(res.token)),
      tap(() => this.loadCurrentUser().subscribe())
    );
  }

  loadCurrentUser() {
    return this.profile
      .getMe<{ id: number; firstName: string; lastName: string }>()
      .pipe(
        map(res => res.data),                        // {type, data} -> data
        tap(user => this.currentUserSubject.next(user))
      );
  }

  refreshCurrentUser() { this.loadCurrentUser().subscribe(); }

  logout(): void {
    TokenService.clear();
    this.currentUserSubject.next(null);
    this.router.navigate([AppRoutes.Public.Root]);
  }

  isAuthenticated(): boolean { return TokenService.isAuthenticated(); }
  getUserRole(): string | null { return TokenService.getRole(); }
  getUserId(): string | null { return TokenService.getUserId(); }
  isEnabled(): boolean { return TokenService.isEnabled(); }
}

