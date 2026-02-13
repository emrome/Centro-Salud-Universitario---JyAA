import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HealthStaffService } from '@core/services/users/health-staff.service';
import { HealthStaff } from '@core/models/users/health-staff.model';
import { UserFormBaseComponent } from '@features/admin/people/users/user/user-form-base/user-form-base.component';
import { AppRoutes } from '@shared/constants/app-routes.constants';
import { SPECIALTIES } from '@core/enums/specialty.enum';
import { buildUserFormGroup } from '@shared/form-builders/user-form.builder';
import { CustomValidators } from '@shared/validators/custom-validators';

@Component({
  selector: 'app-health-staff-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, UserFormBaseComponent],
  templateUrl: './health-staff-form.component.html'
})
export class HealthStaffFormComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  healthStaffId!: number;
  error: string | null = null;
  submitted = false;

  specialties = SPECIALTIES;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private healthStaffService: HealthStaffService
  ) {}

  ngOnInit(): void {
    this.form = buildUserFormGroup(this.fb);

    this.form.addControl('specialty', this.fb.control(null, [Validators.required]));
    this.form.addControl('license', this.fb.control('', [Validators.required]));

    const idParam = this.route.snapshot.paramMap.get('id');
    this.isEdit = !!idParam;

    const pwd = this.form.get('password');
    if (this.isEdit) {
      this.healthStaffId = +idParam!;
      pwd?.clearValidators();
      pwd?.setValue('');
      pwd?.updateValueAndValidity({ emitEvent: false });
      this.loadUser();
    } else {
      pwd?.setValidators([
        Validators.required,
        Validators.minLength(8),
        CustomValidators.strongPassword()
      ]);
      pwd?.updateValueAndValidity({ emitEvent: false });
    }
  }

  loadUser(): void {
    this.healthStaffService.getById(this.healthStaffId).subscribe({
      next: (data: HealthStaff) => {
        this.form.patchValue(data);
      },
      error: () => {
        this.error = 'Error al cargar el personal de salud';
      }
    });
  }

  onSubmit(): void {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const dto: HealthStaff = {
      ...this.form.value,
      birthDate: this.formatDate(this.form.value.birthDate),
      registrationDate: this.formatDate(this.form.value.registrationDate)
    };

    if (this.isEdit && !dto.password) {
      delete (dto as any).password;
    }

    const request$ = this.isEdit
      ? this.healthStaffService.update(this.healthStaffId, dto)
      : this.healthStaffService.create(dto);

    request$.subscribe({
      next: () => this.router.navigate([AppRoutes.Admin.HealthStaff.List]),
      error: (err) => {
        if (err.status === 409) {
          const message = (typeof err.error === 'string' ? err.error : err.error?.message || '').toLowerCase();
          if (message.includes('email'))   this.error = 'El email ya está en uso';
          else if (message.includes('license')) this.error = 'La matrícula ya está en uso';
          else this.error = 'Conflicto al guardar los datos';
        } else {
          this.error = 'Error al guardar';
        }
      }
    });
  }

  cancel(): void {
    this.router.navigate([AppRoutes.Admin.HealthStaff.List]);
  }

  private formatDate(date: string | Date): string | null {
    if (!date) return null;
    const d = new Date(date);
    return d.toISOString().split('T')[0]; // YYYY-MM-DD
  }

  fieldInvalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.submitted);
  }
  getError(name: string): string {
    const c = this.form.get(name);
    if (!c || !c.errors) return '';
    if (c.errors['required']) return 'Este campo es obligatorio';
    return 'Dato inválido';
  }
}
