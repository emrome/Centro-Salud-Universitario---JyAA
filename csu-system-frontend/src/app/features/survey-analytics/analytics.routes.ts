import { Routes } from '@angular/router';
import { DemographicsApexComponent } from './demographics-apex/demographics-apex.component';
import { MapAnalyticsComponent } from './map/map-analytics.component';

export const analyticsRoutes: Routes = [
  { path: 'summary', component: DemographicsApexComponent },
  { path: 'map', component: MapAnalyticsComponent },
  { path: '', pathMatch: 'full', redirectTo: 'summary' }
];
