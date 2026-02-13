import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AppRoutes } from '@shared/constants/app-routes.constants';
@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss',
})
export class SidebarComponent {
  paths = AppRoutes;
  private usersExpanded = false;
  currentUrl = '';

  constructor(private router: Router) {
    this.router.events.subscribe(() => {
      this.currentUrl = this.router.url;
    });
  }

  toggleUsers(): void {
    this.usersExpanded = !this.usersExpanded;
  }

  isUserSubRoute(): boolean {
    return [
      this.paths.Admin.Admins.List,
      this.paths.Admin.HealthStaff.List,
      this.paths.Admin.Representatives.List,
    ].some(path => this.currentUrl.startsWith(path));
  }

  isUsersExpanded(): boolean {
    return this.usersExpanded;
  }

  isUsersActive(): boolean {
    return this.isUserSubRoute();
  }
}

