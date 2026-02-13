import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { SurveyorService } from '@core/services/surveyor.service';
import { PersonFormBaseComponent } from '@features/admin/people/person/person-form-base/person-form-base.component';
import { GENDER_IDENTITIES } from '@core/enums/gender-identity.enum';
import { OCCUPATIONS } from '@core/enums/occupation.enum';
import { AppRoutes } from '@shared/constants/app-routes.constants';
import { CustomValidators } from '@shared/validators/custom-validators';
import { DateUtils } from '@shared/utils/date-utils';

@Component({
  selector: 'app-surveyor-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, PersonFormBaseComponent],
  templateUrl: './surveyor-form.component.html'
})
export class SurveyorFormComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  surveyorId!: number;
  error: string | null = null;
  paths = AppRoutes;
  submitted = false;

  genders = GENDER_IDENTITIES;
  occupations = OCCUPATIONS;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private surveyorService: SurveyorService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName:  ['', [Validators.required, Validators.minLength(2)]],
      birthDate: ['', [Validators.required, CustomValidators.minAge(16)]],
      dni:        ['', [Validators.required, Validators.pattern(/^\d{7,9}$/)]],
      gender:     [null, [Validators.required]],
      occupation: [null, [Validators.required]]
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEdit = true;
      this.surveyorId = +idParam;
      this.loadSurveyor();
    }
  }

  loadSurveyor(): void {
    this.surveyorService.getById(this.surveyorId).subscribe({
      next: (data) => this.form.patchValue(data),
      error: () => (this.error = 'Error al cargar el encuestador')
    });
  }

  onSubmit(): void {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const dto = { ...this.form.value };
    const request$ = this.isEdit
      ? this.surveyorService.update(this.surveyorId, dto)
      : this.surveyorService.create(dto);

    request$.subscribe({
      next: () => this.router.navigate([this.paths.Admin.Surveyors.List]),
      error: (err) => {
        if (err.status === 409) this.error = 'El DNI ya está en uso';
        else if (err.status === 400) this.error = 'Datos inválidos';
        else this.error = 'Error al guardar';
      }
    });
  }

  cancel(): void {
    this.router.navigate([this.paths.Admin.Surveyors.List]);
  }

  fieldInvalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.submitted);
  }

  getError(name: string): string {
    const c = this.form.get(name);
    if (!c || !c.errors) return '';
    const e = c.errors;
    if (e['pattern'])    return 'Formato de dni inválido, debe tener entre 7 y 9 dígitos';
    return 'Dato inválido';
  }

  maxBirthDate(): string {
    return DateUtils.isoDateTodayMinusYears(16);
  }
}
