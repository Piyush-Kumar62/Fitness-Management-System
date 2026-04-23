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
  templateUrl: './activity-list.component.html',
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

  async deleteActivity(id: string): Promise<void> {
    const confirmed = await this.toastService.confirm(
      'Delete this activity?',
      'This action cannot be undone.',
      'Delete activity',
    );
    if (!confirmed) return;

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
