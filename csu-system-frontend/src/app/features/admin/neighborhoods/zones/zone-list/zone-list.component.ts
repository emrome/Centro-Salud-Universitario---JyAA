import {Component, Input, Output, EventEmitter} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Zone } from '@core/models/zone.model';
import { AppRoutes } from '@shared/constants/app-routes.constants';
import { MapPolygonPreviewComponent} from '@shared/components/map-polygon-preview/map-polygon-preview.component';

@Component({
  selector: 'app-zone-list',
  standalone: true,
  imports: [CommonModule, RouterModule, MapPolygonPreviewComponent],
  styleUrls: ['./zone-list.component.scss'],
  templateUrl: './zone-list.component.html'
})
export class ZoneListComponent {
  @Input() zones: Zone[] = [];
  @Output() onDelete = new EventEmitter<{ zoneId: number, neighborhoodId: number }>();
  @Input() neighborhoodName: string = '(Desconocido)';
  error: string | null = null;

  constructor(private router: Router) {}


  editZone(id: number): void {
    const zone = this.zones.find(z => z.id === id);
    if (!zone) {
      this.error = 'No se encontró la zona para editar.';
      return;
    }
    this.router.navigate([AppRoutes.Admin.Neighborhoods.Zones.Edit(zone.neighborhoodId!, id)],
          { state: { neighborhoodName: this.neighborhoodName }
        });
  }

  deleteZone(id: number): void {
    const zone = this.zones.find(z => z.id === id);
    if (!zone) {
      this.error = 'Zona no encontrada para borrar.';
      return;
    }
    if (confirm('¿Está seguro que quiere eliminar la zona?')) {
      this.onDelete.emit({ zoneId: id, neighborhoodId: zone.neighborhoodId! });
    }
  }
}
