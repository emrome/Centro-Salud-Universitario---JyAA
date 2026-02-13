import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AppRoutes } from '@shared/constants/app-routes.constants';

@Component({
  selector: 'app-health-staff-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './health-staff-dashboard.component.html'
})
export class HealthStaffDashboardComponent {
  constructor(private router: Router) {}

  cards = [
    {
      title: 'Solicitudes de reportes',
      subtitle: 'Ver, completar o rechazar',
      icon: 'fa-tasks',
      link: AppRoutes.HealthStaff.ReportRequests.List
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
