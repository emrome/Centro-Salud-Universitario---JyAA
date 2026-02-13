import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

import { SocialOrgRepresentative } from '@core/models/users/social-org-representative.model';
import { SocialOrgRepresentativeService } from '@core/services/users/social-org-representative.service';
import { OrganizationService } from '@core/services/organization.service';
import { Organization } from '@core/models/organization.model';

import { UserFormBaseComponent } from '@features/admin/people/users/user/user-form-base/user-form-base.component';
import { AppRoutes } from '@shared/constants/app-routes.constants';
import { buildUserFormGroup } from '@shared/form-builders/user-form.builder';
import { CustomValidators } from '@shared/validators/custom-validators';

@Component({
  selector: 'app-social-org-representative-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, UserFormBaseComponent],
  templateUrl: './social-org-representative-form.component.html'
})
export class SocialOrgRepresentativeFormComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  representativeId!: number;
  error: string | null = null;
  submitted = false;
  paths = AppRoutes;

  organizations: Organization[] = [];
  loadingOrganizations = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private representativeService: SocialOrgRepresentativeService,
    private organizationService: OrganizationService
  ) {}

  ngOnInit(): void {
    this.form = buildUserFormGroup(this.fb);
    this.form.addControl('organizationId', this.fb.control<number | null>(null, [Validators.required]));

    const idParam = this.route.snapshot.paramMap.get('id');
    this.isEdit = !!idParam;

    const pwd = this.form.get('password');

    if (this.isEdit) {
      this.representativeId = +idParam!;
      pwd?.clearValidators();
      pwd?.setValue('');
      pwd?.updateValueAndValidity({ emitEvent: false });

      this.loadRepresentative();
    } else {
      pwd?.setValidators([Validators.required, Validators.minLength(8), CustomValidators.strongPassword()]);
      pwd?.updateValueAndValidity({ emitEvent: false });

      const qpOrgId = this.route.snapshot.queryParamMap.get('organizationId');
      if (qpOrgId) this.form.patchValue({ organizationId: +qpOrgId });
    }

    this.loadOrganizations();
  }

  private loadOrganizations(): void {
    this.loadingOrganizations = true;
    this.organizationService.getAll().subscribe({
      next: (data: Organization[]) => {
        this.organizations = data.slice().sort((a, b) => a.name.localeCompare(b.name));
        this.loadingOrganizations = false;
      },
      error: () => {
        this.loadingOrganizations = false;
        this.error = 'Error cargando organizaciones';
      }
    });
  }

  private loadRepresentative(): void {
    this.representativeService.getById(this.representativeId).subscribe({
      next: (data: SocialOrgRepresentative) => {
        this.form.patchValue({
          firstName: data.firstName,
          lastName: data.lastName,
          birthDate: data.birthDate,
          email: data.email,
          password: '', // no rellenar en edición
          registrationDate: data.registrationDate,
          enabled: data.enabled,
          organizationId: data.organizationId ?? null
        });
      },
      error: () => {
        this.error = 'Error al cargar el representante';
      }
    });
  }

  onSubmit(): void {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.value as any;
    const dto: SocialOrgRepresentative = {
      ...raw,
      organizationId: raw.organizationId != null ? Number(raw.organizationId) : null
    };

    if (this.isEdit && !dto.password) {
      delete (dto as any).password;
    }

    const request$ = this.isEdit
      ? this.representativeService.update(this.representativeId, dto)
      : this.representativeService.create(dto);

    request$.subscribe({
      next: () => this.router.navigate([this.paths.Admin.Representatives.List]),
      error: (err) => {
        if (err.status === 409)       this.error = 'El email ya está en uso';
        else if (err.status === 404)  this.error = 'Organización no encontrada';
        else                          this.error = 'Error al guardar';
      }
    });
  }

  cancel(): void {
    this.router.navigate([this.paths.Admin.Representatives.List]);
  }

  fieldInvalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.submitted);
  }

  getError(name: string): string {
    const c = this.form.get(name);
    if (!c || !c.errors) return '';
    if (c.errors['required'])   return 'Este campo es obligatorio';
    if (c.errors['email'])      return 'Email inválido';
    if (c.errors['minlength'])  return `Mínimo ${c.errors['minlength'].requiredLength} caracteres`;
    if (c.errors['weakPassword']) return 'Debe incluir mayúscula, minúscula y número';
    if (c.errors['futureDate']) return 'La fecha no puede ser en el futuro';
    return 'Dato inválido';
  }
}
