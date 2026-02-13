import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin, Observable } from 'rxjs';
import { SPECIALTIES } from '@core/enums/specialty.enum';


import { Admin } from '@core/models/users/admin.model';
import { HealthStaff } from '@core/models/users/health-staff.model';
import { SocialOrgRepresentative } from '@core/models/users/social-org-representative.model';

import { AdminService } from '@core/services/users/admin.service';
import { HealthStaffService } from '@core/services/users/health-staff.service';
import { SocialOrgRepresentativeService } from '@core/services/users/social-org-representative.service';

type UserType = 'ADMIN' | 'HEALTH' | 'REP';

interface Row {
  id: number;
  type: UserType;
  firstName: string;
  lastName: string;
  email: string;
  dto: any;
  organizationName?: string | null;
  specialty?: string | null;
  license?: string | null;
  positionInCSU?: string | null;
}

@Component({
  selector: 'app-user-enable-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './user-enable-list.component.html'
})
export class UserEnableListComponent implements OnInit {
  loading = true;
  saving: { [id: string]: boolean } = {};
  errorMessage: string | null = null;
  rows: Row[] = [];

  constructor(
    private adminService: AdminService,
    private healthService: HealthStaffService,
    private repService: SocialOrgRepresentativeService
  ) {}

  ngOnInit(): void {
    this.fetchAll();
  }

  fetchAll(): void {
    this.loading = true;
    forkJoin({
      admins: this.adminService.getAll(),
      health: this.healthService.getAll(),
      reps: this.repService.getAll()
    }).subscribe({
      next: ({ admins, health, reps }) => {
        const a = (admins || []).filter(u => !u.enabled && !u.deleted).map<Row>(u => ({
          id: u.id!, type: 'ADMIN', firstName: u.firstName, lastName: u.lastName, email: u.email, dto: u, positionInCSU: u.positionInCSU ?? null
        }));
        const h = (health || []).filter(u => !u.enabled && !u.deleted).map<Row>(u => ({
          id: u.id!, type: 'HEALTH', firstName: u.firstName, lastName: u.lastName, email: u.email, dto: u,
          specialty: u.specialty ?? null, license: u.license ?? null
        }));
        const r = (reps || []).filter(u => !u.enabled && !u.deleted).map<Row>(u => ({
          id: u.id!, type: 'REP', firstName: u.firstName, lastName: u.lastName, email: u.email, dto: u,
          organizationName: u.organizationName ?? null
        }));
        this.rows = [...a, ...h, ...r].sort((x, y) => x.lastName.localeCompare(y.lastName));
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Error cargando usuarios pendientes';
        this.loading = false;
      }
    });
  }

  enable(row: Row): void {
    this.saving[this.rowKey(0, row)] = true;
    const dto = { ...row.dto, enabled: true };

    let req$: Observable<any>;
    if (row.type === 'ADMIN') req$ = this.adminService.update(row.id, dto as Admin);
    else if (row.type === 'HEALTH') req$ = this.healthService.update(row.id, dto as HealthStaff);
    else req$ = this.repService.update(row.id, dto as SocialOrgRepresentative);

    req$.subscribe({
      next: () => {
        this.rows = this.rows.filter(r => r.id !== row.id || r.type !== row.type);
        this.saving[this.rowKey(0, row)] = false;
      },
      error: () => {
        this.errorMessage = 'Error habilitando usuario';
        this.saving[this.rowKey(0, row)] = false;
      }
    });
  }

  delete(row: Row): void {
    if (!confirm('¿Eliminar este usuario?')) return;
    this.saving[this.rowKey(0, row)] = true;

    let req$: Observable<any>;
    if (row.type === 'ADMIN') req$ = this.adminService.delete(row.id);
    else if (row.type === 'HEALTH') req$ = this.healthService.delete(row.id);
    else req$ = this.repService.delete(row.id);

    req$.subscribe({
      next: () => {
        this.rows = this.rows.filter(r => r.id !== row.id || r.type !== row.type);
        this.saving[this.rowKey(0, row)] = false;
      },
      error: () => {
        this.errorMessage = 'Error eliminando usuario';
        this.saving[this.rowKey(0, row)] = false;
      }
    });
  }

  getSpecialtyLabel(value: string | null | undefined): string {
    if (!value) return '—';
    const opt = SPECIALTIES.find(s => s.value === value);
    return opt ? opt.label : value;
  }

  rowKey = (_index: number, r: Row): string => `${r.type}_${r.id}`;
}
