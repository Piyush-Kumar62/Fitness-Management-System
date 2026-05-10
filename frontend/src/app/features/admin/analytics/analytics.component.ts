import { Component, OnInit, inject, signal } from '@angular/core';

import { DashboardService } from '../../../core/services/dashboard.service';
import { ToastService } from '../../../core/services/toast.service';

interface AnalyticsData {
  label: string;
  value: number;
  change: number;
  trend: 'up' | 'down';
}

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss'],
})
export class AnalyticsComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  private toastService = inject(ToastService);

  metrics = signal<AnalyticsData[]>([]);
  isLoading = signal(true);

  ngOnInit(): void {
    this.loadAnalytics();
  }

  loadAnalytics(): void {
    this.isLoading.set(true);
    this.dashboardService.getAdminStats().subscribe({
      next: (stats) => {
        const totalUsers = Number(stats.totalUsers ?? 0);
        const activeUsersToday = Number(stats.activeUsersToday ?? 0);
        const totalActivities = Number(stats.totalActivities ?? 0);
        const avgActivitiesPerUser = Number(stats.avgActivitiesPerUser ?? 0);
        const newUsersThisWeek = Number(stats.newUsersThisWeek ?? 0);
        const activityRate = totalUsers > 0 ? (activeUsersToday / totalUsers) * 100 : 0;

        this.metrics.set([
          {
            label: 'Active Users Today',
            value: activeUsersToday,
            change: Number(activityRate.toFixed(1)),
            trend: activityRate >= 0 ? 'up' : 'down',
          },
          {
            label: 'New Sign-ups (7d)',
            value: newUsersThisWeek,
            change: Number(((newUsersThisWeek / Math.max(totalUsers, 1)) * 100).toFixed(1)),
            trend: newUsersThisWeek > 0 ? 'up' : 'down',
          },
          {
            label: 'Total Activities',
            value: totalActivities,
            change: Number(avgActivitiesPerUser.toFixed(1)),
            trend: totalActivities > 0 ? 'up' : 'down',
          },
          {
            label: 'Avg Activities/User',
            value: Number(avgActivitiesPerUser.toFixed(1)),
            change: Number(avgActivitiesPerUser.toFixed(1)),
            trend: avgActivitiesPerUser > 0 ? 'up' : 'down',
          },
        ]);
      },
      error: () => {
        this.toastService.error('Failed to load analytics data');
      },
      complete: () => {
        this.isLoading.set(false);
      },
    });
  }

  formatNumber(num: number): string {
    return num.toLocaleString();
  }

  metricShare(value: number): number {
    const maxValue = Math.max(...this.metrics().map((metric) => metric.value), 1);
    return Math.round((value / maxValue) * 100);
  }

  metricChangeShare(change: number): number {
    const maxChange = Math.max(...this.metrics().map((metric) => Math.abs(metric.change)), 1);
    return Math.round((Math.abs(change) / maxChange) * 100);
  }
}
