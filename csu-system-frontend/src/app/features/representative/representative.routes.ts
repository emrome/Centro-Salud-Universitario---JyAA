import { Routes } from '@angular/router';
import { RepresentativeLayoutComponent } from '@layout/representative/representative-layout.component';
import { RepresentativeDashboardComponent } from '@features/representative/dashboard/representative-dashboard.component';

export const representativeRoutes: Routes = [
  {
    path: '',
    component: RepresentativeLayoutComponent,
    children: [
      { path: '', component: RepresentativeDashboardComponent }, // dashboard
      {
        path: 'report-requests',
        loadChildren: () =>
          import('./report-request/report-request.routes')
            .then(m => m.representativeReportRequestRoutes)
      }
    ]
  }
];
