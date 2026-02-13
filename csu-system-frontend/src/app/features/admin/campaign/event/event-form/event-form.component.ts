import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { ZoneService } from '@core/services/zone.service';
import { SurveyorService } from '@core/services/surveyor.service';
import { EventService } from '@core/services/event.service';
import { Zone } from '@core/models/zone.model';
import { Surveyor } from '@core/models/surveyor.model';
import { Event as CampaignEvent } from '@core/models/event.model';
import { CampaignService } from '@core/services/campaign.service';

@Component({
  selector: 'app-event-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './event-form.component.html'
})
export class EventFormComponent implements OnInit, OnChanges {
  @Input() campaignId!: number;
  @Output() onCreate = new EventEmitter<CampaignEvent>();
  @Input() minDate!: string;
  @Input() maxDate!: string;
  @Input() eventToEdit: CampaignEvent | null = null;
  @Output() onUpdate = new EventEmitter<CampaignEvent>();
  form!: FormGroup;
  zones: Zone[] = [];
  surveyors: Surveyor[] = [];
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private zoneService: ZoneService,
    private surveyorService: SurveyorService,
    private eventService: EventService,
    private campaignService: CampaignService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group(
      {
        date: ['', Validators.required],
        zoneId: [null, Validators.required],
        surveyorIds: [[], Validators.required]
      },
      {
        validators: [
          this.validateDateInRange(),
          this.validateAtLeastOneSurveyor()
        ]
      }
    );

    if (this.eventToEdit) {
      this.form.patchValue({
        date: this.eventToEdit.date,
        zoneId: this.eventToEdit.zoneId,
        surveyorIds: this.eventToEdit.surveyorIds
      });
      this.form.markAsTouched();
      this.form.markAsDirty();
    }

    this.campaignService.getById(this.campaignId).subscribe({
      next: campaign => {
        this.zoneService.getByNeighborhood(campaign.neighborhoodId).subscribe({
          next: zones => this.zones = zones,
          error: () => this.error = 'Error cargando zonas'
        });
      },
      error: () => this.error = 'Error cargando campaña'
    });

    this.surveyorService.getAll().subscribe({
      next: data => (this.surveyors = data),
      error: () => (this.error = 'Error cargando encuestadores')
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['minDate'] || changes['maxDate']) && this.form) {
      this.form.setValidators(this.validateDateInRange());
      this.form.updateValueAndValidity();
    }
    if (changes['eventToEdit'] && this.form && this.eventToEdit) {
      this.form.patchValue({
        date: this.eventToEdit.date,
        zoneId: this.eventToEdit.zoneId,
        surveyorIds: this.eventToEdit.surveyorIds
      });
      this.form.markAsTouched();
      this.form.markAsDirty();
    }
  }

  onSubmit(): void {
    const dto: CampaignEvent = { ...this.form.value };
    if (this.form.invalid) {
      this.error = 'Datos inválidos o fuera de rango de campaña';
      return;
    }
    this.error = null;

    if (this.eventToEdit) {
      this.eventService.update(this.campaignId, this.eventToEdit.id!, dto).subscribe({
        next: updated => {
          this.onUpdate.emit(updated);
          this.resetForm();
        },
        error: err => {
          this.error = 'Error al actualizar la jornada';
          console.error(err);
        }
      });
    } else {
      this.eventService.create(this.campaignId, dto).subscribe({
        next: created => {
          this.onCreate.emit(created);
          this.resetForm();
        },
        error: err => {
          if (err.status === 400) {
            this.error = 'Datos inválidos o fuera de rango de campaña';
          } else {
            this.error = 'Error al crear la jornada';
          }
          console.error(err);
        }
      });
    }
  }

  resetForm(): void {
    this.form.reset({
      date: '',
      zoneId: null,
      surveyorIds: []
    });
    this.form.markAsUntouched();
    this.form.markAsPristine();
  }

  onSurveyorCheckboxChange(event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    const current = this.form.get('surveyorIds')?.value as number[];

    if (checkbox.checked) {
      this.form.get('surveyorIds')?.setValue([...current, +checkbox.value]);
    } else {
      this.form.get('surveyorIds')?.setValue(current.filter(id => id !== +checkbox.value));
    }
  }

  validateDateInRange(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      if (!this.minDate || !this.maxDate) return null;

      const dateStr = control.get('date')?.value;
      if (!dateStr) return null;

      const date = new Date(dateStr);
      const min = new Date(this.minDate);
      const max = new Date(this.maxDate);

      return date < min || date > max ? { dateOutOfRange: true } : null;
    };
  }

  validateAtLeastOneSurveyor(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const surveyors = control.get('surveyorIds')?.value;
      return Array.isArray(surveyors) && surveyors.length === 0
        ? { noSurveyors: true }
        : null;
    };
  }
}
