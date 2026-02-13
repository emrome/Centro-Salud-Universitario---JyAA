import { CanMatchFn } from '@angular/router';
import { TokenService } from '../services/token.service';

export const matchAdmin: CanMatchFn = () => TokenService.getRole() === 'ADMIN';

export const matchHealth: CanMatchFn = () => TokenService.getRole() === 'HEALTHSTAFF';

export const matchRepresentative: CanMatchFn = () => TokenService.getRole() === 'REPRESENTATIVE';
