import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { GoalService, Goal, Milestone, GoalStatus } from '../../../../core/services/goal.service';

@Component({
  selector: 'app-goal-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="container mx-auto px-4 py-8 max-w-4xl" *ngIf="goal">
      <!-- Header -->
      <div class="flex justify-between items-start mb-6">
        <div>
          <a routerLink="/user/goals" class="text-blue-600 hover:underline mb-2 inline-block">← Back to Goals</a>
          <h1 class="text-3xl font-bold">{{goal.title}}</h1>
          <span [class]="getStatusClass(goal.status!)" class="inline-block px-3 py-1 rounded mt-2 text-sm font-semibold">
            {{goal.status}}
          </span>
        </div>
        <div class="flex gap-2">
          <a [routerLink]="['/user/goals/edit', goal.id]" class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700">
            Edit
          </a>
          <button (click)="deleteGoal()" class="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700">
            Delete
          </button>
        </div>
      </div>

      <!-- Goal Info Card -->
      <div class="bg-white rounded-lg shadow-md p-6 mb-6">
        <p *ngIf="goal.description" class="text-gray-700 mb-4">{{goal.description}}</p>
        
        <!-- Progress -->
        <div class="mb-6">
          <div class="flex justify-between mb-2">
            <span class="text-lg font-semibold">Progress</span>
            <span class="text-2xl font-bold text-blue-600">{{goal.progressPercentage?.toFixed(0)}}%</span>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-4 mb-2">
            <div 
              class="bg-blue-600 h-4 rounded-full transition-all"
              [style.width.%]="goal.progressPercentage">
            </div>
          </div>
          <div class="flex justify-between text-sm text-gray-600">
            <span>Current: <strong>{{goal.currentValue}} {{goal.unit}}</strong></span>
            <span>Target: <strong>{{goal.targetValue}} {{goal.unit}}</strong></span>
          </div>
        </div>

        <!-- Details Grid -->
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
          <div>
            <span class="text-gray-600">Type:</span>
            <p class="font-semibold">{{formatGoalType(goal.type)}}</p>
          </div>
          <div *ngIf="goal.startDate">
            <span class="text-gray-600">Started:</span>
            <p class="font-semibold">{{goal.startDate | date}}</p>
          </div>
          <div *ngIf="goal.deadline">
            <span class="text-gray-600">Deadline:</span>
            <p class="font-semibold">{{goal.deadline | date}}</p>
          </div>
          <div>
            <span class="text-gray-600">Created:</span>
            <p class="font-semibold">{{goal.createdAt | date}}</p>
          </div>
        </div>
      </div>

      <!-- Milestones Section -->
      <div class="bg-white rounded-lg shadow-md p-6">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-2xl font-bold">Milestones</h2>
          <button 
            (click)="showMilestoneForm = !showMilestoneForm"
            class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700">
            + Add Milestone
          </button>
        </div>

        <!-- Add Milestone Form -->
        <form *ngIf="showMilestoneForm" [formGroup]="milestoneForm" (ngSubmit)="addMilestone()" class="mb-6 p-4 bg-gray-50 rounded-lg">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label class="block text-sm font-semibold mb-1">Title *</label>
              <input 
                type="text" 
                formControlName="title"
                class="w-full px-3 py-2 border rounded-lg"
                placeholder="First 5kg">
            </div>
            <div>
              <label class="block text-sm font-semibold mb-1">Target Value *</label>
              <input 
                type="number" 
                formControlName="targetValue"
                class="w-full px-3 py-2 border rounded-lg"
                placeholder="85">
            </div>
            <div class="flex items-end gap-2">
              <button 
                type="submit"
                [disabled]="milestoneForm.invalid"
                class="flex-1 bg-green-600 text-white py-2 rounded-lg hover:bg-green-700 disabled:bg-gray-400">
                Add
              </button>
              <button 
                type="button"
                (click)="showMilestoneForm = false"
                class="flex-1 bg-gray-300 text-gray-700 py-2 rounded-lg hover:bg-gray-400">
                Cancel
              </button>
            </div>
          </div>
          <div class="mt-2">
            <label class="block text-sm font-semibold mb-1">Description</label>
            <input 
              type="text" 
              formControlName="description"
              class="w-full px-3 py-2 border rounded-lg"
              placeholder="Lose first 5kg">
          </div>
        </form>

        <!-- Milestones List -->
        <div class="space-y-3">
          <div 
            *ngFor="let milestone of goal.milestones"
            class="flex items-center justify-between p-4 border rounded-lg"
            [class.bg-green-50]="milestone.achieved"
            [class.border-green-300]="milestone.achieved">
            
            <div class="flex-1">
              <div class="flex items-center gap-2">
                <span *ngIf="milestone.achieved" class="text-green-600 text-xl">✓</span>
                <h3 class="font-semibold" [class.line-through]="milestone.achieved">
                  {{milestone.title}}
                </h3>
              </div>
              <p *ngIf="milestone.description" class="text-sm text-gray-600">{{milestone.description}}</p>
              <p class="text-sm text-gray-500 mt-1">
                Target: <strong>{{milestone.targetValue}} {{goal.unit}}</strong>
              </p>
              <p *ngIf="milestone.achievedAt" class="text-sm text-green-600 mt-1">
                ✓ Achieved on {{milestone.achievedAt | date}}
              </p>
            </div>

            <button 
              *ngIf="!milestone.achieved"
              (click)="achieveMilestone(milestone.id!)"
              class="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700">
              Mark Achieved
            </button>
          </div>

          <p *ngIf="!goal.milestones || goal.milestones.length === 0" class="text-gray-500 text-center py-8">
            No milestones yet. Add one to track your progress!
          </p>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class GoalDetailComponent implements OnInit {
  goal?: Goal;
  showMilestoneForm = false;
  milestoneForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private goalService: GoalService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.milestoneForm = this.fb.group({
      title: ['', Validators.required],
      description: [''],
      targetValue: [null, Validators.required]
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
      error: (error) => console.error('Error loading goal:', error)
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
      },
      error: (error) => console.error('Error adding milestone:', error)
    });
  }

  achieveMilestone(milestoneId: string) {
    this.goalService.achieveMilestone(milestoneId).subscribe({
      next: () => {
        this.loadGoal(this.goal!.id!);
      },
      error: (error) => console.error('Error achieving milestone:', error)
    });
  }

  deleteGoal() {
    if (!this.goal?.id) return;
    if (!confirm('Are you sure you want to delete this goal?')) return;

    this.goalService.deleteGoal(this.goal.id).subscribe({
      next: () => {
        this.router.navigate(['/user/goals']);
      },
      error: (error) => console.error('Error deleting goal:', error)
    });
  }

  getStatusClass(status: GoalStatus): string {
    const classes: Record<GoalStatus, string> = {
      [GoalStatus.ACTIVE]: 'bg-green-100 text-green-800',
      [GoalStatus.COMPLETED]: 'bg-blue-100 text-blue-800',
      [GoalStatus.PAUSED]: 'bg-yellow-100 text-yellow-800',
      [GoalStatus.ABANDONED]: 'bg-gray-100 text-gray-800'
    };
    return classes[status] || 'bg-gray-100 text-gray-800';
  }

  formatGoalType(type: any): string {
    return type.replace(/_/g, ' ');
  }
}
