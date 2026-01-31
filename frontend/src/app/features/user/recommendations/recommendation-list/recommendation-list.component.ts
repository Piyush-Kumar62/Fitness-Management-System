import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { RecommendationService } from '../../../../core/services/recommendation.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';
import { Recommendation } from '../../../../core/models/recommendation.model';

@Component({
  selector: 'app-recommendation-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="recommendation-list-container">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-white">AI Recommendations</h1>
      </div>

      @if (recommendationService.isLoading()) {
        <div class="flex justify-center items-center p-12">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>
      } @else if (recommendations().length === 0) {
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
              d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"
            ></path>
          </svg>
          <h3 class="text-xl font-semibold text-gray-900 dark:text-white mb-2">
            No recommendations yet
          </h3>
          <p class="text-gray-600 dark:text-gray-400 mb-6">
            Track your activities to get AI-powered fitness recommendations
          </p>
          <a
            routerLink="/user/activities"
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
        <div class="space-y-4">
          @for (rec of recommendations(); track rec.id) {
            <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-6 hover:shadow-lg transition">
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <div class="flex items-center space-x-3 mb-3">
                    <span
                      class="px-3 py-1 rounded-full text-xs font-semibold"
                      [class.bg-blue-100]="rec.type === 'IMPROVEMENT'"
                      [class.text-blue-800]="rec.type === 'IMPROVEMENT'"
                      [class.bg-green-100]="rec.type === 'ACHIEVEMENT'"
                      [class.text-green-800]="rec.type === 'ACHIEVEMENT'"
                      [class.bg-purple-100]="rec.type === 'MOTIVATION'"
                      [class.text-purple-800]="rec.type === 'MOTIVATION'"
                      [class.bg-orange-100]="rec.type === 'WARNING'"
                      [class.text-orange-800]="rec.type === 'WARNING'"
                    >
                      {{ getTypeLabel(rec.type) }}
                    </span>
                    <span class="text-sm text-gray-500 dark:text-gray-400">
                      {{ formatDate(rec.createdAt) }}
                    </span>
                  </div>

                  <p class="text-gray-900 dark:text-white text-lg mb-2">{{ rec.recommendation }}</p>

                  @if (rec.activityId) {
                    <p class="text-sm text-gray-600 dark:text-gray-400">
                      Activity ID: <span class="font-medium">{{ rec.activityId }}</span>
                    </p>
                  }
                </div>

                <button
                  (click)="deleteRecommendation(rec.id)"
                  class="ml-4 text-gray-400 hover:text-red-600 transition"
                  title="Delete recommendation"
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
          }
        </div>
      }
    </div>
  `,
})
export class RecommendationListComponent implements OnInit {
  recommendationService = inject(RecommendationService);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);

  recommendations = this.recommendationService.recommendations;

  ngOnInit() {
    this.loadRecommendations();
  }

  loadRecommendations() {
    const userId = this.authService.user()?.id;
    if (!userId) return;

    this.recommendationService.getUserRecommendations(userId).subscribe({
      error: (error) => {
        console.error('Failed to load recommendations:', error);
        this.toastService.error('Failed to load recommendations');
      },
    });
  }

  deleteRecommendation(id: string) {
    if (!confirm('Are you sure you want to delete this recommendation?')) return;

    this.recommendationService.deleteRecommendation(id).subscribe({
      next: () => {
        this.toastService.success('Recommendation deleted');
      },
      error: (error) => {
        console.error('Failed to delete recommendation:', error);
        this.toastService.error('Failed to delete recommendation');
      },
    });
  }

  getTypeLabel(type: string): string {
    const labels: Record<string, string> = {
      IMPROVEMENT: 'Improvement',
      ACHIEVEMENT: 'Achievement',
      MOTIVATION: 'Motivation',
      WARNING: 'Warning',
      GENERAL: 'General',
    };
    return labels[type] || type;
  }

  formatDate(date: string | Date | undefined): string {
    if (!date) return 'Recently';
    const d = new Date(date);
    const now = new Date();
    const diffMs = now.getTime() - d.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins} min ago`;
    if (diffHours < 24) return `${diffHours} hours ago`;
    if (diffDays < 7) return `${diffDays} days ago`;

    return d.toLocaleDateString();
  }
}
