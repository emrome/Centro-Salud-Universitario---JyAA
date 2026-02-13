import { Routes } from '@angular/router';
import { AdminLayoutComponent } from '@layout/admin/admin-layout.component';
import { AdminDashboardComponent } from '@features/admin/dashboard/admin-dashboard.component';
import {UserEnableListComponent} from '@features/admin/people/users/user/enablement/user-enable-list.component';

export const adminRoutes: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      {
        path: '',
        component: AdminDashboardComponent
      },
      {
        path: 'neighborhoods',
        loadChildren: () =>
          import('@features/admin/neighborhoods/neighborhood.routes')
            .then(m => m.neighborhoodRoutes)
      },
      {
        path: 'users',
        loadChildren: () =>
          import('@features/admin/people/users/admin-users.routes')
            .then(m => m.adminUsersRoutes)
      },
      {
        path: 'surveyors',
        loadChildren: () =>
          import('@features/admin/people/surveyors/surveyors.routes')
            .then(m => m.surveyorRoutes)
      },
      {
        path: 'campaigns',
        loadChildren: () =>
          import('@features/admin/campaign/campaign.routes')
            .then(m => m.adminCampaignsRoutes)
      },
      {
        path: 'organizations',
        loadChildren: () =>
          import('@features/admin/organization/organization.routes')
            .then(m => m.organizationRoutes)
      },
      { path: 'users/enablement', component: UserEnableListComponent },
    ]
  }
];

