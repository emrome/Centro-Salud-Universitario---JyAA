import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AppRoutes } from '@shared/constants/app-routes.constants';

@Component({
  selector: 'app-representative-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './representative-dashboard.component.html'
})
export class RepresentativeDashboardComponent {
  constructor(private router: Router) {}

  cards = [
    {
      title: 'Solicitudes de reporte',
      subtitle: 'Crear y seguir mis solicitudes',
      icon: 'fa-file-alt',
      link: AppRoutes.Representative.ReportRequests.List
    }
  ];

  goTo(route: string) {
    this.router.navigate([route]);
  }
}
