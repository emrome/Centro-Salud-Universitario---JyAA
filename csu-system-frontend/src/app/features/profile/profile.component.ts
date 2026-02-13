import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap, tap, finalize, catchError, of } from 'rxjs';

import { ProfileService } from '@core/services/auth/profile.service';
import { AuthService } from '@core/services/auth/auth.service';
import { CustomValidators } from '@shared/validators/custom-validators';
import { DateUtils } from '@shared/utils/date-utils';
import { AppRoutes } from '@shared/constants/app-routes.constants';
import { SPECIALTIES } from '@core/enums/specialty.enum';

type Role = 'ADMIN'|'HEALTHSTAFF'|'REPRESENTATIVE';

@Component({
  standalone: true,
  selector: 'app-profile',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  role!: Role;
  profileForm!: FormGroup;
  passwordForm!: FormGroup;
  loading = true;
  saving = false;
  savingPwd = false;
  error: string | null = null;
  pwdError: string | null = null;
  pwdSuccess = false;
  success = false;
  showOldPwd = false;
  showNewPwd = false;
  showConfirmPwd = false;
  healthLicense?: string;
  healthSpecialty?: string;
  orgName?: string;
  specialties = SPECIALTIES;

  private returnUrl: string | null = null;
  private roleHome: Record<Role, string> = {
    ADMIN: '/admin',
    HEALTHSTAFF: '/health',
    REPRESENTATIVE: '/representatives'
  };

  constructor(
    private fb: FormBuilder,
    private profile: ProfileService,
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');

    this.passwordForm = this.fb.group(
      {
        oldPassword: ['', Validators.required],
        newPassword: ['',
          [
            Validators.required,
            Validators.minLength(8),
            CustomValidators.strongPassword(),
            (c: any) => (c.value && c.value.trim() === c.value) ? null : { leadingTrailingSpace: true }
          ],
        ],
        confirmNewPassword: ['', Validators.required]
      },
      {
        validators: [
          this.matchPasswords('newPassword', 'confirmNewPassword'),
          this.notEqual('oldPassword', 'newPassword')
        ]
    });

    this.passwordForm.get('newPassword')?.valueChanges.subscribe(() => {
      this.passwordForm.get('confirmNewPassword')?.updateValueAndValidity({ onlySelf: true });
    });

    this.profile.getMe<any>().subscribe({
      next: res => {
        this.role = res.type;
        if (this.role === 'HEALTHSTAFF') {
          this.healthLicense = res.data?.license ?? '';
          this.healthSpecialty = res.data?.specialty ?? '';
        } else if (this.role === 'REPRESENTATIVE') {
          this.orgName = res.data?.organizationName ?? '';
        }
        this.buildProfileForm(res.data);
        this.loading = false;
      },
      error: _ => { this.error = 'No se pudo cargar el perfil'; this.loading = false; }
    });
  }

  private buildProfileForm(data: any) {
    const base = {
      firstName: [data.firstName || '', [Validators.required, Validators.minLength(2)]],
      lastName:  [data.lastName  || '', [Validators.required, Validators.minLength(2)]],
      birthDate: [data.birthDate || '', [Validators.required, CustomValidators.minAge(18)]],
      email:     [{ value: data.email, disabled: true }],
      registrationDate: [{ value: data.registrationDate, disabled: true }],
      enabled:   [{ value: data.enabled, disabled: true }],
      id:        [data.id]
    };

    if (this.role === 'ADMIN') {
      this.profileForm = this.fb.group({
        ...base,
        positionInCSU: [data.positionInCSU || '', [Validators.required, Validators.minLength(2)]]
      });
    } else if (this.role === 'HEALTHSTAFF') {
      const specialtyLabel = this.specialties.find(s => s.value === data.specialty)?.label ?? data.specialty;
      this.profileForm = this.fb.group({
        ...base,
        license:   [{ value: data.license ?? '', disabled: true }],
        specialty: [{ value: specialtyLabel ?? '', disabled: true }]
      });
    } else if (this.role === 'REPRESENTATIVE') {
      this.profileForm = this.fb.group({
        ...base,
        organizationName: [{ value: data.organizationName ?? '', disabled: true }]
      });
    } else {
      this.profileForm = this.fb.group(base);
    }
  }

  submitProfile(): void {
    if (this.profileForm.invalid) { this.profileForm.markAllAsTouched(); return; }
    this.saving = true; this.error = null; this.success = false;

    const payload = { ...this.profileForm.getRawValue() };

    this.profile.updateMe<any>(payload).pipe(
      switchMap(() => this.auth.loadCurrentUser()),
      tap(() => { this.success = true; }),
      catchError(err => { this.error = 'No se pudo guardar el perfil'; return of(null); }),
      finalize(() => { this.saving = false; })
    ).subscribe({
        next: () => {
          const back = this.safeReturn(this.returnUrl);
          if (back) {
            this.router.navigateByUrl(back);
            return;
          }

          switch (this.role) {
            case 'ADMIN':
              this.router.navigateByUrl(AppRoutes.Admin.Root)
              break;
            case 'HEALTHSTAFF':
              this.router.navigateByUrl(AppRoutes.HealthStaff.Root)
              break;
            case 'REPRESENTATIVE':
              this.router.navigateByUrl(AppRoutes.Representative.Root)
              break;
            default:
              this.router.navigateByUrl('/');                // fallback
              break;
          }
        }
      });
  }

  get maxBirthDate(): string {
    return DateUtils.isoDateTodayMinusYears(18);
  }

  submitPassword(): void {
    this.pwdError = null;
    this.pwdSuccess = false;

    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();

      const gErr = this.passwordForm.errors || {};
      if (gErr['passwordMismatch']) {
        this.pwdError = 'Las nuevas contraseñas no coinciden';
      } else if (gErr['sameAsOld']) {
        this.pwdError = 'La nueva contraseña debe ser distinta a la actual';
      } else if (this.passwordForm.get('oldPassword')?.hasError('required')) {
        this.pwdError = 'Ingresá la contraseña actual';
      } else if (this.passwordForm.get('newPassword')?.hasError('required')) {
        this.pwdError = 'Ingresá la nueva contraseña';
      } else if (this.passwordForm.get('newPassword')?.hasError('minlength')) {
        this.pwdError = 'La nueva contraseña debe tener al menos 8 caracteres';
      } else if (this.passwordForm.get('newPassword')?.hasError('weakPassword')) {
        this.pwdError = 'Debe incluir mayúscula, minúscula, número y símbolo';
      } else if (this.passwordForm.get('confirmNewPassword')?.hasError('required')) {
        this.pwdError = 'Confirmá la nueva contraseña';
      } else {
        this.pwdError = 'Revisá los campos';
      }
      return;
    }

    const { oldPassword, newPassword, confirmNewPassword } = this.passwordForm.value;
    this.savingPwd = true;

    this.profile.changePassword({ oldPassword, newPassword, confirmNewPassword }).subscribe({
      next: _ => {
        this.savingPwd = false;
        this.pwdSuccess = true;
        this.passwordForm.reset();
        this.showOldPwd = this.showNewPwd = this.showConfirmPwd = false;
      },
      error: err => {
        this.savingPwd = false;
        if (err?.status === 401) {
          this.pwdError = 'La contraseña actual es incorrecta';
        } else if (err?.status === 400) {
          const msg = (err.error?.message || '').toLowerCase();
          if (msg.includes('weak')) this.pwdError = 'La nueva contraseña no cumple los requisitos';
          else this.pwdError = err.error?.message || 'Datos inválidos';
        } else {
          this.pwdError = 'Error al cambiar la contraseña';
        }
      }
    });
  }

  private matchPasswords(a: string, b: string) {
    return (fg: FormGroup) => {
      const v1 = fg.get(a)?.value, v2 = fg.get(b)?.value;
      if (!v1 || !v2) return null;
      return v1 === v2 ? null : { passwordMismatch: true };
    };
  }

  private notEqual(a: string, b: string) {
    return (fg: FormGroup) => {
      const v1 = fg.get(a)?.value, v2 = fg.get(b)?.value;
      if (!v1 || !v2) return null;
      return v1 === v2 ? { sameAsOld: true } : null;
    };
  }

  private safeReturn(url: string | null): string | null {
    if (!url) return null;
    try {
      const u = new URL(url, window.location.origin);
      const sameOrigin = u.origin === window.location.origin;
      const internal = u.pathname.startsWith('/');
      return (sameOrigin && internal) ? (u.pathname + u.search + u.hash) : null;
    } catch { return null; }
  }
}
