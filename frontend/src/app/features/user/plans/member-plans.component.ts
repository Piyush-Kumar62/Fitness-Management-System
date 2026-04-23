import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TrainerService } from '../../../core/services/trainer.service';

@Component({
  selector: 'app-member-plans',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './member-plans.component.html',
})
export class MemberPlansComponent implements OnInit {
  private trainerService = inject(TrainerService);
  isLoading = signal(false);
  workoutPlan = signal<any>(null);
  dietPlan = signal<any>(null);

  ngOnInit() {
    this.loadPlans();
  }

  private loadPlans() {
    this.isLoading.set(true);
    this.trainerService.getCurrentMemberPlans().subscribe({
      next: (response) => {
        this.workoutPlan.set(response.workoutPlan || null);
        this.dietPlan.set(response.dietPlan || null);
      },
      complete: () => this.isLoading.set(false),
      error: () => this.isLoading.set(false),
    });
  }
}
