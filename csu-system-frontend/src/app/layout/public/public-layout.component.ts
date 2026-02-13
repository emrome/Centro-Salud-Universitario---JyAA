import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '@layout/common/header/header.component';
import { FooterComponent } from '@layout/common/footer/footer.component';
import { SidebarComponent } from '@layout/admin/sidebar/sidebar.component';
import { HealthStaffSidebarComponent } from '@layout/health-staff/sidebar/sidebar.component';
import { RepresentativeSidebarComponent } from '@layout/representative/sidebar/sidebar.component';
import { AuthService } from '@core/services/auth/auth.service';

@Component({
  selector: 'app-public-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    HeaderComponent,
    FooterComponent,
    SidebarComponent,
    HealthStaffSidebarComponent,
    RepresentativeSidebarComponent
  ],
  templateUrl: './public-layout.component.html',
  styleUrls: ['./../layout.component.scss']
})
export class PublicLayoutComponent {
  constructor(private router: Router, private auth: AuthService) {}

  get isAuthenticated(): boolean {
    return this.auth.isAuthenticated();
  }

  get currentUrl(): string {
    return this.router.url;
  }

  get showSidebar(): boolean {
    if (!this.isAuthenticated) return false;

    const routesWithoutSidebar = ['/login', '/register'];

    const shouldHideSidebar = routesWithoutSidebar.some(route =>
      this.currentUrl.startsWith(route)
    );

    return !shouldHideSidebar;
  }

  get role(): 'Admin' | 'HealthStaff' | 'SocialOrgRepresentative' | null {
    const r = this.auth.getUserRole();
    if (r === 'Admin' || r === 'HealthStaff' || r === 'SocialOrgRepresentative') return r;
    return null;
  }
}
