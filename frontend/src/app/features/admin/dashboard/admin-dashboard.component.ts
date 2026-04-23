import {
  Component, OnInit, OnDestroy,
  inject, signal, computed, ViewChild, ElementRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ActivityService } from '../../../core/services/activity.service';
import { Activity } from '../../../core/models/activity.model';
import { DashboardService } from '../../../core/services/dashboard.service';
import { ChartService } from '../../../core/services/chart.service';
import type { Chart } from 'chart.js';

interface DashboardStats {
  totalUsers: number;
  totalActivities: number;
  totalCaloriesBurned: number;
  avgActivitiesPerUser: number;
  activeUsersToday: number;
  newUsersThisWeek: number;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss'],
})
export class AdminDashboardComponent implements OnInit, OnDestroy {
  private activityService = inject(ActivityService);
  private dashboardService = inject(DashboardService);
  private chartService = inject(ChartService);

  @ViewChild('trendCanvas') trendCanvas?: ElementRef<HTMLCanvasElement>;
  @ViewChild('distCanvas') distCanvas?: ElementRef<HTMLCanvasElement>;

  private trendChart?: Chart;
  private distChart?: Chart;

  stats = signal<DashboardStats>({
    totalUsers: 0, totalActivities: 0,
    totalCaloriesBurned: 0, avgActivitiesPerUser: 0,
    activeUsersToday: 0, newUsersThisWeek: 0,
  });

  recentActivities = signal<Activity[]>([]);
  isLoadingStats = signal(true);
  isLoadingActivities = signal(true);

  activityDistribution = computed(() => {
    const distribution: Record<string, number> = {};
    this.recentActivities().forEach(a => {
      distribution[a.type] = (distribution[a.type] || 0) + 1;
    });
    return Object.entries(distribution)
      .map(([type, count]) => ({ type, count }))
      .sort((a, b) => b.count - a.count);
  });

  ngOnInit(): void {
    this.loadDashboardData();
  }

  ngOnDestroy(): void {
    this.chartService.destroy(this.trendChart);
    this.chartService.destroy(this.distChart);
  }

  private loadDashboardData(): void {
    this.loadStats();
    this.loadRecentActivities();
  }

  private loadStats(): void {
    this.isLoadingStats.set(true);
    this.dashboardService.getAdminStats().subscribe({
      next: (stats) => {
        this.stats.set({
          totalUsers: stats.totalUsers ?? 0,
          totalActivities: stats.totalActivities ?? 0,
          totalCaloriesBurned: stats.totalCaloriesBurned ?? 0,
          avgActivitiesPerUser: stats.avgActivitiesPerUser ?? 0,
          activeUsersToday: stats.activeUsersToday ?? 0,
          newUsersThisWeek: stats.newUsersThisWeek ?? 0,
        });
        setTimeout(() => this.initDistChart(stats), 100);
      },
      complete: () => this.isLoadingStats.set(false),
      error: () => this.isLoadingStats.set(false),
    });
  }

  private loadRecentActivities(): void {
    this.isLoadingActivities.set(true);
    this.activityService.getAllSystemActivities().subscribe({
      next: (response: any) => {
        const activities = response.content || [];
        this.recentActivities.set(activities.slice(0, 10));
        this.isLoadingActivities.set(false);
        setTimeout(() => this.initTrendChart(activities), 100);
      },
      error: () => this.isLoadingActivities.set(false),
    });
  }

  private initTrendChart(activities: Activity[]): void {
    if (!this.trendCanvas) return;
    this.chartService.destroy(this.trendChart);
    const months = this.getLast6Months();
    const counts = months.map(m =>
      activities.filter((a: any) => a.createdAt?.startsWith(m)).length
    );
    this.trendChart = this.chartService.createLineChart(
      this.trendCanvas.nativeElement,
      months.map(m => m.substring(0, 7)),
      [{ label: 'Activities', data: counts, color: '#6366f1' }],
    );
  }

  private initDistChart(stats: any): void {
    if (!this.distCanvas) return;
    this.chartService.destroy(this.distChart);
    this.distChart = this.chartService.createDoughnutChart(
      this.distCanvas.nativeElement,
      ['Members', 'Trainers', 'Owners', 'Admins'],
      [stats.totalMembers ?? 0, stats.totalTrainers ?? 0, stats.totalOwners ?? 0, stats.totalAdmins ?? 0],
      ['#6366f1', '#10b981', '#f59e0b', '#ef4444'],
    );
  }

  private getLast6Months(): string[] {
    const months: string[] = [];
    const now = new Date();
    for (let i = 5; i >= 0; i--) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
      months.push(d.toISOString().substring(0, 7));
    }
    return months;
  }

  getActivityTypeIcon(type: string): string {
    const icons: Record<string, string> = {
      RUNNING: '🏃', WALKING: '🚶', CYCLING: '🚴', SWIMMING: '🏊',
      WEIGHT_TRAINING: '🏋️', YOGA: '🧘', HIIT: '💪', CARDIO: '❤️',
      STRETCHING: '🤸', OTHER: '⚡',
    };
    return icons[type] || '⚡';
  }

  formatDuration(seconds: number): string {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    return hours > 0 ? `${hours}h ${minutes}m` : `${minutes}m`;
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit',
    });
  }

  formatNumber(num: number): string {
    if (num >= 1_000_000) return (num / 1_000_000).toFixed(1) + 'M';
    if (num >= 1_000) return (num / 1_000).toFixed(1) + 'K';
    return num.toString();
  }
}
