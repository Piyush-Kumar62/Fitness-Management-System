import { Component, OnInit, inject, signal } from '@angular/core';

import { RouterLink } from '@angular/router';
import { TrainerService } from '../../../../core/services/trainer.service';
import { ToastService } from '../../../../core/services/toast.service';
import { DietPlan } from '../../../../core/models/trainer.model';

@Component({
  selector: 'app-diet-plan-list',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './diet-plan-list.component.html',
})
export class DietPlanListComponent implements OnInit {
  private trainerService = inject(TrainerService);
  private toast = inject(ToastService);
  isLoading = signal(false);
  plans = signal<DietPlan[]>([]);

  ngOnInit() {
    this.loadPlans();
  }

  private loadPlans() {
    this.isLoading.set(true);
    this.trainerService.getDietPlans().subscribe({
      next: (plans) => this.plans.set(plans),
      complete: () => this.isLoading.set(false),
      error: () => this.isLoading.set(false),
    });
  }

  async deletePlan(planId: string): Promise<void> {
    const confirmed = await this.toast.confirm(
      'Delete Diet Plan',
      'This action cannot be undone.',
      'Delete',
    );
    if (!confirmed) {
      return;
    }

    this.trainerService.deleteDietPlan(planId).subscribe({
      next: () => {
        this.toast.success('Diet plan deleted');
        this.plans.update((current) => current.filter((plan) => plan.id !== planId));
      },
      error: () => this.toast.error('Failed to delete diet plan'),
    });
  }
}
