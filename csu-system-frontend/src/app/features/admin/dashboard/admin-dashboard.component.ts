import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AppRoutes } from '@shared/constants/app-routes.constants';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
})
export class AdminDashboardComponent {
  constructor(private router: Router) {}
  cards = [
    {
      title: 'Administradores',
      subtitle: 'Gestión de administradores',
      icon: 'fa-bullhorn',
      link: AppRoutes.Admin.Admins.List
    },
    {
      title: 'Personal de Salud',
      subtitle: 'Gestión de personal de salud',
      icon: 'fa-bullhorn',
      link: AppRoutes.Admin.HealthStaff.List
    },
    {
      title: 'Representantes de Organizaciones',
      subtitle: 'Gestión de representantes',
      icon: 'fa-bullhorn',
      link: AppRoutes.Admin.Representatives.List
    },
    {
      title: 'Campañas',
      subtitle: 'ABM y jornadas',
      icon: 'fa-bullhorn',
      link: '/admin/campaigns'
    },
    {
      title: 'Barrios',
      subtitle: 'Gestión y zonas',
      icon: 'fa-map-marked-alt',
      link: AppRoutes.Admin.Neighborhoods.List
    },
    {
      title: 'Encuestadores',
      subtitle: 'Altas y administración',
      icon: 'fa-user-edit',
      link: AppRoutes.Admin.Surveyors.List
    },
    {
      title: 'Org. Sociales',
      subtitle: 'ABM de organizaciones',
      icon: 'fa-hand-holding-heart',
      link: '/admin/organizations'
    },
    {
      title: 'Habilitación de usuarios',
      subtitle: 'Aprobar y habilitar cuentas',
      icon: 'fa-user-check',
      link: AppRoutes.Admin.UserEnablement.List
    },
    {
      title: 'Análisis de datos demograficos',
      subtitle: 'Composición del barrio',
      icon: 'fa-chart-bar',
      link: AppRoutes.Analytics.Summary
    },
    {
      title: 'Mapa sanitario',
      subtitle: 'Heatmap y conteo por zonas',
      icon: 'fa-map',
      link: AppRoutes.Analytics.Map
    }
  ];

  goTo(route: string) {
    this.router.navigate([route]);
  }

}
