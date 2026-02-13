import { Routes } from '@angular/router';
import { OrganizationListComponent } from './organization-list/organization-list.component';
import { OrganizationFormComponent } from './organization-form/organization-form.component';

export const organizationRoutes: Routes = [
  { path: '', component: OrganizationListComponent },
  { path: 'new', component: OrganizationFormComponent },
  { path: ':id/edit', component: OrganizationFormComponent }
];
