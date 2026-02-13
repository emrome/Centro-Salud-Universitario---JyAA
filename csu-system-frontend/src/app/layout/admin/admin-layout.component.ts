import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '@layout/common/header/header.component';
import { SidebarComponent } from '@layout/admin/sidebar/sidebar.component';
import { FooterComponent } from '@layout/common/footer/footer.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, HeaderComponent, SidebarComponent, FooterComponent],
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./../layout.component.scss']
})
export class AdminLayoutComponent {}
