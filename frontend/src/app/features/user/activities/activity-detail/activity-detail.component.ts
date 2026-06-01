import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ActivityService } from '../../../../core/services/activity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { Activity } from '../../../../core/models/activity.model';

@Component({
  selector: 'app-activity-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './activity-detail.component.html',
})
export class ActivityDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private activityService = inject(ActivityService);
  private toastService = inject(ToastService);

  activity = signal<Activity | null>(null);
  isLoading = signal(true);

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadActivity(id);
    } else {
      this.toastService.error('Invalid activity ID');
      this.router.navigate(['/member/activities']);
    }
  }

  loadActivity(id: string) {
    this.activityService.getActivityById(id).subscribe({
      next: (data) => {
        this.activity.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load activity:', err);
        this.toastService.error('Failed to load activity details');
        this.isLoading.set(false);
        this.router.navigate(['/member/activities']);
      },
    });
  }

  async deleteActivity() {
    const active = this.activity();
    if (!active) return;

    const confirmed = await this.toastService.confirm(
      'Delete this activity?',
      'This action cannot be undone.',
      'Delete activity'
    );
    if (!confirmed) return;

    this.activityService.deleteActivity(active.id).subscribe({
      next: () => {
        this.toastService.success('Activity deleted successfully');
        this.router.navigate(['/member/activities']);
      },
      error: (err) => {
        console.error('Failed to delete activity:', err);
        this.toastService.error('Failed to delete activity');
      },
    });
  }
}
