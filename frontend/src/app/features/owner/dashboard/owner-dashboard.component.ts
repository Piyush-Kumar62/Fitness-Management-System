import { Component, OnInit, OnDestroy, inject, signal, ViewChild, ElementRef } from '@angular/core';

import { RouterLink } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard.service';
import { ChartService } from '../../../core/services/chart.service';
import type { Chart } from 'chart.js';

@Component({
  selector: 'app-owner-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './owner-dashboard.component.html',
})
export class OwnerDashboardComponent implements OnInit, OnDestroy {
  private dashboardService = inject(DashboardService);
  private chartService = inject(ChartService);

  @ViewChild('revenueCanvas') revenueCanvas?: ElementRef<HTMLCanvasElement>;
  @ViewChild('memberCanvas') memberCanvas?: ElementRef<HTMLCanvasElement>;

  private revenueChart?: Chart;
  private memberChart?: Chart;

  metrics = signal([
    { label: 'Total Members', value: '248' },
    { label: 'Active Memberships', value: '191' },
    { label: 'Trainers', value: '12' },
    { label: 'Today Classes', value: '8' },
    { label: 'Monthly Revenue', value: '$18.4K' },
  ]);

  ngOnInit(): void {
    this.dashboardService.getOwnerStats().subscribe((stats) => {
      this.metrics.set([
        { label: 'Total Members', value: String(stats.totalMembers ?? 0) },
        { label: 'Active Memberships', value: String(stats.activeMemberships ?? 0) },
        { label: 'Trainers', value: String(stats.trainers ?? 0) },
        { label: 'Today Classes', value: String(stats.todayClasses ?? 0) },
        { label: 'Monthly Revenue', value: this.asInr(stats.monthlyRevenue ?? 0) },
      ]);
      setTimeout(() => {
        this.initCharts(stats);
      }, 100);
    });
  }

  ngOnDestroy(): void {
    this.chartService.destroy(this.revenueChart);
    this.chartService.destroy(this.memberChart);
  }

  private initCharts(stats: any): void {
    if (this.revenueCanvas) {
      this.chartService.destroy(this.revenueChart);
      // Mocking past 6 months revenue for visualization
      const currentRev = stats.monthlyRevenue ?? 0;
      const data = [
        currentRev * 0.5, currentRev * 0.6, currentRev * 0.8,
        currentRev * 0.9, currentRev * 0.95, currentRev
      ];
      const months = this.getLast6Months();
      this.revenueChart = this.chartService.createLineChart(
        this.revenueCanvas.nativeElement,
        months,
        [{ label: 'Revenue (INR)', data: data, color: '#10b981' }]
      );
    }

    if (this.memberCanvas) {
      this.chartService.destroy(this.memberChart);
      const active = stats.activeMemberships ?? 0;
      const inactive = Math.max((stats.totalMembers ?? 0) - active, 0);
      this.memberChart = this.chartService.createDoughnutChart(
        this.memberCanvas.nativeElement,
        ['Active', 'Inactive'],
        [active, inactive],
        ['#6366f1', '#475569']
      );
    }
  }

  private getLast6Months(): string[] {
    const months: string[] = [];
    const now = new Date();
    for (let i = 5; i >= 0; i--) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
      months.push(d.toLocaleDateString('en-US', { month: 'short' }));
    }
    return months;
  }

  private asInr(value: number): string {
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(value);
  }
}
