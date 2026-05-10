import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { GoalService, Goal, GoalStatus, GoalType } from '../../../../core/services/goal.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-goal-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './goal-list.component.html',
  styles: [],
})
export class GoalListComponent implements OnInit {
  private goalService = inject(GoalService);
  private toast = inject(ToastService);
  private router = inject(Router);

  goals: Goal[] = [];
  filteredGoals: Goal[] = [];
  selectedStatus: string | null = null;

  statuses = [
    { value: null, label: 'All' },
    { value: GoalStatus.ACTIVE, label: 'Active' },
    { value: GoalStatus.COMPLETED, label: 'Completed' },
    { value: GoalStatus.PAUSED, label: 'Paused' },
  ];

  ngOnInit() {
    this.loadGoals();
  }

  loadGoals() {
    this.goalService.getAllGoals().subscribe({
      next: (goals) => {
        this.goals = goals;
        this.filterByStatus(this.selectedStatus);
      },
      error: () => {
        this.toast.error('Failed to load your goals. Please refresh the page.', 'Load Error');
      },
    });
  }

  filterByStatus(status: string | null) {
    this.selectedStatus = status;
    if (status) {
      this.filteredGoals = this.goals.filter((g) => g.status === status);
    } else {
      this.filteredGoals = [...this.goals];
    }
  }

  async deleteGoal(goalId: string, goalTitle: string): Promise<void> {
    const confirmed = await this.toast.confirm(
      `Delete "${goalTitle}"?`,
      'This goal and all its milestones will be permanently removed.',
      'Delete Goal',
    );
    if (!confirmed) return;

    this.goalService.deleteGoal(goalId).subscribe({
      next: () => {
        this.toast.success('Goal deleted successfully.', 'Goal Deleted');
        this.loadGoals();
      },
      error: () => {
        this.toast.error('Failed to delete the goal. Please try again.');
      },
    });
  }

  getStatusClass(status: GoalStatus): string {
    const classes: Record<GoalStatus, string> = {
      [GoalStatus.ACTIVE]: 'bg-green-100 text-green-800',
      [GoalStatus.COMPLETED]: 'bg-blue-100 text-blue-800',
      [GoalStatus.PAUSED]: 'bg-yellow-100 text-yellow-800',
      [GoalStatus.ABANDONED]: 'bg-gray-100 text-gray-800',
    };
    return classes[status] || 'bg-gray-100 text-gray-800';
  }

  formatGoalType(type: GoalType): string {
    return type.replace(/_/g, ' ');
  }

  getAchievedMilestones(goal: Goal): number {
    return goal.milestones?.filter((m) => m.achieved).length || 0;
  }
}
