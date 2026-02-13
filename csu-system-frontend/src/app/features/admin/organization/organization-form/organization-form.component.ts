import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { OrganizationService } from '@core/services/organization.service';
import { NeighborhoodService } from '@core/services/neighborhood.service';

import { Organization } from '@core/models/organization.model';
import { Neighborhood } from '@core/models/neighborhood.model';

import { AppRoutes } from '@shared/constants/app-routes.constants';
import { MAIN_ACTIVITIES } from '@core/enums/main-activity.enum';

@Component({
  selector: 'app-organization-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './organization-form.component.html'
})
export class OrganizationFormComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  organizationId!: number;

  error: string | null = null;
  neighborhoods: Neighborhood[] = [];
  activities = MAIN_ACTIVITIES;

  constructor(
    private fb: FormBuilder,
    private organizationService: OrganizationService,
    private neighborhoodService: NeighborhoodService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      address: [''],
      mainActivity: [null],
      neighborhoodId: [null, Validators.required]
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEdit = true;
      this.organizationId = +idParam;
      this.loadOrganization();
    }

    this.loadNeighborhoods();
  }

  private loadOrganization(): void {
    this.organizationService.getById(this.organizationId).subscribe({
      next: (data: Organization) => {
        this.form.patchValue({
          name: data.name,
          address: data.address ?? '',
          mainActivity: data.mainActivity ?? null,
          neighborhoodId: data.neighborhoodId ?? null
        });
      },
      error: () => (this.error = 'Error al cargar la organización')
    });
  }

  private loadNeighborhoods(): void {
    this.neighborhoodService.getAll().subscribe({
      next: data => (this.neighborhoods = data),
      error: () => (this.error = 'Error cargando barrios')
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    const dto: Organization = { ...this.form.value };

    const request$ = this.isEdit
      ? this.organizationService.update(this.organizationId, dto)
      : this.organizationService.create(dto);

    request$.subscribe({
      next: () => this.router.navigate([AppRoutes.Admin.Organizations.List]),
      error: (err) => {
        if (err.status === 409) this.error = 'Ya existe una organización con ese nombre';
        else if (err.status === 404) this.error = 'Barrio no encontrado';
        else this.error = 'Error guardando la organización';
        console.error(err);
      }
    });
  }

  cancel(): void {
    this.router.navigate([AppRoutes.Admin.Organizations.List]);
  }

}

