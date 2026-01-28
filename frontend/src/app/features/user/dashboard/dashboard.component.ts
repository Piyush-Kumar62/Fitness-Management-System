import { Component, OnInit, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ActivityService } from '../../../core/services/activity.service';
import { RecommendationService } from '../../../core/services/recommendation.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="dashboard-container">
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-2">
          Welcome back, {{ user()?.firstName }}!
        </h1>
        <p class="text-gray-600 dark:text-gray-400">Here's your fitness overview</p>
      </div>

      <!-- Quick Stats -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-2">Activities</h3>
          <p class="text-3xl font-bold text-indigo-600">{{ totalActivities() }}</p>
          <p class="text-sm text-gray-500 mt-2">Total tracked</p>
        </div>

        <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-2">Calories</h3>
          <p class="text-3xl font-bold text-green-600">{{ totalCalories() }}</p>
          <p class="text-sm text-gray-500 mt-2">Total burned</p>
        </div>

        <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-2">Recommendations</h3>
          <p class="text-3xl font-bold text-purple-600">{{ totalRecommendations() }}</p>
          <p class="text-sm text-gray-500 mt-2">AI insights</p>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
        <h2 class="text-xl font-semibold text-gray-900 dark:text-white mb-4">Quick Actions</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <a
            routerLink="/user/activities/new"
            class="flex items-center p-4 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition"
          >
            <div class="flex-shrink-0 bg-indigo-100 dark:bg-indigo-900 p-3 rounded-lg">
              <svg
                class="h-6 w-6 text-indigo-600 dark:text-indigo-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 4v16m8-8H4"
                ></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="font-medium text-gray-900 dark:text-white">Track Activity</p>
              <p class="text-sm text-gray-500">Log your workout</p>
            </div>
          </a>

          <a
            routerLink="/user/activities"
            class="flex items-center p-4 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition"
          >
            <div class="flex-shrink-0 bg-green-100 dark:bg-green-900 p-3 rounded-lg">
              <svg
                class="h-6 w-6 text-green-600 dark:text-green-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
                ></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="font-medium text-gray-900 dark:text-white">View Activities</p>
              <p class="text-sm text-gray-500">See your history</p>
            </div>
          </a>

          <a
            routerLink="/user/bmi-calculator"
            class="flex items-center p-4 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition"
          >
            <div class="flex-shrink-0 bg-purple-100 dark:bg-purple-900 p-3 rounded-lg">
              <svg
                class="h-6 w-6 text-purple-600 dark:text-purple-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z"
                ></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="font-medium text-gray-900 dark:text-white">BMI Calculator</p>
              <p class="text-sm text-gray-500">Check your health</p>
            </div>
          </a>
        </div>
      </div>
    </div>
  `,
})
export class DashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private activityService = inject(ActivityService);
  private recommendationService = inject(RecommendationService);

  user = this.authService.user;

  totalActivities = computed(() => this.activityService.activities().length);
  totalCalories = computed(() =>
    this.activityService.activities().reduce((sum, activity) => sum + activity.caloriesBurned, 0),
  );
  totalRecommendations = computed(() => this.recommendationService.recommendations().length);

  ngOnInit() {
    console.log('Dashboard loaded for user:', this.user());
    this.loadDashboardData();
  }

  private loadDashboardData() {
    // Load activities
    this.activityService.getActivities().subscribe({
      error: (error) => console.error('Failed to load activities:', error),
    });

    // Load recommendations
    const userId = this.user()?.id;
    if (userId) {
      this.recommendationService.getUserRecommendations(userId).subscribe({
        error: (error) => console.error('Failed to load recommendations:', error),
      });
    }
  }
}
