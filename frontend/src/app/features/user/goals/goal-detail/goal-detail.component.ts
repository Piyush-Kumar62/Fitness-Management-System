import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { GoalService, Goal, Milestone, GoalStatus } from '../../../../core/services/goal.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-goal-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './goal-detail.component.html',
  styles: [],
})
export class GoalDetailComponent implements OnInit {
  goal?: Goal;
  showMilestoneForm = false;
  milestoneForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private goalService: GoalService,
    private router: Router,
    private route: ActivatedRoute,
    private toastService: ToastService,
  ) {
    this.milestoneForm = this.fb.group({
      title: ['', Validators.required],
      description: [''],
      targetValue: [null, Validators.required],
    });
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadGoal(id);
    }
  }

  loadGoal(id: string) {
    this.goalService.getGoalById(id).subscribe({
      next: (goal) => {
        this.goal = goal;
      },
      error: (error) => {
        console.error('Error loading goal:', error);
        this.toastService.error('Failed to load goal details');
      },
    });
  }

  addMilestone() {
    if (this.milestoneForm.invalid || !this.goal?.id) return;

    const milestoneData: Milestone = this.milestoneForm.value;

    this.goalService.addMilestone(this.goal.id, milestoneData).subscribe({
      next: () => {
        this.milestoneForm.reset();
        this.showMilestoneForm = false;
        this.loadGoal(this.goal!.id!);
        this.toastService.success('Milestone added');
      },
      error: (error) => {
        console.error('Error adding milestone:', error);
        this.toastService.error('Failed to add milestone');
      },
    });
  }

  achieveMilestone(milestoneId: string) {
    this.goalService.achieveMilestone(milestoneId).subscribe({
      next: () => {
        this.loadGoal(this.goal!.id!);
        this.toastService.success('Milestone marked as achieved');
      },
      error: (error) => {
        console.error('Error achieving milestone:', error);
        this.toastService.error('Failed to update milestone');
      },
    });
  }

  async deleteGoal(): Promise<void> {
    if (!this.goal?.id) return;
    const confirmed = await this.toastService.confirm(
      'Delete this goal?',
      'This action cannot be undone.',
      'Delete goal',
    );
    if (!confirmed) return;

    this.goalService.deleteGoal(this.goal.id).subscribe({
      next: () => {
        this.toastService.success('Goal deleted');
        this.router.navigate(['/member/goals']);
      },
      error: (error) => {
        console.error('Error deleting goal:', error);
        this.toastService.error('Failed to delete goal');
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

  formatGoalType(type: any): string {
    return type.replace(/_/g, ' ');
  }
}
