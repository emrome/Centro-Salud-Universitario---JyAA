import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Observable, take } from 'rxjs';
import { AuthService } from '@core/services/auth/auth.service';
import { ProfileService } from '@core/services/auth/profile.service';
import { AppRoutes } from '@shared/constants/app-routes.constants';
import { UserProfile } from '@core/models/users/user-profile.model';

type Role = 'ADMIN' | 'HEALTHSTAFF' | 'REPRESENTATIVE';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  user$!: Observable<UserProfile | null>;

  constructor(
    public auth: AuthService,
    private profile: ProfileService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.user$ = this.auth.currentUser$;
    if (this.isAuthenticated) {
      this.auth.loadCurrentUser().subscribe();
    }
  }

  goToDashboard(): void {
    const tokenRole = this.normalizeRole(this.auth.getUserRole());
    if (tokenRole) {
      this.navigateToDashboard(tokenRole);
      return;
    }
    this.profile.getMe<unknown>().pipe(take(1)).subscribe({
      next: res => {
        const role = this.normalizeRole((res as any)?.type);
        this.navigateToDashboard(role);
      },
      error: () => this.router.navigate([AppRoutes.Public.Root])
    });
  }

  private navigateToDashboard(role?: Role | null): void {
    const target = this.getDashboardRouteByRole(role || undefined);
    this.router.navigate([target ?? AppRoutes.Public.Root]);
  }

  private getDashboardRouteByRole(role?: Role): string | null {
    switch (role) {
      case 'ADMIN': return AppRoutes.Admin.Dashboard;
      case 'HEALTHSTAFF': return AppRoutes.HealthStaff.Dashboard;
      case 'REPRESENTATIVE': return AppRoutes.Representative.Dashboard;
      default: return null;
    }
  }

  private normalizeRole(raw: string | null | undefined): Role | null {
    if (!raw) return null;
    if (raw === 'Admin') return 'ADMIN';
    if (raw === 'HealthStaff') return 'HEALTHSTAFF';
    if (raw === 'SocialOrgRepresentative') return 'REPRESENTATIVE';
    const r = String(raw).toUpperCase();
    if (r === 'ADMIN') return 'ADMIN';
    if (r === 'HEALTHSTAFF' || r === 'HEALTH') return 'HEALTHSTAFF';
    if (r === 'REPRESENTATIVE' || r === 'REP') return 'REPRESENTATIVE';
    return null;
  }

  get isAuthenticated(): boolean {
    return this.auth.isAuthenticated();
  }

  logout(): void { this.auth.logout(); }
  login(): void { this.router.navigate([AppRoutes.Public.Login]); }
  register(): void { this.router.navigate([AppRoutes.Public.Register]); }
  openProfile(): void { this.router.navigate(['/profile']); }

  get isPublicRoute(): boolean {
    const publicRoutes = [AppRoutes.Public.Login, AppRoutes.Public.Register];
    return publicRoutes.includes(this.router.url);
  }
}
