import { Routes } from '@angular/router';
import { AdminFormComponent } from './admin-form/admin-form.component';
import { AdminListComponent } from './admin-list/admin-list.component';


export const adminRoutes: Routes = [
  { path: 'admins', component: AdminListComponent },
  { path: 'admins/new', component: AdminFormComponent },
  { path: 'admins/:id/edit', component: AdminFormComponent }
];
