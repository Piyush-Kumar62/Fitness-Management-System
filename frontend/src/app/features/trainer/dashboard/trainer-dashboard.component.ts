import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { DashboardService } from '../../../core/services/dashboard.service';

@Component({
  selector: 'app-trainer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './trainer-dashboard.component.html',
})
export class TrainerDashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private dashboardService = inject(DashboardService);

  user = this.authService.user;
  isLoading = signal(false);

  totalMembers = signal(0);
  totalWorkoutPlans = signal(0);
  totalDietPlans = signal(0);
  activeGoals = signal(0);

  ngOnInit() {
    this.loadDashboardData();
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
}
