import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Survey } from '@core/models/survey.model';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-survey-info-box',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './survey-info-box.component.html'
})
export class SurveyInfoBoxComponent {
  @Input() survey!: Survey;
  @Output() uploadRequested = new EventEmitter<void>();
  @Output() deleteRequested = new EventEmitter<void>();

  deleteSurvey(): void {
    confirm('¿Está seguro de que desea eliminar esta encuesta?') && this.deleteRequested.emit();
  }

}
