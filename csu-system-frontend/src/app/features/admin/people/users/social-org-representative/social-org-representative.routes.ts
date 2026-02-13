import { Routes } from '@angular/router';
import { SocialOrgRepresentativeFormComponent } from './social-org-representative-form/social-org-representative-form.component';
import { SocialOrgRepresentativeListComponent } from './social-org-representative-list/social-org-representative-list.component';

export const representativeRoutes: Routes = [
  { path: 'representatives', component: SocialOrgRepresentativeListComponent },
  { path: 'representatives/new', component: SocialOrgRepresentativeFormComponent },
  { path: 'representatives/:id/edit', component: SocialOrgRepresentativeFormComponent }
];
