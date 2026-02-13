import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { SocialOrgRepresentative } from '@core/models/users/social-org-representative.model';
import { SocialOrgRepresentativeService } from '@core/services/users/social-org-representative.service';
import { AppRoutes } from '@shared/constants/app-routes.constants';


@Component({
  selector: 'app-social-org-representative-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './social-org-representative-list.component.html'
})
export class SocialOrgRepresentativeListComponent implements OnInit {
  representatives: SocialOrgRepresentative[] = [];
  loading = true;
  error: string | null = null;
  paths = AppRoutes;

  constructor(
    private representativeService: SocialOrgRepresentativeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchRepresentatives();
  }

  fetchRepresentatives(): void {
    this.loading = true;
    this.representativeService.getAll().subscribe({
      next: (data) => {
        this.representatives = data.filter(rep => rep.enabled);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error cargando representantes';
        this.loading = false;
        console.error(err);
      }
    });
  }

  goToNew(): void {
    this.router.navigate([AppRoutes.Admin.Representatives.New]);
  }

  goToEdit(id: number): void {
    this.router.navigate([AppRoutes.Admin.Representatives.Edit(id)]);
  }

  deleteRepresentative(id: number): void {
    if (!confirm('¿Seguro que querés eliminar este representante?')) return;

    this.representativeService.delete(id).subscribe({
      next: () => this.fetchRepresentatives(),
      error: (err) => {
        this.error = 'Error eliminando el representante';
        console.error(err);
      }
    });
  }
}
