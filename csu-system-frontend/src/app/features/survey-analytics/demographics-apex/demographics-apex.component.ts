import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  NgApexchartsModule,
  ApexAxisChartSeries,
  ApexChart,
  ApexXAxis,
  ApexYAxis,
  ApexPlotOptions,
  ApexDataLabels,
  ApexLegend,
  ApexTooltip,
  ApexNonAxisChartSeries,
  ApexAnnotations
} from 'ng-apexcharts';

import { AnalyticsService } from '@core/services/analytics/analytics.service';
import { DemographicsSummaryDTO } from '@core/models/survey-analytics/demographics-summary.model';
import { GroupedCountDTO } from '@core/models/survey-analytics/grouped-count.model';
import { NeighborhoodService } from '@core/services/neighborhood.service';
import { CampaignService } from '@core/services/campaign.service';
import { Neighborhood } from '@core/models/neighborhood.model';

type ModeKey = 'chart' | 'table';
type ViewKey = 'all' | 'pyramid' | 'gender' | 'job' | 'edu' | 'cov';

interface ViewConfig {
  key: ViewKey;
  label: string;
  icon: string;
  color?: string;
}

@Component({
  selector: 'app-demographics-apex',
  standalone: true,
  imports: [CommonModule, FormsModule, NgApexchartsModule],
  templateUrl: './demographics-apex.component.html',
  styleUrls: ['./demographics-apex.component.scss']
})
export class DemographicsApexComponent implements OnInit {
  // ============================
  // Filtros
  // ============================
  neighborhoodId?: number;
  campaignId?: number;

  neighborhoods: Neighborhood[] = [];
  campaigns: Array<{ id: number; name: string; neighborhoodId?: number; survey?: any }> = [];
  filteredCampaigns: Array<{ id: number; name: string; neighborhoodId?: number; survey?: any }> = [];

  mode: ModeKey = 'chart';
  // Vista por defecto: TODOS (y respeta lo último guardado)
  selectedView: ViewKey = 'all';

  constructor(
    private analytics: AnalyticsService,
    private neighborhoodService: NeighborhoodService,
    private campaignService: CampaignService
  ) {}

  ngOnInit(): void {
    const saved = localStorage.getItem('analytics:view') as ViewKey | null;
    this.selectedView = saved ?? 'all';
    this.loadCampaigns();
  }

  // ============================
  // Manejo de cambios en filtros
  // ============================
  onNeighborhoodChange(neighborhoodId?: number) {
    if (neighborhoodId) {
      this.filteredCampaigns = this.campaigns.filter(c => c.neighborhoodId === neighborhoodId && c.survey);
    } else {
      this.filteredCampaigns = this.campaigns;
    }
    if (this.campaignId && !this.filteredCampaigns.some(c => c.id === this.campaignId)) {
      this.campaignId = undefined;
    }
  }

  onCampaignChange(campaignId?: number) {
    if (campaignId) {
      const c = this.campaigns.find(x => x.id === campaignId);
      this.campaignId = campaignId;
      this.neighborhoodId = c?.neighborhoodId;
      this.onNeighborhoodChange(this.neighborhoodId);
    } else {
      this.campaignId = undefined;
      this.neighborhoodId = undefined;
      this.onNeighborhoodChange(undefined);
    }
  }

  hasSurveys(): boolean {
    if (!this.summary) return false;
    return (
      this.summary.gender?.some(x => x.count > 0) ||
      this.summary.job?.some(x => x.count > 0) ||
      this.summary.education?.some(x => x.count > 0) ||
      this.summary.coverage?.some(x => x.count > 0)
    );
  }

  // ============================
  // Carga de listas
  // ============================
  private loadNeighborhoods(): void {
    this.neighborhoodService.getAll().subscribe({
      next: rows => {
        this.neighborhoods = rows.filter(n =>
          this.campaigns.some(c => c.neighborhoodId === n.id && c.survey)
        );
      },
      error: () => (this.error = 'Error cargando barrios')
    });
  }

  private loadCampaigns(): void {
    this.campaignService.getAll().subscribe({
      next: rows => {
        this.campaigns = (rows as Array<{ id: number; name: string; neighborhoodId?: number; survey?: any }>)
          .filter(c => !!c.survey);

        this.filteredCampaigns = this.neighborhoodId
          ? this.campaigns.filter(c => c.neighborhoodId === this.neighborhoodId)
          : this.campaigns;

        if (this.campaignId && !this.filteredCampaigns.some(c => c.id === this.campaignId)) {
          this.campaignId = undefined;
        }
        this.loadNeighborhoods();
      },
      error: () => (this.error = 'Error cargando campañas')
    });
  }

  // ============================
  // Estado y datos
  // ============================
  loading = false;
  error: string | null = null;

  summary?: DemographicsSummaryDTO;
  pyramidRows: GroupedCountDTO[] = [];

  chartColors = {
    primary: '#4A9B8E',
    secondary: '#6BB6AB',
    accent: '#2E8B7B',
    light: '#A8D5CF',
    gradient: ['#4A9B8E', '#6BB6AB', '#A8D5CF', '#2E8B7B'],
    pie: ['#4A9B8E', '#6BB6AB', '#A8D5CF', '#2E8B7B', '#7FC7BD', '#5CAFFF']
  };

  // ============================
  // Pirámide
  // ============================
  pyramidSeries: ApexAxisChartSeries = [];

  pyramidChart: ApexChart = {
    type: 'bar',
    height: 420,
    stacked: true,
    stackType: 'normal',
    toolbar: { show: false },
    animations: { enabled: false }
  };

  pyramidAnnotations: ApexAnnotations = {
    xaxis: [{ x: 0, borderColor: '#cfd9d6', strokeDashArray: 4 }]
  };

  pyramidPlot: ApexPlotOptions = {
    bar: {
      horizontal: true,
      barHeight: '70%',
      borderRadius: 4,
      borderRadiusApplication: 'end'
    }
  };

  pyramidXAxis: ApexXAxis = {
    categories: [],
    tickAmount: 5,
    decimalsInFloat: 0,
    labels: {
      formatter: (v: string | number) => {
        const n = Number(v);
        if (Number.isNaN(n)) return '';
        const s = Math.abs(Math.round(n)).toString();
        return n < 0 ? `-${s}` : s;
      }
    }
  };

  pyramidYAxis: ApexYAxis = { title: { text: 'Rangos de edad' } };

  pyramidDataLabels: ApexDataLabels = {
    enabled: true,
    formatter: (val: number) => `${Math.abs(Math.round(val))}`,
    style: { fontSize: '11px', fontWeight: 600 as any }
  };

  pyramidLegend: ApexLegend = { position: 'top' };

  pyramidTooltip: ApexTooltip = {
    theme: 'light',
    y: { formatter: (val: number) => `${Math.abs(val)} personas` }
  };

  // ============================
  // Pie (Género)
  // ============================
  genderLabels: string[] = [];
  genderSeries: ApexNonAxisChartSeries = [];

  private pieChartBase: ApexChart = {
    type: 'pie',
    height: 340,
    width: 360,
    animations: { enabled: false },
    toolbar: { show: false }
  };

  get pieChart(): ApexChart {
    if (this.selectedView === 'gender') {
      const isDesktop = typeof window !== 'undefined' && window.innerWidth >= 1024;
      return {
        ...this.pieChartBase,
        height: isDesktop ? 520 : 420,
        width:  isDesktop ? 600 : 380
      };
    }
    return this.pieChartBase;
  }

  pieDataLabels: ApexDataLabels = {
    enabled: true,
    formatter: (val: number) => `${val.toFixed(1)}%`,
    style: { fontSize: '12px', fontWeight: 600 as any, colors: ['#ffffff'] },
    dropShadow: { enabled: false }
  };

  get pieLegend(): ApexLegend {
    if (this.selectedView === 'gender') return { position: 'right', horizontalAlign: 'center' };
    return { position: 'bottom' };
  }

  pieTooltip: ApexTooltip = {
    theme: 'light',
    y: { formatter: (val: number) => `${val} personas` }
  };
  piePlotOptions: ApexPlotOptions = { pie: { donut: { size: '0%' }, expandOnClick: true } };

  // ============================
  // Barras (empleo, educación, cobertura)
  // ============================
  barChart: ApexChart = {
    type: 'bar',
    height: 320,
    animations: { enabled: false },
    toolbar: { show: false }
  };

  barPlot: ApexPlotOptions = {
    bar: {
      borderRadius: 6,
      borderRadiusApplication: 'end',
      columnWidth: '65%',
      colors: { backgroundBarColors: ['#F4F6F8'], backgroundBarRadius: 6 }
    }
  };

  barDataLabels: ApexDataLabels = {
    enabled: true,
    style: { fontSize: '11px', fontWeight: 600 as any, colors: ['#ffffff'] },
    formatter: (val: number) => (val > 0 ? `${val}` : ''),
    offsetY: -2
  };

  jobLabels: string[] = []; jobData: number[] = [];
  eduLabels: string[] = []; eduData: number[] = [];
  covLabels: string[] = []; covData: number[] = [];

  // ============================
  // Selector de vista
  // ============================

  views: ViewConfig[] = [
    { key: 'all',     label: 'Todos',     icon: 'fa-border-all' },
    { key: 'pyramid', label: 'Pirámide',  icon: 'fa-layer-group',     color: '#2E8B7B' },
    { key: 'gender',  label: 'Género',    icon: 'fa-users',           color: '#4A9B8E' },
    { key: 'job',     label: 'Empleo',    icon: 'fa-briefcase',       color: '#4A9B8E' },
    { key: 'edu',     label: 'Educación', icon: 'fa-graduation-cap',  color: '#4A9B8E' },
    { key: 'cov',     label: 'Cobertura', icon: 'fa-heartbeat',       color: '#4A9B8E' }
  ];

  setView(v: ViewKey) {
    this.selectedView = v;
    localStorage.setItem('analytics:view', v);
  }
  showPyramid() { return this.mode === 'chart' && this.selectedView === 'pyramid'; }
  showGender()  { return this.mode === 'chart' && this.selectedView === 'gender'; }
  showJob()     { return this.mode === 'chart' && this.selectedView === 'job'; }
  showEdu()     { return this.mode === 'chart' && this.selectedView === 'edu'; }
  showCov()     { return this.mode === 'chart' && this.selectedView === 'cov'; }
  showAll()     { return this.mode === 'chart' && this.selectedView === 'all'; }

  showAllTable()   { return this.mode === 'table' && this.selectedView === 'all'; }
  showGenderTable(){ return this.mode === 'table' && this.selectedView === 'gender'; }
  showJobTable()   { return this.mode === 'table' && this.selectedView === 'job'; }
  showEduTable()   { return this.mode === 'table' && this.selectedView === 'edu'; }
  showCovTable()   { return this.mode === 'table' && this.selectedView === 'cov'; }
  // ============================
  // Acciones
  // ============================
  fetch() {
    this.error = null;
    if (this.neighborhoodId == null && this.campaignId == null) {
      this.error = 'Elegí un barrio o una campaña.';
      return;
    }
    this.loading = true;

    const params = {
      neighborhoodId: this.neighborhoodId,
      campaignId: this.campaignId
    };

    this.analytics.getSummary(params).subscribe({
      next: d => { this.summary = d; this.buildFromSummary(d); },
      error: e => { console.error(e); this.error = 'No se pudo cargar el resumen.'; },
      complete: () => { this.loading = false; }
    });

    this.analytics.getAgePyramid(params).subscribe({
      next: rows => { this.pyramidRows = rows; this.buildPyramid(rows); },
      error: e => { console.error(e); this.error = 'No se pudo cargar la pirámide.'; }
    });
  }

  reset() {
    this.neighborhoodId = undefined;
    this.campaignId = undefined;
    this.summary = undefined;
    this.pyramidRows = [];
    this.pyramidSeries = [];
    this.pyramidXAxis = { ...this.pyramidXAxis, categories: [], min: undefined, max: undefined };

    this.genderLabels = []; this.genderSeries = [];
    this.jobLabels = []; this.jobData = [];
    this.eduLabels = []; this.eduData = [];
    this.covLabels = []; this.covData = [];
    this.error = null;
  }

  // ============================
  // Mappers / helpers
  // ============================
  private buildFromSummary(d: DemographicsSummaryDTO) {
    this.genderLabels = d.gender.map(x => x.group);
    this.genderSeries = d.gender.map(x => x.count);

    this.jobLabels = d.job.map(x => x.group);
    this.jobData   = d.job.map(x => x.count);

    this.eduLabels = d.education.map(x => x.group);
    this.eduData   = d.education.map(x => x.count);

    this.covLabels = d.coverage.map(x => x.group);
    this.covData   = d.coverage.map(x => x.count);
  }

  private buildPyramid(rows: GroupedCountDTO[]) {
    if (!rows?.length) {
      this.pyramidSeries = [];
      this.pyramidXAxis = { ...this.pyramidXAxis, categories: [] };
      return;
    }

    const buckets = Array.from(new Set(rows.map(r => r.group))).sort(this.bucketSort);
    const genders = Array.from(new Set(rows.map(r => r.subgroup)));

    const map = new Map<string, Map<string, number>>();
    buckets.forEach(b => map.set(b, new Map()));
    rows.forEach(r => map.get(r.group)!.set(r.subgroup, r.count));

    const isMale = (g: string) => {
      const s = (g || '').trim().toLowerCase();
      return ['varón','varon','hombre','masculino','m','male','masc'].some(x => s.includes(x));
    };

    this.pyramidSeries = genders.map(g => ({
      name: g,
      data: buckets.map(b => {
        const v = map.get(b)?.get(g) ?? 0;
        return isMale(g) ? -v : v;
      })
    }));

    // máximo absoluto y “nice” simétrico
    const rawMax = Math.max(1, ...this.pyramidSeries.flatMap(s => s.data.map(v => Math.abs(Number(v)))));
    const niceStep = (n: number) => {
      const p = Math.pow(10, Math.floor(Math.log10(n)));
      const m = n / p;
      const step = m <= 2 ? 2 : m <= 5 ? 5 : 10;
      return step * p;
    };
    const niceMax = niceStep(rawMax);

    // ajustar alto de barra según cantidad de buckets
    const barHeight =
      buckets.length >= 8 ? '70%' :
        buckets.length >= 5 ? '75%' :
          buckets.length >= 3 ? '80%' : '60%';
    this.pyramidPlot = { bar: { ...this.pyramidPlot.bar!, barHeight } };

    this.pyramidXAxis = {
      ...this.pyramidXAxis,
      categories: buckets,
      min: -niceMax,
      max:  niceMax,
      tickAmount: 5
    };
  }

  private bucketSort = (a: string, b: string) => {
    const n = (s: string) => { const m = s.match(/^(\d+)/); return m ? parseInt(m[1], 10) : Number.MAX_SAFE_INTEGER; };
    return n(a) - n(b);
  };

  // Barras helpers
  configureBarSeries(data: number[], name: string): ApexAxisChartSeries { return [{ name, data }]; }

  getXAxisConfig(categories: string[]): ApexXAxis {
    return { categories, labels: { rotate: -45, maxHeight: 60 } };
  }

  getYAxisConfig(title: string): ApexYAxis {
    return { title: { text: title }, decimalsInFloat: 0 };
  }
}
