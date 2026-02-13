import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReportRequestService } from '@core/services/report-request.service';
import { ReportRequest } from '@core/models/report-request.model';

type Tab = 'pending' | 'resolved' | 'rejected';

@Component({
  selector: 'app-health-report-request-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './report-request-list.component.html',
  styleUrls: ['./report-request-list.component.scss']
})
export class HealthReportRequestListComponent implements OnInit {
  currentTab: Tab = 'pending';

  pending: ReportRequest[] = [];
  resolved: ReportRequest[] = [];
  rejected: ReportRequest[] = [];

  loading = false;
  error: string | null = null;
  success: string | null = null;

  completeForm!: FormGroup<{
    name: FormControl<string>;
    description: FormControl<string>;
    visibleToAllHealthStaff: FormControl<boolean>;
    isPublic: FormControl<boolean>;
  }>;

  fileMap: { [reqId: number]: File | null } = {};

  constructor(private service: ReportRequestService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.completeForm = this.fb.group({
      name: this.fb.control('', { nonNullable: true, validators: [Validators.required] }),
      description: this.fb.control('', { nonNullable: true, validators: [Validators.required] }),
      visibleToAllHealthStaff: this.fb.control(true, { nonNullable: true }),
      isPublic: this.fb.control(false, { nonNullable: true }),
    });
    this.fetchPending();
  }

  setTab(tab: Tab): void {
    this.currentTab = tab;
    if (tab === 'pending' && this.pending.length === 0) this.fetchPending();
    if (tab === 'resolved' && this.resolved.length === 0) this.fetchResolved();
    if (tab === 'rejected' && this.rejected.length === 0) this.fetchRejected();
  }

  fetchPending(): void {
    this.loading = true; this.error = null;
    this.service.getPendingForHealth().subscribe({
      next: list => { this.pending = list; this.loading = false; },
      error: () => { this.error = 'Error cargando pendientes'; this.loading = false; }
    });
  }

  fetchResolved(): void {
    this.loading = true; this.error = null;
    this.service.getResolvedForHealth().subscribe({
      next: list => { this.resolved = list; this.loading = false; },
      error: () => { this.error = 'Error cargando resueltas'; this.loading = false; }
    });
  }

  fetchRejected(): void {
    this.loading = true; this.error = null;
    this.service.getRejectedForHealth().subscribe({
      next: list => { this.rejected = list; this.loading = false; },
      error: () => { this.error = 'Error cargando rechazadas'; this.loading = false; }
    });
  }

  onPickFile(e: Event, reqId: number): void {
    const input = e.target as HTMLInputElement;
    const file = input.files && input.files.length ? input.files[0] : null;
    this.fileMap[reqId] = file;
  }

  complete(req: ReportRequest): void {
    const file = this.fileMap[req.id!];
    if (!file) { this.error = 'Seleccioná un archivo'; return; }
    if (this.completeForm.invalid) { this.error = 'Completá nombre y descripción'; return; }
    this.success = null; this.error = null;

    this.service.completeWithReport(req.id!, {
      name: this.completeForm.controls.name.value,
      description: this.completeForm.controls.description.value,
      visibleToAllHealthStaff: this.completeForm.controls.visibleToAllHealthStaff.value,
      isPublic: this.completeForm.controls.isPublic.value,
      file
    }).subscribe({
      next: () => {
        this.success = `Solicitud #${req.id} completada`;
        this.fileMap[req.id!] = null;
        this.completeForm.reset({ name: '', description: '', visibleToAllHealthStaff: true, isPublic: false });
        this.fetchPending();
      },
      error: () => { this.error = 'Error completando la solicitud'; }
    });
  }

  reject(req: ReportRequest): void {
    const reason = prompt('Motivo del rechazo:') || undefined;
    this.success = null; this.error = null;
    this.service.reject(req.id!, reason).subscribe({
      next: () => { this.success = `Solicitud #${req.id} rechazada`; this.fetchPending(); },
      error: () => { this.error = 'Error rechazando la solicitud'; }
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
      error: () => { this.error = 'Error descargando archivo'; }
    });
  }

  trackByRequestId = (_: number, item: ReportRequest) => item.id ?? _;
}
