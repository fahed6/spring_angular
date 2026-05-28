import { Component, inject, OnInit, OnDestroy,
         ElementRef, ViewChild, PLATFORM_ID } from '@angular/core';
import { NgIf } from '@angular/common';
import { isPlatformBrowser } from '@angular/common';
import { StaffService, StaffDashboardStats } from '../../core/services/staff.service';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [NgIf],
  templateUrl: './staff-dashboard.component.html',
  styleUrl: './staff-dashboard.component.css'
})
export class StaffDashboardComponent implements OnInit, OnDestroy {
  private svc        = inject(StaffService);
  private platformId = inject(PLATFORM_ID);

  @ViewChild('chartSessions') chartSessionsRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('chartPayments') chartPaymentsRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('chartTypes')    chartTypesRef!:    ElementRef<HTMLCanvasElement>;

  stats: StaffDashboardStats | null = null;
  private charts: Chart[] = [];

  ngOnInit(): void {
    this.svc.getStaffDashboard().subscribe(d => {
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
    const fontFamily  = '"Segoe UI", system-ui, sans-serif';
    const tooltipStyle = { backgroundColor: '#354A5E', titleFont: { size: 12 }, bodyFont: { size: 11 } };
    const legendStyle  = { labels: { font: { size: 11, family: fontFamily }, padding: 16, boxWidth: 12 } };
    const tickFont     = { size: 11, family: fontFamily };

    // Chart 1 — Planning by status (Doughnut)
    this.charts.push(new Chart(this.chartSessionsRef.nativeElement, {
      type: 'doughnut',
      data: {
        labels: ['Planifiées', 'Complétées', 'Annulées'],
        datasets: [{
          data: [s.scheduledSessions, s.completedSessions, s.cancelledSessions],
          backgroundColor: ['#0057D2', '#1A8E5F', '#AA0000'],
          borderWidth: 2,
          borderColor: '#fff'
        }]
      },
      options: {
        cutout: '65%',
        plugins: { legend: legendStyle, tooltip: tooltipStyle }
      }
    }));

    // Chart 2 — Payments by status (Bar)
    this.charts.push(new Chart(this.chartPaymentsRef.nativeElement, {
      type: 'bar',
      data: {
        labels: ['Payé', 'En attente', 'Partiel'],
        datasets: [{
          label: 'Paiements',
          data: [s.paidPayments, s.pendingPayments, s.partialPayments],
          backgroundColor: ['#1A8E5F', '#C75300', '#6E32C9'],
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

    // Chart 3 — Sessions by type (Doughnut)
    this.charts.push(new Chart(this.chartTypesRef.nativeElement, {
      type: 'doughnut',
      data: {
        labels: ['Code', 'Conduite'],
        datasets: [{
          data: [s.codeSessions, s.conduiteSessions],
          backgroundColor: ['#6E32C9', '#0057D2'],
          borderWidth: 2,
          borderColor: '#fff'
        }]
      },
      options: {
        cutout: '65%',
        plugins: { legend: legendStyle, tooltip: tooltipStyle }
      }
    }));
  }
}
