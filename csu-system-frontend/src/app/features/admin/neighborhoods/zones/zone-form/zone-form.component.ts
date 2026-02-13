import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormGroup, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { ZoneService } from '@core/services/zone.service';
import { NeighborhoodService } from '@core/services/neighborhood.service';
import { Zone } from '@core/models/zone.model';
import { CustomValidators } from '@shared/validators/custom-validators';
import { AppRoutes } from '@shared/constants/app-routes.constants';
import { EntityBasicFieldsComponent } from '@shared/components/entity-basic-fields/entity-basic-fields.component';
import { MapPolygonPickerComponent } from '@shared/components/map-polygon-picker/map-polygon-picker.component';

@Component({
  selector: 'app-zone-form',
  standalone: true,
  templateUrl: './zone-form.component.html',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, MapPolygonPickerComponent, EntityBasicFieldsComponent ]
})
export class ZoneFormComponent implements OnInit {
  @Input() neighborhoodId!: number;
  @Output() zoneCreated = new EventEmitter<Zone>();
  form!: FormGroup;
  isEdit = false;
  zoneId!: number;
  loading = false;
  error: string | null = null;
  neighborhoodName: string = '(Desconocido)';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private zoneService: ZoneService,
    private neighborhoodService: NeighborhoodService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      geolocation: this.fb.array([], CustomValidators.minLengthArray(3))
    });

    const state = history.state;
    const neighId = this.route.snapshot.paramMap.get('neighId');
    if (neighId) {
      this.neighborhoodId = +neighId;

      if (state?.neighborhoodName) {
        this.neighborhoodName = state.neighborhoodName;
      } else {
        this.neighborhoodService.getById(this.neighborhoodId).subscribe({
          next: (neigh:any) => this.neighborhoodName = neigh.name,
          error: () => this.neighborhoodName = '(Desconocido)'
        });
      }
    }

    const id = this.route.snapshot.paramMap.get('zoneId');
    if (id) {
      this.isEdit = true;
      this.zoneId = +id;
      this.loadZone();
    }
  }

  get geolocation(): FormArray {
    return this.form.get('geolocation') as FormArray;
  }

  loadZone() {
    this.loading = true;
    this.zoneService.getById(this.neighborhoodId, this.zoneId).subscribe({
      next: (zone) => {
        this.form.patchValue({
          name: zone.name,
          description: zone.description
        });
        this.neighborhoodId = zone.neighborhoodId!;
        this.loading = false;
        this.geolocation.clear();
        zone.coordinates.forEach((coord) => {
          this.geolocation.push(this.fb.group({
            id: [coord.id],
            lat: [coord.lat, Validators.required],
            lng: [coord.lng, Validators.required]
          }));
        });
      },
      error: () => {
        this.error = 'No se pudo cargar la zona.';
        this.loading = false;
      }
    });
  }

  submit():void {
    if (this.form.invalid) return;

    const data = { ...this.form.value };
    const dto: Zone = {id: this.isEdit ? this.zoneId : undefined,
      name: data.name,
      description: data.description,
      neighborhoodId: this.neighborhoodId,
      coordinates: data.geolocation
        .filter((coord: any) => coord.lat != null && coord.lng != null)
        .map((coord: any) => ({
          id: coord.id ?? null,
          lat: +coord.lat,
          lng: +coord.lng
        }))
    };

    const request$ = this.isEdit
      ? this.zoneService.update(this.neighborhoodId, this.zoneId, dto)
      : this.zoneService.create(this.neighborhoodId, dto);

    request$.subscribe({
      next: () => {
        this.router.navigate([AppRoutes.Admin.Neighborhoods.Edit(this.neighborhoodId)], {
          state: { newZone: dto }
        });
      },
      error: (err) => {
        if (err.status === 409){
          this.error = 'Ya existe una zona con ese nombre en este barrio.';
        }
        else {
          this.error = 'Error guardando la zona.';
        }
      }
    });
  }

  cancel() {
    this.router.navigate([AppRoutes.Admin.Neighborhoods.Edit(this.neighborhoodId)]);
  }
}
