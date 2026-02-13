import { Routes } from '@angular/router';

import { AdminListComponent } from './admin/admin-list/admin-list.component';
import { AdminFormComponent } from './admin/admin-form/admin-form.component';

import { HealthStaffListComponent } from './health-staff/health-staff-list/health-staff-list.component';
import { HealthStaffFormComponent } from './health-staff/health-staff-form/health-staff-form.component';

import { SocialOrgRepresentativeListComponent } from './social-org-representative/social-org-representative-list/social-org-representative-list.component';
import { SocialOrgRepresentativeFormComponent } from './social-org-representative/social-org-representative-form/social-org-representative-form.component';

export const adminUsersRoutes: Routes = [
  { path: '', redirectTo: 'admins', pathMatch: 'full' },
  { path: 'admins', component: AdminListComponent },
  { path: 'admins/new', component: AdminFormComponent },
  { path: 'admins/:id/edit', component: AdminFormComponent },

  { path: 'health-staff', component: HealthStaffListComponent },
  { path: 'health-staff/new', component: HealthStaffFormComponent },
  { path: 'health-staff/:id/edit', component: HealthStaffFormComponent },

  { path: 'representatives', component: SocialOrgRepresentativeListComponent },
  { path: 'representatives/new', component: SocialOrgRepresentativeFormComponent },
  { path: 'representatives/:id/edit', component: SocialOrgRepresentativeFormComponent }
];

