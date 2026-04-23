import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TrainerService } from '../../../../core/services/trainer.service';
import { DietPlan } from '../../../../core/models/trainer.model';

@Component({
  selector: 'app-diet-plan-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './diet-plan-list.component.html',
})
export class DietPlanListComponent implements OnInit {
  private trainerService = inject(TrainerService);
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
}
