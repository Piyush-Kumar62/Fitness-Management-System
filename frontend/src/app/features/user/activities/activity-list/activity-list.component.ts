import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ActivityService } from '../../../../core/services/activity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { Activity } from '../../../../core/models/activity.model';

@Component({
  selector: 'app-activity-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="activity-list-container">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-white">My Activities</h1>
        <a
          routerLink="/user/activities/new"
          class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition flex items-center"
        >
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 4v16m8-8H4"
            ></path>
          </svg>
          Track Activity
        </a>
      </div>

      @if (activityService.isLoading()) {
        <div class="flex justify-center items-center p-12">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>
      } @else if (activities().length === 0) {
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-12 text-center">
          <svg
            class="mx-auto h-16 w-16 text-gray-400 mb-4"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M13 10V3L4 14h7v7l9-11h-7z"
            ></path>
          </svg>
          <h3 class="text-xl font-semibold text-gray-900 dark:text-white mb-2">
            No activities yet
          </h3>
          <p class="text-gray-600 dark:text-gray-400 mb-6">
            Start tracking your workouts to monitor your fitness progress!
          </p>
          <a
            routerLink="/user/activities/new"
            class="inline-flex items-center px-6 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition"
          >
            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 4v16m8-8H4"
              ></path>
            </svg>
            Track Your First Activity
          </a>
        </div>
      } @else {
        <!-- Activity Stats -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-4">
            <h3 class="text-sm text-gray-600 dark:text-gray-400">Total Activities</h3>
            <p class="text-2xl font-bold text-indigo-600">{{ activities().length }}</p>
          </div>
          <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-4">
            <h3 class="text-sm text-gray-600 dark:text-gray-400">Total Duration</h3>
            <p class="text-2xl font-bold text-green-600">{{ getTotalDuration() }} min</p>
          </div>
          <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-4">
            <h3 class="text-sm text-gray-600 dark:text-gray-400">Total Calories</h3>
            <p class="text-2xl font-bold text-purple-600">{{ getTotalCalories() }} kcal</p>
          </div>
        </div>

        <!-- Activities List -->
        <div class="space-y-4">
          @for (activity of activities(); track activity.id) {
            <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-6 hover:shadow-lg transition">
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <div class="flex items-center space-x-3 mb-2">
                    <span
                      class="px-3 py-1 bg-indigo-100 dark:bg-indigo-900 text-indigo-800 dark:text-indigo-300 rounded-full text-sm font-semibold"
                    >
                      {{ activity.type }}
                    </span>
                    <span class="text-sm text-gray-500 dark:text-gray-400">
                      {{ formatDate(activity.date) }}
                    </span>
                  </div>

                  <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mt-4">
                    <div>
                      <p class="text-xs text-gray-500 dark:text-gray-400">Duration</p>
                      <p class="text-lg font-semibold text-gray-900 dark:text-white">
                        {{ activity.duration }} min
                      </p>
                    </div>
                    <div>
                      <p class="text-xs text-gray-500 dark:text-gray-400">Calories</p>
                      <p class="text-lg font-semibold text-gray-900 dark:text-white">
                        {{ activity.caloriesBurned }} kcal
                      </p>
                    </div>
                    @if (activity.distance) {
                      <div>
                        <p class="text-xs text-gray-500 dark:text-gray-400">Distance</p>
                        <p class="text-lg font-semibold text-gray-900 dark:text-white">
                          {{ activity.distance }} km
                        </p>
                      </div>
                    }
                    @if (activity.intensity) {
                      <div>
                        <p class="text-xs text-gray-500 dark:text-gray-400">Intensity</p>
                        <p class="text-lg font-semibold text-gray-900 dark:text-white capitalize">
                          {{ activity.intensity }}
                        </p>
                      </div>
                    }
                  </div>

                  @if (activity.notes) {
                    <p class="text-sm text-gray-600 dark:text-gray-400 mt-3">
                      {{ activity.notes }}
                    </p>
                  }
                </div>

                <div class="flex space-x-2 ml-4">
                  <a
                    [routerLink]="['/user/activities', activity.id]"
                    class="text-gray-400 hover:text-indigo-600 transition"
                    title="View details"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                      ></path>
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                      ></path>
                    </svg>
                  </a>
                  <button
                    (click)="deleteActivity(activity.id)"
                    class="text-gray-400 hover:text-red-600 transition"
                    title="Delete activity"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                      ></path>
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          }
        </div>
      }
    </div>
  `,
})
export class ActivityListComponent implements OnInit {
  activityService = inject(ActivityService);
  private toastService = inject(ToastService);

  activities = this.activityService.activities;

  ngOnInit() {
    this.loadActivities();
  }

  loadActivities() {
    this.activityService.getActivities().subscribe({
      error: (error) => {
        console.error('Failed to load activities:', error);
        this.toastService.error('Failed to load activities');
      },
    });
  }

  deleteActivity(id: string) {
    if (!confirm('Are you sure you want to delete this activity?')) return;

    this.activityService.deleteActivity(id).subscribe({
      next: () => {
        this.toastService.success('Activity deleted');
      },
      error: (error) => {
        console.error('Failed to delete activity:', error);
        this.toastService.error('Failed to delete activity');
      },
    });
  }

  getTotalDuration(): number {
    return this.activities().reduce((sum, activity) => sum + activity.duration, 0);
  }

  getTotalCalories(): number {
    return this.activities().reduce((sum, activity) => sum + activity.caloriesBurned, 0);
  }

  formatDate(date: string | Date | undefined): string {
    if (!date) return 'Unknown date';
    const d = new Date(date);
    const now = new Date();
    const diffMs = now.getTime() - d.getTime();
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffDays === 0) return 'Today';
    if (diffDays === 1) return 'Yesterday';
    if (diffDays < 7) return `${diffDays} days ago`;

    return d.toLocaleDateString();
  }
}
