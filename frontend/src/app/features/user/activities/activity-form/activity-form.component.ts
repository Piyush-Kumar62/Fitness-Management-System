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
  templateUrl: './activity-form.component.html',
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
        this.router.navigate(['/member/activities']);
      },
    });
  }

  onSubmit() {
    const errors: string[] = [];
    if (!this.formData.type) errors.push('Activity type is required.');
    if (!this.formData.duration || this.formData.duration <= 0) errors.push('Duration must be greater than 0.');
    if (!this.formData.caloriesBurned || this.formData.caloriesBurned <= 0) errors.push('Calories burned must be greater than 0.');
    if (errors.length > 0) {
      this.toastService.validationError('Please fix the following:', errors);
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
          this.isEditMode ? 'Activity updated successfully' : 'Activity tracked successfully! 💪',
          this.isEditMode ? 'Activity Updated' : 'Activity Logged!',
        );
        this.router.navigate(['/member/activities']);
      },
      error: () => {
        this.toastService.error('Failed to save activity. Please check your inputs and try again.');
        this.isSubmitting = false;
      },
    });
  }

  onCancel() {
    this.router.navigate(['/member/activities']);
  }
}
