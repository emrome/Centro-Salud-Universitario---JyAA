import { Routes } from '@angular/router';

export const surveyorRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('@features/admin/people/surveyors/surveyor-list/surveyor-list.component').then(m => m.SurveyorListComponent)
  },
  {
    path: 'new',
    loadComponent: () =>
      import('@features/admin/people/surveyors/surveyor-form/surveyor-form.component').then(m => m.SurveyorFormComponent)
  },
  {
    path: ':id',
    loadComponent: () =>
      import('@features/admin/people/surveyors/surveyor-form/surveyor-form.component').then(m => m.SurveyorFormComponent)
  }
];
