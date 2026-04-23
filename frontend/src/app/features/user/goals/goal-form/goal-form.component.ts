import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { GoalService, Goal, GoalType, GoalStatus } from '../../../../core/services/goal.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-goal-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './goal-form.component.html',
  styles: [],
})
export class GoalFormComponent implements OnInit {
  goalForm: FormGroup;
  isEditMode = false;
  goalId?: string;
  loading = false;

  goalTypes = [
    { value: GoalType.WEIGHT_LOSS, label: 'Weight Loss' },
    { value: GoalType.WEIGHT_GAIN, label: 'Weight Gain' },
    { value: GoalType.MUSCLE_GAIN, label: 'Muscle Gain' },
    { value: GoalType.ENDURANCE, label: 'Endurance' },
    { value: GoalType.STRENGTH, label: 'Strength' },
    { value: GoalType.FLEXIBILITY, label: 'Flexibility' },
    { value: GoalType.HABIT_BUILDING, label: 'Habit Building' },
    { value: GoalType.CUSTOM, label: 'Custom' },
  ];

  goalStatuses = [
    { value: GoalStatus.ACTIVE, label: 'Active' },
    { value: GoalStatus.PAUSED, label: 'Paused' },
    { value: GoalStatus.COMPLETED, label: 'Completed' },
    { value: GoalStatus.ABANDONED, label: 'Abandoned' },
  ];

  constructor(
    private fb: FormBuilder,
    private goalService: GoalService,
    private router: Router,
    private route: ActivatedRoute,
    private toast: ToastService,
  ) {
    this.goalForm = this.fb.group({
      title: ['', Validators.required],
      description: [''],
      type: ['', Validators.required],
      targetValue: [null],
      currentValue: [0],
      unit: ['', Validators.required],
      startDate: [''],
      deadline: [''],
      status: [GoalStatus.ACTIVE],
    });
  }

  ngOnInit() {
    this.goalId = this.route.snapshot.paramMap.get('id') || undefined;
    this.isEditMode = !!this.goalId;

    if (this.isEditMode && this.goalId) {
      this.loadGoal(this.goalId);
    }
  }

  loadGoal(id: string) {
    this.goalService.getGoalById(id).subscribe({
      next: (goal) => {
        this.goalForm.patchValue(goal);
      },
      error: () => {
        this.toast.error('Could not load goal details. It may have been deleted.', 'Goal Not Found');
        this.router.navigate(['/member/goals']);
      },
    });
  }

  onSubmit() {
    if (this.goalForm.invalid) {
      this.goalForm.markAllAsTouched();
      this.toast.warning('Please complete all required fields before saving.', 'Incomplete Form');
      return;
    }

    this.loading = true;
    const goalData: Goal = this.goalForm.value;

    const request =
      this.isEditMode && this.goalId
        ? this.goalService.updateGoal(this.goalId, goalData)
        : this.goalService.createGoal(goalData);

    request.subscribe({
      next: () => {
        const msg = this.isEditMode ? 'Goal updated successfully!' : 'New goal created!';
        this.toast.success(msg, this.isEditMode ? 'Goal Updated' : 'Goal Created 🎯');
        this.router.navigate(['/member/goals']);
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  goBack() {
    this.router.navigate(['/member/goals']);
  }
}
