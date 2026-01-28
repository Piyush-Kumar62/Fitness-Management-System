import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ActivityService } from '../../../core/services/activity.service';
import { Activity } from '../../../core/models/activity.model';

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
export class AdminDashboardComponent implements OnInit {
  private activityService = inject(ActivityService);

  stats = signal<DashboardStats>({
    totalUsers: 0,
    totalActivities: 0,
    totalCaloriesBurned: 0,
    avgActivitiesPerUser: 0,
    activeUsersToday: 0,
    newUsersThisWeek: 0,
  });

  recentActivities = signal<Activity[]>([]);
  isLoadingStats = signal(true);
  isLoadingActivities = signal(true);

  // Activity type distribution
  activityDistribution = computed(() => {
    const activities = this.recentActivities();
    const distribution: Record<string, number> = {};

    activities.forEach((activity) => {
      distribution[activity.type] = (distribution[activity.type] || 0) + 1;
    });

    return Object.entries(distribution)
      .map(([type, count]) => ({ type, count }))
      .sort((a, b) => b.count - a.count);
  });

  ngOnInit(): void {
    this.loadDashboardData();
  }

  private loadDashboardData(): void {
    this.loadStats();
    this.loadRecentActivities();
  }

  private loadStats(): void {
    this.isLoadingStats.set(true);

    // TODO: Replace with actual admin API call
    // For now, using mock data
    setTimeout(() => {
      this.stats.set({
        totalUsers: 1248,
        totalActivities: 8567,
        totalCaloriesBurned: 3284750,
        avgActivitiesPerUser: 6.9,
        activeUsersToday: 342,
        newUsersThisWeek: 47,
      });
      this.isLoadingStats.set(false);
    }, 1000);
  }

  private loadRecentActivities(): void {
    this.isLoadingActivities.set(true);

    // TODO: Replace with admin API to get all activities
    this.activityService.getActivities().subscribe({
      next: (activities: Activity[]) => {
        this.recentActivities.set(activities.slice(0, 10));
        this.isLoadingActivities.set(false);
      },
      error: () => {
        this.isLoadingActivities.set(false);
      },
    });
  }

  getActivityTypeIcon(type: string): string {
    const icons: Record<string, string> = {
      RUNNING: '🏃',
      WALKING: '🚶',
      CYCLING: '🚴',
      SWIMMING: '🏊',
      WEIGHT_TRAINING: '🏋️',
      YOGA: '🧘',
      HIIT: '💪',
      CARDIO: '❤️',
      STRETCHING: '🤸',
      OTHER: '⚡',
    };
    return icons[type] || '⚡';
  }

  formatDuration(seconds: number): string {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);

    if (hours > 0) {
      return `${hours}h ${minutes}m`;
    }
    return `${minutes}m`;
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  formatNumber(num: number): string {
    if (num >= 1000000) {
      return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
      return (num / 1000).toFixed(1) + 'K';
    }
    return num.toString();
  }
}
