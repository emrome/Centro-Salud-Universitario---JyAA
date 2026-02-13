import { Routes } from '@angular/router';
import { PublicLayoutComponent } from '@layout/public/public-layout.component';
import { HomeComponent } from './home/home.component';

export const publicRoutes: Routes = [
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      {
        path: '',
        component: HomeComponent
      },
      {
        path: 'login',
        loadComponent: () =>
          import('@features/auth/login/login.component').then(m => m.LoginComponent)
      },
      {
        path: 'register',
        loadComponent: () =>
          import('@features/auth/register/register.component').then(m => m.RegisterComponent)
      }
    ]
  }
];
