import { Component, OnInit } from '@angular/core';
import { NeighborhoodService } from '@core/services/neighborhood.service';
import { ZoneService } from '@core/services/zone.service';
import { Neighborhood } from '@core/models/neighborhood.model';
import { Zone } from '@core/models/zone.model';
import {Router, RouterModule} from '@angular/router';
import {CommonModule} from "@angular/common";
import {ZoneListComponent} from '@features/admin/neighborhoods/zones/zone-list/zone-list.component';
import { AppRoutes } from '@shared/constants/app-routes.constants';
import { MapPolygonPreviewComponent } from '@shared/components/map-polygon-preview/map-polygon-preview.component';


@Component({
  selector: 'app-neighborhood-list',
  standalone: true,
  imports: [CommonModule, RouterModule, ZoneListComponent, MapPolygonPreviewComponent],
  templateUrl: './neighborhood-list.component.html'
})
export class NeighborhoodListComponent implements OnInit {
  neighborhoods: Neighborhood[] = [];
  loading = true;
  error: string | null = null;
  zones: { [key: number]: Zone[] } = {};
  loadingZones: { [key: number]: boolean } = {};
  expandedNeighborhoodId: number | null = null;

  constructor(
    private neighborhoodService: NeighborhoodService,
    private zoneService: ZoneService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchNeighborhoods();
  }

  fetchNeighborhoods(): void {
    this.loading = true;
    this.neighborhoodService.getAll().subscribe({
      next: (data) => {
        this.neighborhoods = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar los barrios';
        this.loading = false;
        console.error(err);
      }
    });
  }

  goToNew(): void {
    this.router.navigate([AppRoutes.Admin.Neighborhoods.New]);
  }

  goToEdit(id: number): void {
    const zones = this.zones[id] ?? [];
    this.router.navigate([AppRoutes.Admin.Neighborhoods.Edit(id)], {
      state: { zones: zones }
    });
  }

  deleteNeighborhood(id: number): void {
    if (!confirm('¿Está seguro que quiere eliminar el barrio?')) return;

    this.neighborhoodService.delete(id).subscribe({
      next: () => this.fetchNeighborhoods(),
      error: (err) => {
        this.error = 'Error eliminando el barrio';
        console.error(err);
      }
    });
  }

  toggleZones(neighborhoodId: number): void {
    if (this.expandedNeighborhoodId === neighborhoodId) {
      this.expandedNeighborhoodId = null;
      return;
    }

    this.expandedNeighborhoodId = neighborhoodId;
    if (!this.zones[neighborhoodId]) {
      this.loadingZones[neighborhoodId] = true;
      this.zoneService.getByNeighborhood(neighborhoodId).subscribe({
        next: data => {
          const neighborhood = this.neighborhoods.find(n => n.id === neighborhoodId);
          const neighborhoodName = neighborhood?.name ?? '(Desconocido)';

          this.zones[neighborhoodId] = data.map(z => ({
            ...z,
            _neighborhoodName: neighborhoodName
          }));

          this.loadingZones[neighborhoodId] = false;
        },
        error: err => {
          this.loadingZones[neighborhoodId] = false;
          this.error = 'Error al cargar las zonas';
        }
      });
    }
  }

  deleteZone(data: {neighborhoodId: number, zoneId: number}) {
    this.zoneService.delete(data.neighborhoodId, data.zoneId).subscribe({
      next: () => {
        const index = this.zones[data.neighborhoodId].findIndex(z => z.id === data.zoneId);
        if (index > -1) {
          this.zones[data.neighborhoodId].splice(index, 1);
        }
      },
      error: (err) => {
        this.error = 'Error eliminando la zona';
        console.error(err);
      }
    });
  }
}
