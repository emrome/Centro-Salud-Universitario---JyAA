import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RegisterService } from '@core/services/auth/register.service';
import { OrganizationService } from '@core/services/organization.service';
import { Organization } from '@core/models/organization.model';
import { SPECIALTIES } from '@core/enums/specialty.enum';
import { CustomValidators } from '@shared/validators/custom-validators';
import { DateUtils } from '@shared/utils/date-utils';
import { AppRoutes } from '@shared/constants/app-routes.constants';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
})
export class RegisterComponent implements OnInit {
  form: FormGroup;
  userType: string = 'HealthStaff';
  submitted = false;
  isSubmitting = false;
  error: string | null = null;
  success = false;

  specialties = SPECIALTIES;
  organizations: Organization[] = [];
  loadingOrganizations = false;

  constructor(
    private fb: FormBuilder,
    private registerService: RegisterService,
    private organizationService: OrganizationService,
    private router: Router
  ) {
    this.form = this.fb.group({
      userType: ['HealthStaff', Validators.required],
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName:  ['', [Validators.required, Validators.minLength(2)]],
      birthDate: ['', [Validators.required, CustomValidators.minAge(18)]],
      email:     ['', [Validators.required, Validators.email]],
      password:  ['', [Validators.required, Validators.minLength(8), CustomValidators.strongPassword()]],
      specialty: [''],
      license: [''],
      organizationId: [null],
    });
  }

  ngOnInit(): void {
    this.applyDynamicValidators();

    this.form.get('userType')?.valueChanges.subscribe((type: string) => {
      this.userType = type;
      this.applyDynamicValidators();
      this.resetTypeSpecificFields(type);
    });

    this.loadOrganizations();
  }

  passwordVisible = false;

  togglePassword(): void {
    this.passwordVisible = !this.passwordVisible;
  }

  private loadOrganizations(): void {
    this.loadingOrganizations = true;
    this.organizationService.getAll().subscribe({
      next: (data) => {
        this.organizations = data.slice().sort((a, b) => a.name.localeCompare(b.name));
        this.loadingOrganizations = false;
      },
      error: () => {
        this.loadingOrganizations = false;
        this.error = 'Error cargando organizaciones';
      }
    });

    this.applyDynamicValidators();
  }

  private applyDynamicValidators(): void {
    const type = this.form.get('userType')?.value;

    const specialty      = this.form.get('specialty')!;
    const license        = this.form.get('license')!;
    const organizationId = this.form.get('organizationId')!;

    specialty.clearValidators();
    license.clearValidators();
    organizationId.clearValidators();

    if (type === 'HealthStaff') {
      specialty.setValidators([Validators.required]);
      license.setValidators([Validators.required]);
    } else if (type === 'SocialOrgRepresentative') {
      organizationId.setValidators([Validators.required]);
    }

    // actualizar sin emitir más eventos
    specialty.updateValueAndValidity({ emitEvent: false });
    license.updateValueAndValidity({ emitEvent: false });
    organizationId.updateValueAndValidity({ emitEvent: false });
  }

  private resetTypeSpecificFields(type: string): void {
    if (type !== 'HealthStaff') {
      this.form.get('specialty')?.reset('');
      this.form.get('license')?.reset('');
    }
    if (type !== 'SocialOrgRepresentative') this.form.get('organizationId')?.reset(null);
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = null;
    this.success = false;

    if (this.form.invalid) return;
    this.isSubmitting = true;

    const value = this.form.value;

    const payload: any = {
      userType: value.userType,
      firstName: value.firstName,
      lastName: value.lastName,
      birthDate: value.birthDate,
      email: value.email,
      password: value.password,
      enabled: false
    };

    if (value.userType === 'HealthStaff') {
      payload.specialty = value.specialty;
      payload.license = value.license;
    } else if (value.userType === 'SocialOrgRepresentative') {
      payload.organizationId = Number(value.organizationId);
    }

    this.registerService.register(payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.success = true;
        this.error = null;
      },
      error: (err) => {
        this.isSubmitting = false;

        // 400 con details de Bean Validation
        if (err?.status === 400 && Array.isArray(err.error?.details) && err.error.details.length) {
          this.error = err.error.details.map((d: any) => d.message).join(' · ');
          return;
        }

        if (err?.status === 409) {
          const message = (typeof err.error === 'string' ? err.error : err.error?.message || '').toLowerCase();
          if (message.includes('email')) {
            this.error = 'Ya existe una cuenta con este correo electrónico.';
          } else if (message.includes('license')) {
            this.error = 'Ya existe una cuenta con esta matrícula/licencia.';
          } else {
            this.error = 'No se pudo completar el registro. Verificá los datos ingresados.';
          }
          return;
        }

        if (err?.status === 400 && /edad|under|18/.test((err.error?.message || '').toLowerCase())) {
          this.error = 'Debés ser mayor de 18 años para registrarte.';
          return;
        }

        this.error = 'Error inesperado. Intentá nuevamente más tarde.';
      }
    });
  }

  get maxBirthDate(): string {
    return DateUtils.isoDateTodayMinusYears(18);
  }

  getErrors(controlName: string): string[] {
    const control = this.form.get(controlName);
    if (!control || !control.errors) return [];
    const shouldShow = this.submitted || control.dirty || control.touched;
    if (!shouldShow) return [];

    const errors: string[] = [];
    if (control.errors['required'])     errors.push('Requerido');
    if (control.errors['email'])        errors.push('Formato inválido');
    if (control.errors['minlength'])    errors.push(`Mínimo ${control.errors['minlength'].requiredLength} caracteres`);
    if (control.errors['weakPassword']) errors.push('Debe contener mayúscula, minúscula, número y símbolo');
    if (control.errors['underAge'])     errors.push('Debés ser mayor de 18 años');
    return errors;
  }

  closeModal(): void {
    this.success = false;
    this.router.navigate([AppRoutes.Public.Root]);
  }
}
