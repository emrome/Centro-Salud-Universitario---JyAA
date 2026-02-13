import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SurveyorService } from '@core/services/surveyor.service';
import { AppRoutes } from '@shared/constants/app-routes.constants';

@Component({
  selector: 'app-surveyor-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './surveyor-list.component.html'
})
export class SurveyorListComponent implements OnInit {
  surveyors: any[] = [];
  loading = true;
  error: string | null = null;
  paths = AppRoutes;

  genderLabels: Record<string, string> = {
    WOMAN_CIS: 'Mujer cis',
    MAN_CIS: 'Varón cis',
    WOMAN_TRANS: 'Mujer trans',
    MAN_TRANS: 'Varón trans',
    NON_BINARY: 'No binarie',
    OTHER_IDENTITY: 'Otro',
    DONT_KNOW_OR_NO_ANSWER: 'No sabe o no contesta'
  };

  occupationLabels: Record<string, string> = {
    STUDENT: "Estudiante",
    PROFESSIONAL: 'Profesional',
    CSU_STAFF: 'Personal CSU',
    VOLUNTEER: 'Voluntario',
    OTHER: 'Otro',
  };

  constructor(private surveyorService: SurveyorService) {}

  ngOnInit(): void {
    this.surveyorService.getAll().subscribe({
      next: (data) => {
        this.surveyors = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Error al cargar los encuestadores';
        this.loading = false;
      }
    });
  }

  deleteSurveyor(id: number): void {
    if (!confirm('¿Estás seguro que querés eliminar este encuestador?')) return;

    this.surveyorService.delete(id).subscribe({
      next: () => {
        this.surveyors = this.surveyors.filter(s => s.id !== id);
      },
      error: () => {
        this.error = 'Error al eliminar el encuestador';
      }
    });
  }
}
