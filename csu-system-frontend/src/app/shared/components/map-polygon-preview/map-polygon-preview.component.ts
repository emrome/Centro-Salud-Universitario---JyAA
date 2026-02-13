import { AfterViewInit, Component, ElementRef, Input, OnChanges, OnDestroy, SimpleChanges, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';

type Coord = { lat: number; lng: number };

@Component({
  selector: 'app-map-polygon-preview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './map-polygon-preview.component.html',
  styleUrls: ['./map-polygon-preview.component.scss']
})
export class MapPolygonPreviewComponent implements AfterViewInit, OnChanges, OnDestroy {
  @Input({ required: true }) coordinates: Coord[] = [];
  @Input() height = '160px';
  @Input() width = '100%';

  @ViewChild('mapEl', { static: true }) mapEl!: ElementRef<HTMLDivElement>;

  private map: L.Map | null = null;
  private polygon: L.Polygon | null = null;
  private io?: IntersectionObserver;
  private shouldInit = true;

  ngAfterViewInit(): void {
    // Lazy-init: solo creamos el mapa cuando el card entra en viewport
    this.io = new IntersectionObserver((entries) => {
      const visible = entries.some(e => e.isIntersecting);
      if (visible && this.shouldInit) {
        this.initMap();
        this.shouldInit = false;
      }
    }, { rootMargin: '200px' });
    this.io.observe(this.mapEl.nativeElement);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['coordinates']) return;
    if (!this.map && this.coordinates && this.coordinates.length >= 3 && this.mapEl?.nativeElement) {
      this.initMap();
      this.shouldInit = false;
      return;
    }
    if (this.map && this.polygon) {
      this.updatePolygon();
    }
  }

  ngOnDestroy(): void {
    this.io?.disconnect();
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  private initMap(): void {
    if (!this.coordinates || this.coordinates.length < 3) return;

    this.map = L.map(this.mapEl.nativeElement, {
      zoomControl: false,
      attributionControl: false,
      dragging: false,
      doubleClickZoom: false,
      scrollWheelZoom: false,
      boxZoom: false,
      keyboard: false,
      touchZoom: false,
      preferCanvas: true,
    } as L.MapOptions);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      minZoom: 3,
      detectRetina: false,
      noWrap: true
    }).addTo(this.map);

    this.polygon = L.polygon(this.coordinates, { weight: 2 });
    this.polygon.addTo(this.map);

    this.fitBounds();
    setTimeout(() => this.map?.invalidateSize(), 0); // por si el contenedor cambia tamaño
  }

  private updatePolygon(): void {
    if (!this.map) return;
    if (!this.polygon) {
      this.polygon = L.polygon(this.coordinates, { weight: 2 }).addTo(this.map);
    } else {
      this.polygon.setLatLngs(this.coordinates as any);
    }
    this.fitBounds();
  }

  private fitBounds(): void {
    if (!this.map || !this.polygon) return;
    const b = this.polygon.getBounds();
    this.map.fitBounds(b, { padding: [10, 10], maxZoom: 16 });
  }
}
