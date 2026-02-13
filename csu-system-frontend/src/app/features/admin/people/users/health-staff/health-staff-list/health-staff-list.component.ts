import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HealthStaffService } from '@core/services/users/health-staff.service';
import { HealthStaff } from '@core/models/users/health-staff.model';
import { AppRoutes } from '@shared/constants/app-routes.constants';

@Component({
  selector: 'app-health-staff-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './health-staff-list.component.html'
})
export class HealthStaffListComponent implements OnInit {
  healthStaff: HealthStaff[] = [];
  loading = true;
  error: string | null = null;
  paths = AppRoutes;

  specialtyLabel: { [key: string]: string } = {
    CLINIC: 'Clínica',
    PSYCHOLOGY: 'Psicología',
    NURSING: 'Enfermería',
    SOCIAL_WORK: 'Trabajo social',
    NUTRITION: 'Nutrición'
  };

  constructor(private healthStaffService: HealthStaffService, private router: Router) {}

  ngOnInit(): void {
    this.fetchHealthStaff();
  }

  fetchHealthStaff(): void {
    this.loading = true;
    this.healthStaffService.getAll().subscribe({
      next: (data) => {
        this.healthStaff = data.filter(staff => staff.enabled);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error cargando personal de salud';
        this.loading = false;
        console.error(err);
      }
    });
  }

  goToNew(): void {
    this.router.navigate([this.paths.Admin.HealthStaff.New]);
  }

  goToEdit(id: number): void {
    this.router.navigate([this.paths.Admin.HealthStaff.Edit(id)]);
  }

  deleteUser(id: number): void {
    if (!confirm('¿Seguro que querés eliminar este usuario?')) return;

    this.healthStaffService.delete(id).subscribe({
      next: () => this.fetchHealthStaff(),
      error: (err) => {
        this.error = 'Error eliminando el usuario';
        console.error(err);
      }
    });
  }
}
