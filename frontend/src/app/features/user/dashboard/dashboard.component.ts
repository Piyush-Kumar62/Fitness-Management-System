import { Component, OnInit, inject, computed, signal } from '@angular/core';

import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ActivityService } from '../../../core/services/activity.service';
import { RecommendationService } from '../../../core/services/recommendation.service';
import { DashboardService } from '../../../core/services/dashboard.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private activityService = inject(ActivityService);
  private recommendationService = inject(RecommendationService);
  private dashboardService = inject(DashboardService);
  private toastService = inject(ToastService);

  user = this.authService.user;

  totalActivities = computed(() => this.activityService.activities().length);
  totalCalories = computed(() =>
    this.activityService.activities().reduce((sum, activity) => sum + activity.caloriesBurned, 0),
  );
  totalRecommendations = computed(() => this.recommendationService.recommendations().length);
  bookedClasses = signal(0);

  ngOnInit() {
    this.loadDashboardData();
  }

  private loadDashboardData() {
    this.dashboardService.getMemberStats().subscribe({
      next: (stats) => {
        this.bookedClasses.set(stats.bookedClasses ?? 0);
      },
      error: () => {
        this.toastService.error('Failed to load dashboard stats');
      },
    });

    // Load activities
    this.activityService.getActivities().subscribe({
      error: (error) => {
        console.error('Failed to load activities:', error);
        this.toastService.error('Failed to load recent activities');
      },
    });

    // Load recommendations
    const userId = this.user()?.id;
    if (userId) {
      this.recommendationService.getUserRecommendations(userId).subscribe({
        error: (error) => {
          console.error('Failed to load recommendations:', error);
          this.toastService.error('Failed to load recommendations');
        },
      });
    }
  }
}
