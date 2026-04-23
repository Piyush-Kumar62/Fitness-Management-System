import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TrainerService } from '../../../../core/services/trainer.service';
import { WorkoutPlan } from '../../../../core/models/trainer.model';

@Component({
  selector: 'app-workout-plan-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './workout-plan-list.component.html',
})
export class WorkoutPlanListComponent implements OnInit {
  private trainerService = inject(TrainerService);
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
}
