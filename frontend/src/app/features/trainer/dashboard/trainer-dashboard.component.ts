import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { DashboardService } from '../../../core/services/dashboard.service';
import { TrainerService } from '../../../core/services/trainer.service';
import { User } from '../../../core/models/user.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-trainer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './trainer-dashboard.component.html',
})
export class TrainerDashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private dashboardService = inject(DashboardService);
  private trainerService = inject(TrainerService);

  user = this.authService.user;
  isLoading = signal(false);
  membersLoading = signal(false);

  totalMembers = signal(0);
  totalWorkoutPlans = signal(0);
  totalDietPlans = signal(0);
  activeGoals = signal(0);
  recentMembers = signal<User[]>([]);

  ngOnInit() {
    this.loadDashboardData();
    this.loadRecentMembers();
  }

  private loadDashboardData() {
    this.isLoading.set(true);
    this.dashboardService.getTrainerStats().subscribe({
      next: (stats) => {
        this.totalMembers.set(stats.totalMembers ?? 0);
        this.totalWorkoutPlans.set(stats.totalWorkoutPlans ?? 0);
        this.totalDietPlans.set(stats.totalDietPlans ?? 0);
        this.activeGoals.set(stats.activePlanAssignments ?? 0);
      },
      complete: () => this.isLoading.set(false),
      error: () => this.isLoading.set(false),
    });
  }

  private loadRecentMembers() {
    this.membersLoading.set(true);
    this.trainerService.getAssignedMembers().subscribe({
      next: (data) => {
        this.recentMembers.set(data.slice(0, 5));
      },
      complete: () => this.membersLoading.set(false),
      error: () => this.membersLoading.set(false),
    });
  }
}
