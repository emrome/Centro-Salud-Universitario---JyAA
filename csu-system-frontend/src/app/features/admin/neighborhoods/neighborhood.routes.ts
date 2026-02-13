import { Routes } from '@angular/router';
import { NeighborhoodListComponent } from '@features/admin/neighborhoods/neighborhood-list/neighborhood-list.component';
import { NeighborhoodFormComponent } from '@features/admin/neighborhoods/neighborhood-form/neighborhood-form.component';
import { ZoneListComponent } from '@features/admin/neighborhoods/zones/zone-list/zone-list.component';
import { ZoneFormComponent } from '@features/admin/neighborhoods/zones/zone-form/zone-form.component';

export const neighborhoodRoutes: Routes = [
  { path: '', component: NeighborhoodListComponent },
  { path: 'new', component: NeighborhoodFormComponent },
  { path: ':id/edit', component: NeighborhoodFormComponent },
  {
    path: ':neighId',
    children: [
      { path: 'zones', component: ZoneListComponent },
      { path: 'zones/new', component: ZoneFormComponent },
      { path: 'zones/:zoneId/edit', component: ZoneFormComponent }
    ]
  }
];
