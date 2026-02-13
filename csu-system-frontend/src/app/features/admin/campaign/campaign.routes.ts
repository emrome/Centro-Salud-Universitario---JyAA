import { Routes } from '@angular/router';
import { CampaignListComponent } from './campaign-list/campaign-list.component';
import { CampaignFormComponent } from './campaign-form/campaign-form.component';

export const adminCampaignsRoutes: Routes = [
  { path: '', component: CampaignListComponent },
  { path: 'new', component: CampaignFormComponent },
  { path: ':id/edit', component: CampaignFormComponent }
];

