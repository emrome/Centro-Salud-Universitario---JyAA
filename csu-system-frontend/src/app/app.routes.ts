import { Routes } from '@angular/router';
import { authGuard, authMatch } from '@core/guards/auth.guard';
import { roleGuard, roleMatch } from '@core/guards/role.guard';
import { AdminLayoutComponent } from '@layout/admin/admin-layout.component';
import { HealthStaffLayoutComponent } from '@layout/health-staff/health-staff-layout.component';
import { PublicLayoutComponent } from '@layout/public/public-layout.component';
import { ProfileComponent } from '@features/profile/profile.component';

export const routes: Routes = [
  {
    path: '',
    loadChildren: () =>
      import('@features/public/public.routes').then(m => m.publicRoutes)
  },
  {
    path: 'profile',
    component: AdminLayoutComponent,
    canMatch: [authMatch, roleMatch(['Admin'])],
    canActivate: [authGuard, roleGuard(['Admin'])],
    children: [{ path: '', component: ProfileComponent }]
  },
  {
    path: 'profile',
    component: HealthStaffLayoutComponent,
    canMatch: [authMatch, roleMatch(['HealthStaff'])],
    canActivate: [authGuard, roleGuard(['HealthStaff'])],
    children: [{ path: '', component: ProfileComponent }]
  },
  {
    path: 'profile',
    component: PublicLayoutComponent,
    canMatch: [authMatch, roleMatch(['SocialOrgRepresentative'])],
    canActivate: [authGuard, roleMatch(['SocialOrgRepresentative'])],
    children: [{ path: '', component: ProfileComponent }]
  },
  {
    path: 'admin',
    canMatch: [authMatch, roleMatch(['Admin'])],
    canActivate: [authGuard, roleGuard(['Admin'])],
    loadChildren: () =>
      import('@features/admin/admin.routes').then(m => m.adminRoutes)
  },
  {
    path: 'representative',
    canMatch: [authMatch, roleMatch(['SocialOrgRepresentative'])],
    canActivate: [authGuard, roleGuard(['SocialOrgRepresentative'])],
    loadChildren: () =>
      import('@features/representative/representative.routes').then(m => m.representativeRoutes)
  },
  {
    path: 'health-staff',
    canMatch: [authMatch, roleMatch(['HealthStaff'])],
    canActivate: [authGuard, roleGuard(['HealthStaff'])],
    loadChildren: () =>
      import('@features/health-staff/health-staff.routes').then(m => m.healthStaffRoutes)
  },
  {
    path: 'analytics',
    component: AdminLayoutComponent,
    canMatch: [authMatch, roleMatch(['Admin'])],
    canActivate: [authGuard, roleGuard(['Admin'])],
    children: [
      {
        path: '',
        loadChildren: () =>
          import('@features/survey-analytics/analytics.routes')
            .then(m => m.analyticsRoutes)
      }
    ]
  },
  {
    path: 'analytics',
    component: HealthStaffLayoutComponent,
    canMatch: [authMatch, roleMatch(['HealthStaff'])],
    canActivate: [authGuard, roleGuard(['HealthStaff'])],
    children: [
      {
        path: '',
        loadChildren: () =>
          import('@features/survey-analytics/analytics.routes')
            .then(m => m.analyticsRoutes)
      }
    ]
  },
  { path: '**', redirectTo: '/' },
];
