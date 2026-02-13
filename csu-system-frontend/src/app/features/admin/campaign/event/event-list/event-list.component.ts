import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Event as CampaignEvent } from '@core/models/event.model';
import { FullDateEsPipe } from '@shared/pipes/full-date-es.pipe';


@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [CommonModule, FullDateEsPipe],
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.scss']
})
export class EventListComponent {
  @Input() events: CampaignEvent[] = [];
  @Output() onDelete = new EventEmitter<number>();
  @Output() onEdit = new EventEmitter<CampaignEvent>();
  @Input() isList: boolean = false;
  error: string | null = null;
  @Input() surveyorsByEvent: { [eventId: number]: string[] } = {};
  @Input() zonesByEvent: { [zoneId: number]: string } = {};

  constructor() {
  }

  delete(eventId: number): void {
    const campaign = this.events.find(c => c.id === eventId);
    if (!campaign){
      this.error = 'Jornada no encontrada para borrar.';
      return;
    }
    if (confirm('¿Está seguro que quiere eliminar la jornada?')) {
      this.onDelete.emit(eventId);
    }
  }

  edit(event: CampaignEvent): void {
    if (!this.isList) {
      this.error = 'No se puede editar la jornada porque no está en modo edición.';
      return;
    }
    this.onEdit.emit(event);
  }
}
