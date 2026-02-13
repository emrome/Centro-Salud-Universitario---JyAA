import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '@core/services/auth/auth.service';
import { TokenService } from '@core/services/token.service';
import { AppRoutes } from '@shared/constants/app-routes.constants';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  form: FormGroup;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.authService.login(this.form.value).subscribe({
      next: (res) => {
        TokenService.setToken(res.token);
        const role = TokenService.getRole();

        switch (role) {
          case 'Admin':
            this.router.navigate([AppRoutes.Admin.Root]);
            break;
          case 'HealthStaff':
            this.router.navigate([AppRoutes.HealthStaff.Root]);
            break;
          case 'SocialOrgRepresentative':
            this.router.navigate([AppRoutes.Representative.Root]);
            break;
        }
      },
      error: (err) => {
        this.error = (typeof err.error === 'string' && err.error) ||
          err.error?.message ||
          err.error?.error ||
          err.message ||
          'Error inesperado al iniciar sesión';
      }
    });
  }

  passwordVisible = false;

  togglePassword(): void {
    this.passwordVisible = !this.passwordVisible;
  }

  goToRegister(): void {
    this.router.navigate([AppRoutes.Public.Register]);
  }
}

