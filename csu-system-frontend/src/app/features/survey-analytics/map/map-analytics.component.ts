import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import * as L from 'leaflet';
import { MapAnalyticsService, ZoneCountDTO } from '@core/services/analytics/map-analytics.service';
import { NeighborhoodService } from '@core/services/neighborhood.service';
import { ZoneService } from '@core/services/zone.service';
import { Neighborhood } from '@core/models/neighborhood.model';
import { Zone } from '@core/models/zone.model';

@Component({
  selector: 'app-map-analytics',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './map-analytics.component.html',
  styleUrls: ['./map-analytics.component.scss']
})
export class MapAnalyticsComponent implements OnInit, OnDestroy {
  neighborhoodId?: number;
  condition = 'DIABETES';
  minAge?: number;

  neighborhoods: Neighborhood[] = [];
  zones: Zone[] = [];

  loading = false;
  error: string | null = null;

  private map?: L.Map;
  private zonesLayer?: L.GeoJSON;

  constructor(
    private api: MapAnalyticsService,
    private neighborhoodService: NeighborhoodService,
    private zoneService: ZoneService
  ) {}

  ngOnInit(): void {
    this.initMap();
    this.loadNeighborhoods();
  }

  ngOnDestroy(): void { this.map?.remove(); }

  onNeighborhoodChange(): void {
    if (!this.neighborhoodId) {
      this.clearLayers(); this.zones = []; return;
    }
    this.zoneService.getByNeighborhood(this.neighborhoodId).subscribe({
      next: zs => { this.zones = zs || []; this.fitToZones(); this.fetch(); },
      error: _ => { this.zones = []; this.fetch(); }
    });
  }

  fetch(): void {
    if (!this.neighborhoodId) return;
    this.error = null; this.loading = true;
    this.drawZones();
  }

  private initMap(): void {
    this.map = L.map('map-analytics', { center: [-34.9, -57.95], zoom: 12, zoomControl: true });
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19, attribution: '© OpenStreetMap'
    }).addTo(this.map);
  }

  private loadNeighborhoods(): void {
    this.neighborhoodService.getAll().subscribe({
      next: ns => {
        this.neighborhoods = ns;
        if (!this.neighborhoodId && this.neighborhoods.length) {
          this.neighborhoodId = this.neighborhoods[0].id!;
          this.onNeighborhoodChange();
        }
      },
      error: _ => this.error = 'Error cargando barrios'
    });
  }

  private drawZones(): void {
    if (!this.map || !this.neighborhoodId) return;

    this.api.zoneCounts({
      neighborhoodId: this.neighborhoodId,
      condition: this.condition,
      minAge: this.minAge
    }).subscribe({
      next: (rows: ZoneCountDTO[]) => {
        if (this.zonesLayer) this.map!.removeLayer(this.zonesLayer);

        const gj = this.toZonesGeoJSON(rows);
        this.zonesLayer = L.geoJSON(gj, {
          style: f => ({
            color: '#2E8B7B',
            weight: 1,
            fillOpacity: (f?.properties?.count ?? 0) ? 0.55 : 0.15,
            fillColor: this.colorFor(f?.properties?.count ?? 0, rows)
          }),
          onEachFeature: (feature, layer) => {
            const n = feature.properties?.zoneName ?? 'Zona';
            const c = feature.properties?.count ?? 0;
            layer.bindTooltip(`<strong>${n}</strong><br/>Casos: ${c}`, { sticky: true });
          }
        }).addTo(this.map!);
      },
      error: _ => this.error = 'No se pudo cargar el conteo por zona',
      complete: () => this.loading = false
    });
  }

  private toZonesGeoJSON(rows: ZoneCountDTO[]): any {
    const countMap = new Map<number, number>(rows.map(r => [r.zoneId, r.count]));
    const nameMap = new Map<number, string>(rows.map(r => [r.zoneId, r.zoneName]));
    const features: any[] = [];

    for (const z of this.zones) {
      const coords = (z.coordinates || []).map(c => [c.lng, c.lat]);
      if (!coords.length) continue;
      if (coords.length && (coords[0][0] !== coords[coords.length - 1][0] || coords[0][1] !== coords[coords.length - 1][1])) {
        coords.push(coords[0]);
      }
      features.push({
        type: 'Feature',
        properties: { zoneId: z.id, zoneName: nameMap.get(z.id!) ?? z.name, count: countMap.get(z.id!) ?? 0 },
        geometry: { type: 'Polygon', coordinates: [coords] }
      });
    }

    return { type: 'FeatureCollection', features };
  }

  private colorFor(value: number, rows: ZoneCountDTO[]): string {
    const max = Math.max(1, ...rows.map(r => r.count));
    const t = value / max;
    const g = Math.floor(180 + 50 * t);
    const s = Math.floor(120 + 60 * t);
    return `rgb(${s - 60}, ${g}, ${s - 20})`;
  }

  private fitToZones(): void {
    if (!this.map || !this.zones?.length) return;
    const latlngs: L.LatLngTuple[] = [];
    this.zones.forEach(z => (z.coordinates || []).forEach(c => latlngs.push([c.lat, c.lng])));
    if (latlngs.length) this.map.fitBounds(L.latLngBounds(latlngs), { padding: [20, 20] });
  }

  private clearLayers(): void {
    if (this.zonesLayer && this.map) { this.map.removeLayer(this.zonesLayer); this.zonesLayer = undefined; }
  }
}
