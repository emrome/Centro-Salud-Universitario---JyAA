import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Admin } from '@core/models/users/admin.model';
import { AdminService } from '@core/services/users/admin.service';
import { AuthService } from '@core/services/auth/auth.service';
import { AppRoutes } from '@shared/constants/app-routes.constants';


@Component({
  selector: 'app-admin-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-list.component.html'
})
export class AdminListComponent implements OnInit {
  admins: Admin[] = [];
  loading = true;
  error: string | null = null;
  paths = AppRoutes;
  private currentUserIdNum?: number;

  constructor(private adminService: AdminService, private router: Router, private auth: AuthService) {}


  ngOnInit(): void {
    const userId = this.auth.getUserId();
    const asNumber = userId !== null ? Number(userId) : undefined;
    this.currentUserIdNum = Number.isFinite(asNumber) ? (asNumber as number) : undefined;
    this.fetchAdmins();
  }

  fetchAdmins(): void {
    this.loading = true;
    this.adminService.getAll().subscribe({
      next: (data) => {Admin
        this.admins = data
          .filter(a => a.enabled)
          .filter(a => this.currentUserIdNum === undefined ? true : a.id !== this.currentUserIdNum);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error cargando administradores';
        this.loading = false;
        console.error(err);
      }
    });
  }

  goToNew(): void {
    this.router.navigate([this.paths.Admin.Admins.New]);
  }

  goToEdit(id: number): void {
    this.router.navigate([this.paths.Admin.Admins.Edit(id)]);
  }

  deleteAdmin(id: number): void {
    if (!confirm('¿Seguro que querés eliminar este administrador?')) return;

    this.adminService.delete(id).subscribe({
      next: () => this.fetchAdmins(),
      error: (err) => {
        this.error = 'Error eliminando el administrador';
        console.error(err);
      }
    });
  }
}
