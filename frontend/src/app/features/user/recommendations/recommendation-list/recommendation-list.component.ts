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
  templateUrl: './recommendation-list.component.html',
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

  async deleteRecommendation(id: string): Promise<void> {
    const confirmed = await this.toastService.confirm(
      'Delete this recommendation?',
      'This action cannot be undone.',
      'Delete recommendation',
    );
    if (!confirmed) return;

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
