import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReportRequestService } from '@core/services/report-request.service';
import { ReportRequest } from '@core/models/report-request.model';

@Component({
  selector: 'app-report-request-list-rep',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './report-request-list.component.html'
})
export class ReportRequestListRepComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  error: string | null = null;
  success: string | null = null;

  pending: ReportRequest[] = [];
  completed: ReportRequest[] = [];

  constructor(private fb: FormBuilder, private service: ReportRequestService) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      description: ['', Validators.required]
    });
    this.fetch();
  }

  fetch(): void {
    this.loading = true;
    this.error = null;
    this.service.getByRequesterMe().subscribe({
      next: (list) => {
        this.pending = list.filter(x => x.status === 'PENDING');
        this.completed = list.filter(x => x.status !== 'PENDING');
        this.loading = false;
      },
      error: () => {
        this.error = 'Error cargando solicitudes';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    const payload = { description: this.form.value.description };
    this.success = null;
    this.error = null;
    this.service.create(payload).subscribe({
      next: () => {
        this.success = 'Solicitud creada';
        this.form.reset();
        this.fetch();
      },
      error: () => {
        this.error = 'Error creando solicitud';
      }
    });
  }

  download(reportId?: number): void {
    if (!reportId) return;
    this.service.downloadCompletedReportFile(reportId).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `reporte_${reportId}.pdf`;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: () => {
        this.error = 'Error descargando archivo';
      }
    });
  }
}
