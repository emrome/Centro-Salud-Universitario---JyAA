import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

import { Admin } from '@core/models/users/admin.model';
import { AdminService } from '@core/services/users/admin.service';
import { UserFormBaseComponent } from '@features/admin/people/users/user/user-form-base/user-form-base.component';
import { AppRoutes } from '@shared/constants/app-routes.constants';
import { CustomValidators } from '@shared/validators/custom-validators';
import { buildUserFormGroup } from '@shared/form-builders/user-form.builder';

@Component({
  selector: 'app-admin-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, UserFormBaseComponent],
  templateUrl: './admin-form.component.html'
})
export class AdminFormComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  adminId!: number;
  error: string | null = null;
  paths = AppRoutes;
  submitted = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private adminService: AdminService
  ) {}

  ngOnInit(): void {
    // Base compartida (no tocamos lo de tu compa)
    this.form = buildUserFormGroup(this.fb);

    // Campo específico de Admin
    this.form.addControl(
      'positionInCSU',
      this.fb.control('', [Validators.required, Validators.minLength(2)]) // <- opcional minLength
    );

    const idParam = this.route.snapshot.paramMap.get('id');
    this.isEdit = !!idParam;

    const pwd = this.form.get('password');

    if (this.isEdit) {
      this.adminId = +idParam!;
      pwd?.clearValidators();
      pwd?.setValue('');
      pwd?.updateValueAndValidity({ emitEvent: false });

      this.loadAdmin();
    } else {
      pwd?.setValidators([
        Validators.required,
        Validators.minLength(8),
        CustomValidators.strongPassword()
      ]);
      pwd?.updateValueAndValidity({ emitEvent: false });
    }
  }

  private loadAdmin(): void {
    this.adminService.getById(this.adminId).subscribe({
      next: (data: Admin) => {
        this.form.patchValue({
          firstName: data.firstName,
          lastName: data.lastName,
          birthDate: data.birthDate,
          email: data.email,
          password: '', // no rellenar en edición
          registrationDate: data.registrationDate,
          enabled: data.enabled,
          positionInCSU: data.positionInCSU
        });
      },
      error: () => {
        this.error = 'Error al cargar el administrador';
      }
    });
  }

  onSubmit(): void {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const dto: Admin = { ...this.form.value };
    if (this.isEdit && !dto.password) {
      delete (dto as any).password;
    }

    const request$ = this.isEdit
      ? this.adminService.update(this.adminId, dto)
      : this.adminService.create(dto);

    request$.subscribe({
      next: () => this.router.navigate([this.paths.Admin.Admins.List]),
      error: (err) => {
        if (err.status === 409)      this.error = 'El email ya está en uso';
        else if (err.status === 404) this.error = 'Administrador no encontrado';
        else                         this.error = 'Error al guardar';
      }
    });
  }

  cancel(): void {
    this.router.navigate([this.paths.Admin.Admins.List]);
  }

  fieldInvalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.submitted);
  }

  getError(name: string): string {
    const c = this.form.get(name);
    if (!c || !c.errors) return '';
    if (c.errors['required'])   return 'Este campo es obligatorio';
    if (c.errors['minlength'])  return `Mínimo ${c.errors['minlength'].requiredLength} caracteres`;
    return 'Dato inválido';
  }
}
