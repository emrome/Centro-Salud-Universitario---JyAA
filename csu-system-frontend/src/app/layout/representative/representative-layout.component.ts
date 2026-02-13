import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from '@layout/common/header/header.component';
import { FooterComponent } from '@layout/common/footer/footer.component';
import { RepresentativeSidebarComponent } from '@layout/representative/sidebar/sidebar.component';

@Component({
  selector: 'app-representative-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, HeaderComponent, FooterComponent, RepresentativeSidebarComponent],
  templateUrl: './representative-layout.component.html',
  styleUrls: ['./../layout.component.scss']
})
export class RepresentativeLayoutComponent {}
