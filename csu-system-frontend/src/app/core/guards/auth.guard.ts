import { CanActivateFn, CanMatchFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenService } from '../services/token.service';
import { AppRoutes } from '@shared/constants/app-routes.constants';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  if (!TokenService.isAuthenticated()) {
    router.navigate([AppRoutes.Public.Login]);
    return false;
  }
  return true;
};

export const authMatch: CanMatchFn = () => {
  return TokenService.isAuthenticated();
};
