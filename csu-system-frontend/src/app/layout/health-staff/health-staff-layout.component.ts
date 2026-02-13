import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from '@layout/common/header/header.component';
import { FooterComponent } from '@layout/common/footer/footer.component';
import { HealthStaffSidebarComponent } from '@layout/health-staff/sidebar/sidebar.component';

@Component({
  selector: 'app-health-staff-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, HeaderComponent, FooterComponent, HealthStaffSidebarComponent],
  templateUrl: './health-staff-layout.component.html',
  styleUrls: ['./../layout.component.scss'],
})
export class HealthStaffLayoutComponent {}
