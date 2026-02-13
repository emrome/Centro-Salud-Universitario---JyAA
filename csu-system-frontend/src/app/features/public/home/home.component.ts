import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReportService } from '@core/services/report.service';
import { Report } from '@core/models/report.model';
import {environment} from '@env/environment';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  apiBase = environment.apiUrl;
  publicReports: Report[] = [];
  loadingPublic = false;
  errorPublic: string | null = null;

  constructor(private reportService: ReportService) {}

  ngOnInit(): void {
    this.loadingPublic = true;
    this.reportService.getPublic().subscribe({
      next: (list) => {
        this.publicReports = list;
        this.loadingPublic = false;
      },
      error: () => {
        this.errorPublic = 'No se pudieron cargar los reportes públicos';
        this.loadingPublic = false;
      }
    });
  }
}
