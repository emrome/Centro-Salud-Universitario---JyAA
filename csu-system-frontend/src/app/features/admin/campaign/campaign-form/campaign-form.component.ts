import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CampaignService } from '@core/services/campaign.service';
import { NeighborhoodService } from '@core/services/neighborhood.service';
import { ZoneService } from '@core/services/zone.service';
import { EventService } from '@core/services/event.service';
import { Campaign } from '@core/models/campaign.model';
import { Neighborhood } from '@core/models/neighborhood.model';
import { Event as CampaignEvent } from '@core/models/event.model';
import { Surveyor } from '@core/models/surveyor.model';
import { AppRoutes } from '@shared/constants/app-routes.constants';
import { EventListComponent } from '@features/admin/campaign/event/event-list/event-list.component';
import {SurveyInfoBoxComponent} from '@shared/components/survey-info-box/survey-info-box.component';

@Component({
  selector: 'app-campaign-form',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterModule,
    EventListComponent, SurveyInfoBoxComponent
  ],
  templateUrl: './campaign-form.component.html'
})
export class CampaignFormComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  campaignId!: number;
  campaign!: Campaign;
  loadingEvents = false;
  events: CampaignEvent[] = [];
  surveyorsByEvent: { [eventId: number]: string[] } = {};
  zonesByEvent: { [eventId: number]: string } = {};
  neighborhoodId!: number;
  error: string | null = null;
  neighborhoods: Neighborhood[] = [];
  formCsv?: File;
  branchCsv?: File;
  showUploadForm = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;
  uploadErrorMessage = false;
  isSubmitting = false;
  isUploading = false;
  earliestEventDate?: string;
  latestEventDate?: string;

  constructor(
    private fb: FormBuilder,
    private campaignService: CampaignService,
    private neighborhoodService: NeighborhoodService,
    private zoneService: ZoneService,
    private eventService: EventService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group(
      {
        name: ['', Validators.required],
        startDate: ['', Validators.required],
        endDate: ['', Validators.required],
        neighborhoodId: [null, Validators.required]
      },
      { validators: [this.dateRangeValidator, this.dateMustCoverEvents()] }
    );

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEdit = true;
      this.campaignId = +idParam;
      this.loadCampaign();
    }

    this.loadNeighborhoods();
  }

  loadCampaign(): void {
    this.campaignService.getById(this.campaignId).subscribe({
      next: (data: Campaign) => {
        this.form.patchValue(data);
        this.campaign = data;
        this.loadEvents();
        this.neighborhoodId = data.neighborhoodId;
      },
      error: () => (this.error = 'Error al cargar la campaña')
    });
  }

  loadNeighborhoods(): void {
    this.neighborhoodService.getAll().subscribe({
      next: data => (this.neighborhoods = data),
      error: () => (this.error = 'Error cargando barrios')
    });
  }

  onSubmit(): void {
    this.isSubmitting = true;
    const dto: Campaign = { ...this.form.value };

    const request$ = this.isEdit
      ? this.campaignService.update(this.campaignId, dto)
      : this.campaignService.create(dto);

    request$.subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigate([AppRoutes.Admin.Campaigns.List]);
      },
      error: (err) => {
        this.isSubmitting = false;
        const backendMsg: string | undefined = err?.error?.error || err?.error?.message;

        if (err.status === 409) this.error = 'Ya existe una campaña con ese nombre';
        else if (err.status === 404) this.error = 'Barrio no encontrado';
        else if (err.status === 400 && backendMsg?.includes('jornadas fuera del nuevo rango')) {
          this.error = backendMsg;
        } else if (err.status === 400) {
          this.error = 'Datos inválidos o fuera de rango (fecha de inicio o fin)';
        } else {
          this.error = 'Error guardando la campaña';
        }
        console.error(err);
      }
    });
  }

  cancel(): void {
    this.router.navigate([AppRoutes.Admin.Campaigns.List]);
  }

  deleteEvent(eventId: number): void {
    this.eventService.delete(this.campaignId, eventId).subscribe({
      next: () => this.loadEvents(),
      error: () => this.error = 'Error eliminando evento'
    });
  }

  loadEvents(): void {
    this.loadingEvents = true;
    this.eventService.getByCampaignId(this.campaignId).subscribe({
      next: (data: CampaignEvent[]) => {
        this.events = data;

        if (this.events.length > 0) {
          const dates = this.events.map(e => e.date).filter(Boolean).sort();
          this.earliestEventDate = dates[0];
          this.latestEventDate = dates[dates.length - 1];
        } else {
          this.earliestEventDate = undefined;
          this.latestEventDate = undefined;
        }

        this.form.updateValueAndValidity({ onlySelf: false, emitEvent: false });

        for (const event of this.events) {
          this.loadZoneName(event);
          this.loadSurveyorNames(event);
        }

        this.loadingEvents = false;
      },
      error: () => {
        this.error = 'Error cargando eventos';
        this.loadingEvents = false;
      }
    });
  }

  dateMustCoverEvents() {
    return (group: FormGroup) => {
      if (!this.isEdit || !this.events || this.events.length === 0) {
        return null;
      }

      const start: string | null = group.get('startDate')?.value || null;
      const end: string | null = group.get('endDate')?.value || null;

      if (!start || !end || !this.earliestEventDate || !this.latestEventDate) {
        return null;
      }

      const errors: any = {};
      if (start > this.earliestEventDate) {
        errors.startExcludesEvents = true;
      }
      if (end < this.latestEventDate) {
        errors.endExcludesEvents = true;
      }

      return Object.keys(errors).length ? errors : null;
    };
  }

  getNeighborhoodName(id: number): string {
    const n = this.neighborhoods.find(n => n.id === id);
    return n ? n.name : 'Barrio desconocido';
  }

  loadSurveyorNames(event: CampaignEvent): void {
    this.eventService.getSurveyors(this.campaignId, event.id!).subscribe({
      next: (surveyors: Surveyor[]) => {
        this.surveyorsByEvent[event.id!] = surveyors.map(s => `${s.firstName} ${s.lastName}`);
      },
      error: () => {
        this.surveyorsByEvent[event.id!] = ['Error al cargar encuestadores'];
      }
    });
  }

  loadZoneName(event: CampaignEvent): void {
    this.zoneService.getByNeighborhood(this.neighborhoodId).subscribe({
      next: zones => {
        const zone = zones.find(z => z.id === event.zoneId);
        this.zonesByEvent[event.id!] = zone ? zone.name : 'Zona desconocida';
      },
      error: () => {
        this.zonesByEvent[event.id!] = 'Error al cargar zona';
      }
    });
  }

  dateRangeValidator(group: FormGroup): { [key: string]: any } | null {
    const start = group.get('startDate')?.value;
    const end = group.get('endDate')?.value;

    if (start && end && end < start) {
      return { dateRangeInvalid: true };
    }
    return null;
  }

  onFileChange(event: Event, type: 'form' | 'branch') {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      if (type === 'form') this.formCsv = input.files[0];
      else this.branchCsv = input.files[0];
    }
  }

  uploadSurvey() {
    if (!this.formCsv || !this.branchCsv || !this.campaign) {
      this.errorMessage = 'Faltan archivos o campaña no cargada';
      return;
    }
    if (this.campaign.survey) {
      const confirmed = confirm("Ya existe una encuesta para esta campaña. ¿Deseás eliminarla para subir una nueva?");
      if (!confirmed) return;

      this.deleteSurveyAndUpload();
    } else {
      this.performUpload();
    }
  }

  deleteSurvey(): void {
    if (!this.campaignId) return;
    this.campaignService.deleteSurvey(this.campaignId).subscribe({
      next: () => {
        this.campaign.survey = undefined;
        this.successMessage = 'Encuesta eliminada correctamente.';
      },
      error: () => {
        this.errorMessage = 'Ocurrió un error al eliminar la encuesta.';
      }
    });
  }

  deleteSurveyAndUpload(): void {
    this.campaignService.deleteSurvey(this.campaignId).subscribe({
      next: () => {
        this.campaign.survey = undefined;
        this.successMessage = 'Encuesta anterior eliminada correctamente.';
        this.performUpload();
      },
      error: () => {
        this.errorMessage = 'Error al eliminar la encuesta anterior.';
      }
    });
  }

  performUpload(): void {
    this.isUploading = true;

    const formData = new FormData();
    formData.append('formCsv', this.formCsv!);
    formData.append('branchCsv', this.branchCsv!);
    formData.append('campaignId', this.campaignId.toString());
    formData.append('campaignCode', this.campaign.name.replaceAll(" ", "_").toLowerCase());
    formData.append('neighborhoodName', this.getNeighborhoodName(this.neighborhoodId).replaceAll(" ", "_").toLowerCase());

    this.successMessage = null;
    this.errorMessage = null;

    this.campaignService.importSurvey(this.campaignId, formData).subscribe({
      next: () => {
        this.isUploading = false;
        this.successMessage = 'Encuesta importada correctamente';
        this.formCsv = undefined;
        this.branchCsv = undefined;
        this.showUploadForm = false;
        this.loadCampaign();
      },
      error: () => {
        this.isUploading = false;
        this.uploadErrorMessage = true;
      }
    });
  }
}
