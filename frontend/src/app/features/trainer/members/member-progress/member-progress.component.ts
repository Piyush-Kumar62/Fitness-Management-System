import { Component, OnInit, inject, signal } from '@angular/core';

import { ActivatedRoute, RouterLink } from '@angular/router';
import { TrainerService } from '../../../../core/services/trainer.service';
import { MemberProgress, DietPlan, WorkoutPlan } from '../../../../core/models/trainer.model';

@Component({
  selector: 'app-member-progress',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './member-progress.component.html',
})
export class MemberProgressComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private trainerService = inject(TrainerService);
  isLoading = signal(false);
  memberId = signal<string | null>(null);
  progress = signal<MemberProgress | null>(null);
  workoutPlans = signal<WorkoutPlan[]>([]);
  dietPlans = signal<DietPlan[]>([]);

  ngOnInit() {
    this.memberId.set(this.route.snapshot.paramMap.get('id'));
    this.loadMemberProgress();
  }

  private loadMemberProgress() {
    const memberId = this.memberId();
    if (!memberId) {
      return;
    }
    this.isLoading.set(true);
    this.trainerService.getMemberProgress(memberId).subscribe({
      next: (progress) => this.progress.set(progress),
      complete: () => this.isLoading.set(false),
      error: () => this.isLoading.set(false),
    });
    this.trainerService.getWorkoutPlans().subscribe((plans) => this.workoutPlans.set(plans));
    this.trainerService.getDietPlans().subscribe((plans) => this.dietPlans.set(plans));
  }

  assign(workoutPlanId?: string, dietPlanId?: string): void {
    const memberId = this.memberId();
    if (!memberId) {
      return;
    }
    this.trainerService.assignPlan({ memberId, workoutPlanId, dietPlanId }).subscribe(() => {
      this.loadMemberProgress();
    });
  }
}
