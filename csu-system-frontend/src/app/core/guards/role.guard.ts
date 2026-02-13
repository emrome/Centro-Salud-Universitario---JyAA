import { CanActivateFn, CanMatchFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenService } from '../services/token.service';
import { AppRoutes } from '@shared/constants/app-routes.constants';

export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return () => {
    const router = inject(Router);
    const role = TokenService.getRole();
    if (!role || !allowedRoles.includes(role)) {
      router.navigate([AppRoutes.Public.Root]);
      return false;
    }
    return true;
  };
};

export const roleMatch = (allowedRoles: string[]): CanMatchFn => {
  return () => {
    const role = TokenService.getRole();
    return !!role && allowedRoles.includes(role);
  };
};
