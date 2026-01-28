import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ActivityService } from '../../../../core/services/activity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { ActivityType } from '../../../../core/models/activity.model';

@Component({
  selector: 'app-activity-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="activity-form-container max-w-2xl mx-auto">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-6">
        {{ isEditMode ? 'Edit Activity' : 'Track Activity' }}
      </h1>

      <div class="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
        <form (ngSubmit)="onSubmit()" class="space-y-6">
          <!-- Activity Type -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Activity Type *
            </label>
            <select
              [(ngModel)]="formData.type"
              name="type"
              required
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
            >
              <option value="">Select activity type</option>
              @for (type of activityTypes; track type.value) {
                <option [value]="type.value">{{ type.label }}</option>
              }
            </select>
          </div>

          <!-- Date -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Date *
            </label>
            <input
              type="date"
              [(ngModel)]="formData.date"
              name="date"
              required
              [max]="maxDate"
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
            />
          </div>

          <!-- Duration -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Duration (minutes) *
            </label>
            <input
              type="number"
              [(ngModel)]="formData.duration"
              name="duration"
              required
              min="1"
              max="1440"
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              placeholder="Enter duration in minutes"
            />
          </div>

          <!-- Calories Burned -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Calories Burned *
            </label>
            <input
              type="number"
              [(ngModel)]="formData.caloriesBurned"
              name="caloriesBurned"
              required
              min="0"
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              placeholder="Estimated calories burned"
            />
          </div>

          <!-- Distance (Optional) -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Distance (km)
            </label>
            <input
              type="number"
              [(ngModel)]="formData.distance"
              name="distance"
              min="0"
              step="0.1"
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              placeholder="Distance covered (if applicable)"
            />
          </div>

          <!-- Intensity -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Intensity
            </label>
            <select
              [(ngModel)]="formData.intensity"
              name="intensity"
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
            >
              <option value="">Select intensity</option>
              <option value="LOW">Low</option>
              <option value="MODERATE">Moderate</option>
              <option value="HIGH">High</option>
            </select>
          </div>

          <!-- Notes -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Notes
            </label>
            <textarea
              [(ngModel)]="formData.notes"
              name="notes"
              rows="3"
              class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              placeholder="Add any notes about your workout..."
            ></textarea>
          </div>

          <!-- Submit Buttons -->
          <div class="flex space-x-4">
            <button
              type="submit"
              [disabled]="isSubmitting"
              class="flex-1 bg-indigo-600 text-white py-3 rounded-lg hover:bg-indigo-700 transition font-semibold disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ isSubmitting ? 'Saving...' : isEditMode ? 'Update Activity' : 'Track Activity' }}
            </button>
            <button
              type="button"
              (click)="onCancel()"
              class="px-6 py-3 bg-gray-200 dark:bg-gray-700 text-gray-900 dark:text-white rounded-lg hover:bg-gray-300 dark:hover:bg-gray-600 transition font-semibold"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
})
export class ActivityFormComponent implements OnInit {
  private activityService = inject(ActivityService);
  private toastService = inject(ToastService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  isEditMode = false;
  isSubmitting = false;
  activityId: string | null = null;
  maxDate = new Date().toISOString().split('T')[0];

  formData = {
    type: '',
    date: new Date().toISOString().split('T')[0],
    duration: null as number | null,
    caloriesBurned: null as number | null,
    distance: null as number | null,
    intensity: '',
    notes: '',
  };

  activityTypes = this.activityService.getActivityTypes();

  ngOnInit() {
    this.activityId = this.route.snapshot.paramMap.get('id');
    if (this.activityId) {
      this.isEditMode = true;
      this.loadActivity();
    }
  }

  loadActivity() {
    if (!this.activityId) return;

    this.activityService.getActivityById(this.activityId).subscribe({
      next: (activity) => {
        this.formData = {
          type: activity.type,
          date: activity.date
            ? new Date(activity.date).toISOString().split('T')[0]
            : new Date().toISOString().split('T')[0],
          duration: activity.duration,
          caloriesBurned: activity.caloriesBurned,
          distance: activity.distance || null,
          intensity: activity.intensity || '',
          notes: activity.notes || '',
        };
      },
      error: (error) => {
        console.error('Failed to load activity:', error);
        this.toastService.error('Failed to load activity');
        this.router.navigate(['/user/activities']);
      },
    });
  }

  onSubmit() {
    if (!this.formData.type || !this.formData.duration || !this.formData.caloriesBurned) {
      this.toastService.error('Please fill in all required fields');
      return;
    }

    this.isSubmitting = true;

    const activityData: any = {
      type: this.formData.type as ActivityType,
      date: new Date(this.formData.date),
      duration: this.formData.duration,
      caloriesBurned: this.formData.caloriesBurned,
    };

    if (this.formData.distance) {
      activityData.distance = this.formData.distance;
    }
    if (this.formData.intensity) {
      activityData.intensity = this.formData.intensity;
    }
    if (this.formData.notes) {
      activityData.notes = this.formData.notes;
    }

    const operation = this.isEditMode
      ? this.activityService.updateActivity(this.activityId!, activityData)
      : this.activityService.createActivity(activityData);

    operation.subscribe({
      next: () => {
        this.toastService.success(
          this.isEditMode ? 'Activity updated successfully' : 'Activity tracked successfully',
        );
        this.router.navigate(['/user/activities']);
      },
      error: (error) => {
        console.error('Failed to save activity:', error);
        this.toastService.error('Failed to save activity');
        this.isSubmitting = false;
      },
    });
  }

  onCancel() {
    this.router.navigate(['/user/activities']);
  }
}
