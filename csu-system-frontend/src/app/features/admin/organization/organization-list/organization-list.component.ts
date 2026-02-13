import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Organization } from '@core/models/organization.model';
import { OrganizationService } from '@core/services/organization.service';
import { NeighborhoodService } from '@core/services/neighborhood.service';
import { SocialOrgRepresentative } from '@core/models/users/social-org-representative.model';
import { SocialOrgRepresentativeService } from '@core/services/users/social-org-representative.service';
import { AppRoutes } from '@shared/constants/app-routes.constants';
import { MAIN_ACTIVITIES } from '@core/enums/main-activity.enum';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-organization-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './organization-list.component.html'
})
export class OrganizationListComponent implements OnInit {
  organizations: Organization[] = [];
  neighborhoods: { [key: number]: string } = {};
  representatives: { [orgId: number]: SocialOrgRepresentative[] } = {};
  representativeCounts: { [orgId: number]: number } = {};
  loadingRepresentatives: { [orgId: number]: boolean } = {};
  expandedOrganizationId: number | null = null;
  loading = true;
  error: string | null = null;

  constructor(
    private organizationService: OrganizationService,
    private neighborhoodService: NeighborhoodService,
    private representativeService: SocialOrgRepresentativeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchOrganizations();
    this.fetchNeighborhoods();
  }

  fetchOrganizations(): void {
    this.loading = true;
    this.organizationService.getAll().subscribe({
      next: data => {
        this.organizations = data;
        this.loading = false;
        this.loadRepresentativeCounts();
      },
      error: () => {
        this.error = 'Error cargando organizaciones';
        this.loading = false;
      }
    });
  }

  loadRepresentativeCounts(): void {
    const requests = this.organizations.map(org => this.representativeService.getByOrganization(org.id!));
    if (requests.length === 0) return;
    forkJoin(requests).subscribe({
      next: results => {
        results.forEach((reps, index) => {
          const orgId = this.organizations[index].id!;
          this.representativeCounts[orgId] = reps && reps.length ? reps.length : 0;
        });
      },
      error: () => {
        this.organizations.forEach(org => {
          this.representativeCounts[org.id!] = 0;
        });
      }
    });
  }

  fetchNeighborhoods(): void {
    this.neighborhoodService.getAll().subscribe({
      next: data => {
        for (const n of data) {
          if (n.id != null) this.neighborhoods[n.id] = n.name;
        }
      }
    });
  }

  toggleRepresentatives(orgId: number): void {
    if (this.expandedOrganizationId === orgId) {
      this.expandedOrganizationId = null;
      return;
    }
    this.expandedOrganizationId = orgId;
    if (!this.representatives[orgId]) {
      this.refreshRepresentatives(orgId);
    }
  }

  refreshRepresentatives(orgId: number): void {
    this.loadingRepresentatives = { ...this.loadingRepresentatives, [orgId]: true };
    this.representativeService.getByOrganization(orgId).subscribe({
      next: reps => {
        this.representatives = { ...this.representatives, [orgId]: reps ? reps : [] };
        this.representativeCounts[orgId] = reps && reps.length ? reps.length : 0;
        this.loadingRepresentatives = { ...this.loadingRepresentatives, [orgId]: false };
      },
      error: () => {
        this.loadingRepresentatives = { ...this.loadingRepresentatives, [orgId]: false };
        this.error = 'Error cargando representantes';
      }
    });
  }

  goToNewOrganization(): void {
    this.router.navigate([AppRoutes.Admin.Organizations.New]);
  }

  goToEditOrganization(id: number): void {
    this.router.navigate([AppRoutes.Admin.Organizations.Edit(id)]);
  }

  goToNewRepresentative(orgId: number): void {
    this.router.navigate([AppRoutes.Admin.Representatives.New], {
      queryParams: { organizationId: orgId }
    });
  }

  goToEditRepresentative(id: number): void {
    this.router.navigate([AppRoutes.Admin.Representatives.Edit(id)]);
  }

  deleteRepresentative(repId: number, orgId: number): void {
    if (!confirm('¿Eliminar representante?')) return;
    this.representativeService.delete(repId).subscribe({
      next: () => {
        const current = this.representatives[orgId] || [];
        const updated = current.filter(r => r.id !== repId);
        this.representatives = { ...this.representatives, [orgId]: updated };
        this.representativeCounts[orgId] = updated.length;
      },
      error: () => {
        this.error = 'Error eliminando representante';
      }
    });
  }

  deleteOrganization(id: number): void {
    if (!confirm('¿Eliminar organización?')) return;
    this.organizationService.delete(id).subscribe({
      next: () => this.fetchOrganizations(),
      error: () => {
        this.error = 'Error eliminando organización';
      }
    });
  }

  getActivityLabel(value: string | null | undefined): string {
    if (!value) return '—';
    const opt = MAIN_ACTIVITIES.find(a => a.value === value);
    return opt && opt.label ? opt.label : value;
  }

  getRepresentativeCount(orgId: number): number {
    return this.representativeCounts[orgId] !== undefined ? this.representativeCounts[orgId] : 0;
  }

  trackRep = (_: number, r: SocialOrgRepresentative) => r.id ? r.id : r.email;
}
