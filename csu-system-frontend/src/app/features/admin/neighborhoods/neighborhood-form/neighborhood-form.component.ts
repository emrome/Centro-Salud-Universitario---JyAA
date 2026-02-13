import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Neighborhood } from '@core/models/neighborhood.model';
import { Zone } from '@core/models/zone.model';
import { NeighborhoodService } from '@core/services/neighborhood.service';
import { CommonModule } from '@angular/common';
import { ZoneListComponent } from '@features/admin/neighborhoods/zones/zone-list/zone-list.component';
import { ZoneService } from '@core/services/zone.service';
import { CustomValidators } from '@shared/validators/custom-validators';
import { AppRoutes } from '@shared/constants/app-routes.constants';
import { EntityBasicFieldsComponent } from '@shared/components/entity-basic-fields/entity-basic-fields.component';
import { MapPolygonPickerComponent } from '@shared/components/map-polygon-picker/map-polygon-picker.component';

@Component({
  selector: 'app-neighborhood-form',
  standalone: true,
  templateUrl: './neighborhood-form.component.html',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, ZoneListComponent, EntityBasicFieldsComponent, MapPolygonPickerComponent]
})
export class NeighborhoodFormComponent implements OnInit {

  zones: Zone[] = [];
  form!: FormGroup;
  isEdit = false;
  neighborhoodId!: number;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private neighborhoodService: NeighborhoodService,
    private zoneService: ZoneService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      geolocation: this.fb.array([], CustomValidators.minLengthArray(3))
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEdit = true;
      this.neighborhoodId = +idParam;
      this.loadNeighborhood();

      this.zoneService.getByNeighborhood(this.neighborhoodId).subscribe({
        next: zones => {
          this.zones = zones;
          if (history.state?.newZone) {
            history.replaceState({}, '');
          }
        },
        error: () => {
          this.error = 'Error cargando zonas del barrio';
        }
      });

    }
  }

  get geolocation(): FormArray {
    return this.form.get('geolocation') as FormArray;
  }

  loadNeighborhood(): void {
    this.neighborhoodService.getById(this.neighborhoodId).subscribe({
      next: (data: Neighborhood) => {
        this.form.patchValue({
          name: data.name,
          description: data.description
        });

        while (this.geolocation.length !== 0) {
          this.geolocation.removeAt(0);
        }

        data.geolocation.forEach(coord => {
          const coordGroup = this.fb.group({
            id: [coord.id],
            lat: [coord.lat, [Validators.required, Validators.pattern(/^[-+]?\d+(\.\d+)?$/)]],
            lng: [coord.lng, [Validators.required, Validators.pattern(/^[-+]?\d+(\.\d+)?$/)]]
          });
          this.geolocation.push(coordGroup);
        });
      },
      error: () => {
        this.error = 'No se pudo cargar el barrio.';
      }
    });
  }

  onSubmit(): void {
    const dto: Neighborhood = { ...this.form.value };

    const request$ = this.isEdit
      ? this.neighborhoodService.update(this.neighborhoodId, dto)
      : this.neighborhoodService.create(dto);

    request$.subscribe({
      next: () => {
        this.router.navigate([AppRoutes.Admin.Neighborhoods.List]);
      },
      error: (err) => {
        if (err.status === 409) {
          this.error = 'El barrio con ese nombre ya existe';
        } else {
          this.error = 'Error guardando el barrio';
        }
        console.error(err);
      }
    });
  }

  cancel() {
    this.router.navigate([AppRoutes.Admin.Neighborhoods.List]);
  }

  goToCreateZone(): void {
    this.router.navigate(
      [AppRoutes.Admin.Neighborhoods.Zones.New(this.neighborhoodId)],
      {
        state: {
          neighborhoodName: this.form.get('name')?.value
        }
      }
    );
  }

  deleteZone(data: { zoneId: number, neighborhoodId: number }): void {
    this.zoneService.delete(data.neighborhoodId, data.zoneId).subscribe({
      next: () => {
        this.zones = this.zones.filter(zone => zone.id !== data.zoneId);
      },
      error: () => {
        this.error = 'Error eliminando la zona';
      }
    });
  }
}
