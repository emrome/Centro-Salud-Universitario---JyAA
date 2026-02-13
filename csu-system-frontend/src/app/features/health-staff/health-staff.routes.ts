import { Routes } from '@angular/router';
import { HealthStaffLayoutComponent } from '@layout/health-staff/health-staff-layout.component';
import { HealthStaffDashboardComponent } from '@features/health-staff/dashboard/health-staff-dashboard.component';

export const healthStaffRoutes: Routes = [
  {
    path: '',
    component: HealthStaffLayoutComponent,
    children: [
      { path: '', component: HealthStaffDashboardComponent }, // dashboard
      {
        path: 'report-requests',
        loadChildren: () =>
          import('./report-request/report-request.routes')
            .then(m => m.healthStaffReportRequestRoutes)
      }
    ]
  }
];
