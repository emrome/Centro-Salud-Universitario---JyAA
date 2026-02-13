import { Routes } from '@angular/router';
import { HealthStaffFormComponent } from './health-staff-form/health-staff-form.component';
import { HealthStaffListComponent } from './health-staff-list/health-staff-list.component';

export const healthStaffRoutes: Routes = [
  { path: 'health-staff', component: HealthStaffListComponent },
  { path: 'health-staff/new', component: HealthStaffFormComponent },
  { path: 'health-staff/:id/edit', component: HealthStaffFormComponent }
];
