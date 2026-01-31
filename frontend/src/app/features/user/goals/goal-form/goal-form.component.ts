import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { GoalService, Goal, GoalType, GoalStatus } from '../../../../core/services/goal.service';

@Component({
  selector: 'app-goal-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container mx-auto px-4 py-8 max-w-2xl">
      <h1 class="text-3xl font-bold mb-6">{{isEditMode ? 'Edit Goal' : 'Create New Goal'}}</h1>

      <form [formGroup]="goalForm" (ngSubmit)="onSubmit()" class="bg-white rounded-lg shadow-md p-6">
        
        <!-- Title -->
        <div class="mb-4">
          <label class="block text-gray-700 font-semibold mb-2">Title *</label>
          <input 
            type="text" 
            formControlName="title"
            class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="e.g., Lose 10kg">
          <div *ngIf="goalForm.get('title')?.invalid && goalForm.get('title')?.touched" class="text-red-500 text-sm mt-1">
            Title is required
          </div>
        </div>

        <!-- Description -->
        <div class="mb-4">
          <label class="block text-gray-700 font-semibold mb-2">Description</label>
          <textarea 
            formControlName="description"
            rows="3"
            class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Describe your goal..."></textarea>
        </div>

        <!-- Type -->
        <div class="mb-4">
          <label class="block text-gray-700 font-semibold mb-2">Goal Type *</label>
          <select 
            formControlName="type"
            class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
            <option value="">Select type...</option>
            <option *ngFor="let type of goalTypes" [value]="type.value">{{type.label}}</option>
          </select>
        </div>

        <!-- Target & Current Value -->
        <div class="grid grid-cols-2 gap-4 mb-4">
          <div>
            <label class="block text-gray-700 font-semibold mb-2">Target Value</label>
            <input 
              type="number" 
              formControlName="targetValue"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="80">
          </div>
          <div>
            <label class="block text-gray-700 font-semibold mb-2">Current Value</label>
            <input 
              type="number" 
              formControlName="currentValue"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="90">
          </div>
        </div>

        <!-- Unit -->
        <div class="mb-4">
          <label class="block text-gray-700 font-semibold mb-2">Unit *</label>
          <input 
            type="text" 
            formControlName="unit"
            class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="e.g., kg, hours, count">
        </div>

        <!-- Dates -->
        <div class="grid grid-cols-2 gap-4 mb-4">
          <div>
            <label class="block text-gray-700 font-semibold mb-2">Start Date</label>
            <input 
              type="date" 
              formControlName="startDate"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
          </div>
          <div>
            <label class="block text-gray-700 font-semibold mb-2">Deadline</label>
            <input 
              type="date" 
              formControlName="deadline"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
          </div>
        </div>

        <!-- Status (edit mode only) -->
        <div *ngIf="isEditMode" class="mb-6">
          <label class="block text-gray-700 font-semibold mb-2">Status</label>
          <select 
            formControlName="status"
            class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
            <option *ngFor="let status of goalStatuses" [value]="status.value">{{status.label}}</option>
          </select>
        </div>

        <!-- Buttons -->
        <div class="flex gap-4">
          <button 
            type="submit"
            [disabled]="goalForm.invalid || loading"
            class="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 disabled:bg-gray-400">
            {{loading ? 'Saving...' : (isEditMode ? 'Update Goal' : 'Create Goal')}}
          </button>
          <button 
            type="button"
            (click)="goBack()"
            class="flex-1 bg-gray-200 text-gray-700 py-2 rounded-lg hover:bg-gray-300">
            Cancel
          </button>
        </div>
      </form>
    </div>
  `,
  styles: []
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
    { value: GoalType.CUSTOM, label: 'Custom' }
  ];

  goalStatuses = [
    { value: GoalStatus.ACTIVE, label: 'Active' },
    { value: GoalStatus.PAUSED, label: 'Paused' },
    { value: GoalStatus.COMPLETED, label: 'Completed' },
    { value: GoalStatus.ABANDONED, label: 'Abandoned' }
  ];

  constructor(
    private fb: FormBuilder,
    private goalService: GoalService,
    private router: Router,
    private route: ActivatedRoute
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
      status: [GoalStatus.ACTIVE]
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
      error: (error) => console.error('Error loading goal:', error)
    });
  }

  onSubmit() {
    if (this.goalForm.invalid) return;

    this.loading = true;
    const goalData: Goal = this.goalForm.value;

    const request = this.isEditMode && this.goalId
      ? this.goalService.updateGoal(this.goalId, goalData)
      : this.goalService.createGoal(goalData);

    request.subscribe({
      next: () => {
        this.router.navigate(['/user/goals']);
      },
      error: (error) => {
        console.error('Error saving goal:', error);
        this.loading = false;
      }
    });
  }

  goBack() {
    this.router.navigate(['/user/goals']);
  }
}
