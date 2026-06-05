import { Component, inject, OnInit, OnDestroy, AfterViewInit,
         ElementRef, ViewChild, PLATFORM_ID } from '@angular/core';
import { NgIf } from '@angular/common';
import { isPlatformBrowser } from '@angular/common';
import { AdminService } from '../../services/admin.service';
import { DashboardStats } from '../../models/dashboard.model';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [NgIf],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit, OnDestroy {
  private svc        = inject(AdminService);
  private platformId = inject(PLATFORM_ID);

  @ViewChild('chartStatus')    chartStatusRef!:    ElementRef<HTMLCanvasElement>;
  @ViewChild('chartEnrollment')chartEnrollmentRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('chartPayments')  chartPaymentsRef!:   ElementRef<HTMLCanvasElement>;

  stats: DashboardStats | null = null;
  currentMonth = new Date().toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' });

  private charts: Chart[] = [];

  ngOnInit(): void {
    this.svc.getDashboard().subscribe(d => {
      this.stats = d;
      if (isPlatformBrowser(this.platformId)) {
        setTimeout(() => this.renderCharts(), 0);
      }
    });
  }

  ngOnDestroy(): void {
    this.charts.forEach(c => c.destroy());
  }

  private renderCharts(): void {
    if (!this.stats) return;
    this.charts.forEach(c => c.destroy());
    this.charts = [];

    const s = this.stats;
    const fontFamily = '"Segoe UI", system-ui, sans-serif';
    const tooltipStyle = { backgroundColor: '#354A5E', titleFont: { size: 12 }, bodyFont: { size: 11 } };
    const legendStyle  = { labels: { font: { size: 11, family: fontFamily }, padding: 16, boxWidth: 12 } };
    const tickFont     = { size: 11, family: fontFamily };

    // Chart 1 — Students by status (Doughnut)
    this.charts.push(new Chart(this.chartStatusRef.nativeElement, {
      type: 'doughnut',
      data: {
        labels: ['Actifs', 'Diplômés', 'Suspendus'],
        datasets: [{
          data: [s.activeStudents, s.completedStudents, s.suspendedStudents],
          backgroundColor: ['#1A8E5F', '#0057D2', '#AA0000'],
          borderWidth: 2,
          borderColor: '#fff'
        }]
      },
      options: {
        cutout: '65%',
        plugins: { legend: legendStyle, tooltip: tooltipStyle }
      }
    }));

    // Chart 2 — Monthly enrollments (Bar)
    this.charts.push(new Chart(this.chartEnrollmentRef.nativeElement, {
      type: 'bar',
      data: {
        labels: s.enrollmentLabels,
        datasets: [{
          label: 'Inscriptions',
          data: s.enrollmentData,
          backgroundColor: '#0057D2',
          borderRadius: 2
        }]
      },
      options: {
        plugins: { legend: { display: false }, tooltip: tooltipStyle },
        scales: {
          x: { grid: { display: false }, ticks: { font: tickFont } },
          y: { grid: { color: '#EEEEEE' }, ticks: { font: tickFont, stepSize: 1 }, beginAtZero: true }
        }
      }
    }));

    // Chart 3 — Planning sessions by status (Horizontal bar)
    this.charts.push(new Chart(this.chartPaymentsRef.nativeElement, {
      type: 'bar',
      data: {
        labels: ['Planifiées', 'Complétées', 'Annulées'],
        datasets: [{
          label: 'Séances',
          data: [s.scheduledSessions, s.completedSessions, s.cancelledSessions],
          backgroundColor: ['#0057D2', '#1A8E5F', '#AA0000'],
          borderRadius: 2
        }]
      },
      options: {
        indexAxis: 'y',
        plugins: { legend: { display: false }, tooltip: tooltipStyle },
        scales: {
          x: { grid: { color: '#EEEEEE' }, ticks: { font: tickFont, stepSize: 1 }, beginAtZero: true },
          y: { grid: { display: false }, ticks: { font: tickFont } }
        }
      }
    }));
  }
}
