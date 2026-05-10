import { Component, OnInit, inject, signal, computed } from '@angular/core';

import { map } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { ActivityService } from '../../../core/services/activity.service';
import { ToastService } from '../../../core/services/toast.service';
import { Activity, ActivityType } from '../../../core/models/activity.model';

@Component({
  selector: 'app-activity-management',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './activity-management.component.html',
  styleUrls: ['./activity-management.component.scss'],
})
export class ActivityManagementComponent implements OnInit {
  private activityService = inject(ActivityService);
  private toast = inject(ToastService);

  activities = signal<Activity[]>([]);
  isLoading = signal(false);

  // Filters
  searchQuery = signal('');
  selectedType = signal<ActivityType | 'ALL'>('ALL');
  dateFilter = signal<'today' | 'week' | 'month' | 'all'>('all');

  activityTypes = this.activityService.getActivityTypes();

  // Statistics
  totalActivities = computed(() => this.filteredActivities().length);
  totalDuration = computed(() => this.filteredActivities().reduce((sum, a) => sum + a.duration, 0));
  totalCalories = computed(() =>
    this.filteredActivities().reduce((sum, a) => sum + a.caloriesBurned, 0),
  );

  // Filtered activities
  filteredActivities = computed(() => {
    let filtered = this.activities();

    // Search filter
    const query = this.searchQuery().toLowerCase();
    if (query) {
      filtered = filtered.filter((a) => a.type.toLowerCase().includes(query));
    }

    // Type filter
    const type = this.selectedType();
    if (type !== 'ALL') {
      filtered = filtered.filter((a) => a.type === type);
    }

    // Date filter
    const dateFilter = this.dateFilter();
    const now = new Date();
    if (dateFilter !== 'all') {
      filtered = filtered.filter((a) => {
        if (!a.startTime) return false;
        const activityDate = new Date(a.startTime);
        const diffTime = now.getTime() - activityDate.getTime();
        const diffDays = diffTime / (1000 * 60 * 60 * 24);

        switch (dateFilter) {
          case 'today':
            return diffDays < 1;
          case 'week':
            return diffDays < 7;
          case 'month':
            return diffDays < 30;
          default:
            return true;
        }
      });
    }

    return filtered;
  });

  ngOnInit(): void {
    this.loadActivities();
  }

  loadActivities(): void {
    this.isLoading.set(true);

    this.activityService.getAllSystemActivities(0, 1000).subscribe({
      next: (response: any) => {
        const activities: Activity[] = response.content || [];
        this.activities.set(activities);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      },
    });
  }

  onSearchChange(value: string): void {
    this.searchQuery.set(value);
  }

  onTypeChange(type: ActivityType | 'ALL'): void {
    this.selectedType.set(type);
  }

  onDateFilterChange(filter: 'today' | 'week' | 'month' | 'all'): void {
    this.dateFilter.set(filter);
  }

  async deleteActivity(activityId: string): Promise<void> {
    const confirmed = await this.toast.confirm(
      'Delete this activity?',
      'This action cannot be undone.',
      'Delete activity',
    );
    if (!confirmed) return;

    this.activityService.deleteActivity(activityId).subscribe({
      next: () => {
        this.toast.success('Activity deleted successfully');
        this.loadActivities();
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
    return num.toLocaleString();
  }

  exportActivities(): void {
    const data = this.filteredActivities();
    if (!data || data.length === 0) {
      this.toast.info('No activities to export.');
      return;
    }

    const headers = [
      'Type',
      'Date',
      'Duration (s)',
      'Calories',
      'Distance',
      'Intensity',
      'Notes',
      'System Created At',
    ];

    const csvContent = data.map((row) => {
      const type = row.type || '';
      const date = row.date || row.startTime || '';
      const duration = row.duration || 0;
      const calories = row.caloriesBurned || 0;
      const distance = row.distance || '';
      const intensity = row.intensity || '';
      const notes = `"${(row.notes || '').replace(/"/g, '""')}"`;
      const createdAt = row.createdAt || '';
      return [type, date, duration, calories, distance, intensity, notes, createdAt].join(',');
    });

    csvContent.unshift(headers.join(','));
    const csvString = csvContent.join('\n');

    const blob = new Blob([csvString], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    if (link.download !== undefined) {
      const url = URL.createObjectURL(blob);
      link.setAttribute('href', url);
      link.setAttribute('download', 'exported_admin_activities.csv');
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
    this.toast.success('Activities exported successfully.');
  }
}
