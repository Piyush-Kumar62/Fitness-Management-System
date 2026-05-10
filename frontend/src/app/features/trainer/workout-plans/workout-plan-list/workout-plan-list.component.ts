import { Component, OnInit, inject, signal } from '@angular/core';

import { RouterLink } from '@angular/router';
import { TrainerService } from '../../../../core/services/trainer.service';
import { ToastService } from '../../../../core/services/toast.service';
import { WorkoutPlan } from '../../../../core/models/trainer.model';

@Component({
  selector: 'app-workout-plan-list',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './workout-plan-list.component.html',
})
export class WorkoutPlanListComponent implements OnInit {
  private trainerService = inject(TrainerService);
  private toast = inject(ToastService);
  isLoading = signal(false);
  plans = signal<WorkoutPlan[]>([]);

  ngOnInit() {
    this.loadPlans();
  }

  private loadPlans() {
    this.isLoading.set(true);
    this.trainerService.getWorkoutPlans().subscribe({
      next: (plans) => this.plans.set(plans),
      complete: () => this.isLoading.set(false),
      error: () => this.isLoading.set(false),
    });
  }

  async deletePlan(planId: string): Promise<void> {
    const confirmed = await this.toast.confirm(
      'Delete Workout Plan',
      'This action cannot be undone.',
      'Delete',
    );
    if (!confirmed) {
      return;
    }

    this.trainerService.deleteWorkoutPlan(planId).subscribe({
      next: () => {
        this.toast.success('Workout plan deleted');
        this.plans.update((current) => current.filter((plan) => plan.id !== planId));
      },
      error: () => this.toast.error('Failed to delete workout plan'),
    });
  }
}
