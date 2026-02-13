import { AfterViewInit, Component, ElementRef, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import * as L from 'leaflet';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-map-polygon-picker',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './map-polygon-picker.component.html',
  styleUrls: ['./map-polygon-picker.component.scss']
})
export class MapPolygonPickerComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input({ required: true }) formArray!: FormArray;
  @Input() height = '420px';
  @Input() minVertices = 3;
  @Input() initialCenter: L.LatLngExpression = [-34.9205, -57.9536];
  @Input() initialZoom = 14;

  @ViewChild('mapEl', { static: true }) mapEl!: ElementRef<HTMLDivElement>;

  private map!: L.Map;
  private drawnItems = new L.FeatureGroup();
  private drawControl!: L.Control.Draw;
  private polygonLayer: L.Polygon | null = null;
  private sub?: Subscription;

  constructor(private fb: FormBuilder) {
    (L.Icon.Default as any).mergeOptions({
      iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
      iconUrl: 'assets/leaflet/marker-icon.png',
      shadowUrl: 'assets/leaflet/marker-shadow.png',
    });
  }

  ngOnInit(): void {
    this.sub = this.formArray.valueChanges.subscribe(() => this.renderFromForm());
  }

  // 👇 cambios mínimos: async + cargar plugin y usar strings de eventos
  async ngAfterViewInit(): Promise<void> {
    (window as any).L = L;
    await import('leaflet-draw');

    this.map = L.map(this.mapEl.nativeElement, { center: this.initialCenter, zoom: this.initialZoom });
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: '© OpenStreetMap contributors' }).addTo(this.map);
    this.drawnItems.addTo(this.map);

    this.drawControl = new (L.Control as any).Draw({
      draw: {
        polygon: { allowIntersection: false, showArea: true, guidelineDistance: 10, shapeOptions: { weight: 2 }, repeatMode: false },
        marker: false, polyline: false, rectangle: false, circle: false, circlemarker: false
      },
      edit: { featureGroup: this.drawnItems, remove: true }
    });
    this.map.addControl(this.drawControl);

    this.map.on('draw:created', (e: any) => {
      const layer = e.layer as L.Layer;
      if (this.polygonLayer) this.drawnItems.removeLayer(this.polygonLayer);
      this.polygonLayer = layer as L.Polygon;
      this.drawnItems.addLayer(this.polygonLayer);
      this.syncFormFromPolygon();
      this.fitBounds();
    });

    this.map.on('draw:edited', () => this.syncFormFromPolygon());
    this.map.on('draw:deleted', () => { this.polygonLayer = null; this.formArray.clear(); });

    this.renderFromForm();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
    if (this.map) this.map.remove();
  }

  private renderFromForm(): void {
    if (!this.formArray) return;
    const latlngs: L.LatLngLiteral[] = [];
    for (let i = 0; i < this.formArray.length; i++) {
      const g = this.formArray.at(i) as FormGroup;
      const lat = g.get('lat')?.value;
      const lng = g.get('lng')?.value;
      if (lat != null && lng != null) latlngs.push({ lat: +lat, lng: +lng });
    }
    if (latlngs.length < this.minVertices) {
      if (this.polygonLayer) {
        this.drawnItems.removeLayer(this.polygonLayer);
        this.polygonLayer = null;
      }
      return;
    }
    if (!this.polygonLayer) {
      this.polygonLayer = L.polygon(latlngs, { weight: 2 });
      this.drawnItems.addLayer(this.polygonLayer);
      this.fitBounds();
    } else {
      this.polygonLayer.setLatLngs(latlngs as any);
    }
  }

  private fitBounds(): void {
    if (this.polygonLayer) this.map.fitBounds(this.polygonLayer.getBounds(), { padding: [20, 20] });
  }

  private syncFormFromPolygon(): void {
    if (!this.polygonLayer) return;
    const latlngs: L.LatLng[] = (this.polygonLayer.getLatLngs()[0] as L.LatLng[]);
    if (!latlngs || latlngs.length < this.minVertices) { this.formArray.clear(); return; }

    const currentLen = this.formArray.length;
    const newLen = latlngs.length;
    const len = Math.min(currentLen, newLen);

    for (let i = 0; i < len; i++) {
      const g = this.formArray.at(i) as FormGroup;
      g.patchValue({ lat: +latlngs[i].lat, lng: +latlngs[i].lng }, { emitEvent: false });
    }
    for (let i = currentLen; i < newLen; i++) {
      this.formArray.push(this.fb.group({ id: [null], lat: [+latlngs[i].lat], lng: [+latlngs[i].lng] }), { emitEvent: false });
    }
    for (let i = currentLen - 1; i >= newLen; i--) this.formArray.removeAt(i, { emitEvent: false });
  }
}
