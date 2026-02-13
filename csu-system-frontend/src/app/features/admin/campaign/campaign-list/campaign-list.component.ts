import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Campaign } from '@core/models/campaign.model';
import { Event as CampaignEvent } from '@core/models/event.model';
import { Surveyor } from '@core/models/surveyor.model';
import { CampaignService } from '@core/services/campaign.service';
import { EventService } from '@core/services/event.service';
import { AppRoutes } from '@shared/constants/app-routes.constants';
import { EventListComponent } from '@features/admin/campaign/event/event-list/event-list.component';
import { EventFormComponent } from '@features/admin/campaign/event/event-form/event-form.component';
import { NeighborhoodService } from '@core/services/neighborhood.service';
import { ZoneService } from '@core/services/zone.service';
import { SurveyInfoBoxComponent } from '@shared/components/survey-info-box/survey-info-box.component';
import { FullDateEsPipe } from '@shared/pipes/full-date-es.pipe';

@Component({
  selector: 'app-campaign-list',
  standalone: true,
  imports: [
    CommonModule, RouterModule, EventListComponent, EventFormComponent,
    FullDateEsPipe, SurveyInfoBoxComponent
  ],
  templateUrl: './campaign-list.component.html',
  styleUrls: ['./campaign-list.component.scss']
})
export class CampaignListComponent implements OnInit {
  campaigns: Campaign[] = [];
  events: { [key: number]: CampaignEvent[] } = {};
  loadingEvents: { [key: number]: boolean } = {};
  neighborhoods: { [key: number]: string } = {};
  zonesByEvent: { [id: number]: string } = {};
  surveyorsByEvent: { [eventId: number]: string[] } = {};
  expandedCampaignId: number | null = null;
  showEventForm: { [key: number]: boolean } = {};
  eventToEditMap: { [campaignId: number]: CampaignEvent | null } = {};
  loading = true;
  error: string | null = null;
  isList = true;


  constructor(
    private campaignService: CampaignService,
    private eventService: EventService,
    private router: Router,
    private neighborhoodService: NeighborhoodService,
    private zoneService: ZoneService,
  ) {
  }

  ngOnInit(): void {
    this.fetchCampaigns();
    this.fetchNeighborhoods();
  }

  fetchCampaigns(): void {
    this.campaignService.getAll().subscribe({
      next: data => {
        this.campaigns = data;
        this.loading = false;
      },
      error: err => {
        this.error = 'Error cargando campañas';
        this.loading = false;
      }
    });
  }

  toggleEvents(campaignId: number): void {
    if (this.expandedCampaignId === campaignId) {
      this.expandedCampaignId = null;
      return;
    }

    this.expandedCampaignId = campaignId;

    if (!this.events[campaignId]) {
      this.loadingEvents[campaignId] = true;

      this.eventService.getByCampaignId(campaignId).subscribe({
        next: data => {
          this.events[campaignId] = data;
          this.showEventForm[campaignId] = false;
          this.loadingEvents[campaignId] = false;

          const campaign = this.campaigns.find(c => c.id === campaignId);
          if (campaign) {
            data.forEach(event => {
              this.fetchZoneNameByEvent(campaign.neighborhoodId, event.zoneId, event.id!);
              this.fetchSurveyorsByEvent(campaignId, event.id!);
            });
          }
        },
        error: err => {
          this.error = 'Error al cargar jornadas';
          this.loadingEvents[campaignId] = false;
          console.error(err);
        }
      });
    }
  }

  goToNewCampaign(): void {
    this.router.navigate([AppRoutes.Admin.Campaigns.New]);
  }

  goToEditCampaign(id: number): void {
    this.router.navigate([AppRoutes.Admin.Campaigns.Edit(id)]);
  }

  deleteCampaign(id: number): void {
    if (!confirm('¿Eliminar campaña?')) return;
    this.campaignService.delete(id).subscribe({
      next: () => this.fetchCampaigns(),
      error: err => {
        this.error = 'Error eliminando campaña';
        console.error(err);
      }
    });
  }

  deleteEvent(eventId: number, campaignId: number): void {
    if (!this.events[campaignId]) return;
    this.eventService.delete(campaignId, eventId).subscribe({
      next: () => {
        this.events[campaignId] = this.events[campaignId].filter(e => e.id !== eventId);
      },
      error: err => {
        this.error = 'Error eliminando jornada';
        console.error(err);
      }
    });
  }

  fetchNeighborhoods(): void {
    this.neighborhoodService.getAll().subscribe({
      next: data => {
        for (const n of data) {
          this.neighborhoods[n.id!] = n.name;
        }
      },
      error: err => {
        console.error('Error cargando barrios', err);
      }
    });
  }

  toggleEventForm(campaignId: number): void {
    this.showEventForm[campaignId] = !this.showEventForm[campaignId];
    if (this.showEventForm[campaignId]) {
      this.eventToEditMap[campaignId] = null;
    } else {
      this.resetEventForm(campaignId);
    }
  }

  fetchZoneNameByEvent(neighborhoodId: number, zoneId: number, eventId: number): void {
    this.zoneService.getById(neighborhoodId, zoneId).subscribe({
      next: zone => {
        this.zonesByEvent[eventId] = zone.name;
      },
      error: err => {
        this.zonesByEvent[eventId] = 'Zona desconocida';
        console.error('Error cargando zona', err);
      }
    });
  }

  fetchSurveyorsByEvent(campaignId: number, eventId: number): void {
    this.eventService.getSurveyors(campaignId, eventId).subscribe({
      next: (data: Surveyor[]) => {
        this.surveyorsByEvent[eventId] = data.map((s: any) => `${s.firstName} ${s.lastName}`);
      },
      error: err => console.error('Error cargando encuestadores', err)
    });
  }

  addEvent(event: CampaignEvent, campaignId: number): void {
    this.events[campaignId].push(event);

    const campaign = this.campaigns.find(c => c.id === campaignId);
    if (campaign) {
      this.fetchZoneNameByEvent(campaign.neighborhoodId, event.zoneId, event.id!);
      this.fetchSurveyorsByEvent(campaignId, event.id!);
    }
    this.showEventForm[campaignId] = false;
  }

  updateEvent(updated: CampaignEvent, campaignId: number): void {
    const events = this.events[campaignId];
    const index = events.findIndex(e => e.id === updated.id);
    if (index !== -1) {
      events[index] = updated;
      const campaign = this.campaigns.find(c => c.id === campaignId);
      if (campaign) {
        this.fetchZoneNameByEvent(campaign.neighborhoodId, updated.zoneId, updated.id!);
        this.fetchSurveyorsByEvent(campaignId, updated.id!);
      }
    }
    this.resetEventForm(campaignId);
  }

  editEvent(event: CampaignEvent, campaignId: number): void {
    this.eventToEditMap[campaignId] = event;
    this.showEventForm[campaignId] = true;
  }

  resetEventForm(campaignId: number): void {
    this.eventToEditMap[campaignId] = null;
    this.showEventForm[campaignId] = false;
  }

  deleteSurvey(campaignId: number): void {
    this.campaignService.deleteSurvey(campaignId).subscribe({
      next: () => {
        const campaign = this.campaigns.find(c => c.id === campaignId);
        if (campaign) campaign.survey = undefined;
      },
      error: () => {
        this.error = 'Error al eliminar la encuesta.';
      }
    });
  }
}
