import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { GoalService, Goal, GoalStatus, GoalType } from '../../../../core/services/goal.service';

@Component({
  selector: 'app-goal-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container mx-auto px-4 py-8">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold">My Goals</h1>
        <a routerLink="/user/goals/new" class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700">
          + New Goal
        </a>
      </div>

      <!-- Filter Tabs -->
      <div class="mb-6 flex gap-2">
        <button 
          *ngFor="let status of statuses"
          (click)="filterByStatus(status.value)"
          [class.bg-blue-600]="selectedStatus === status.value"
          [class.text-white]="selectedStatus === status.value"
          [class.bg-gray-200]="selectedStatus !== status.value"
          class="px-4 py-2 rounded-lg">
          {{status.label}}
        </button>
      </div>

      <!-- Goals Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div *ngFor="let goal of filteredGoals" 
          class="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow cursor-pointer"
          [routerLink]="['/user/goals', goal.id]">
          
          <!-- Goal Header -->
          <div class="flex justify-between items-start mb-4">
            <h3 class="text-xl font-semibold">{{goal.title}}</h3>
            <span [class]="getStatusClass(goal.status!)" class="px-2 py-1 rounded text-xs font-semibold">
              {{goal.status}}
            </span>
          </div>

          <!-- Goal Type -->
          <p class="text-gray-600 text-sm mb-3">
            <span class="font-medium">Type:</span> {{formatGoalType(goal.type)}}
          </p>

          <!-- Progress Bar -->
          <div class="mb-3">
            <div class="flex justify-between text-sm mb-1">
              <span>Progress</span>
              <span class="font-semibold">{{goal.progressPercentage?.toFixed(0)}}%</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div 
                class="bg-blue-600 h-2 rounded-full transition-all"
                [style.width.%]="goal.progressPercentage">
              </div>
            </div>
          </div>

          <!-- Current vs Target -->
          <div class="flex justify-between text-sm text-gray-600">
            <span>Current: <strong>{{goal.currentValue}} {{goal.unit}}</strong></span>
            <span>Target: <strong>{{goal.targetValue}} {{goal.unit}}</strong></span>
          </div>

          <!-- Deadline -->
          <div *ngIf="goal.deadline" class="mt-3 text-sm text-gray-500">
            <span>📅 Deadline: {{goal.deadline | date}}</span>
          </div>

          <!-- Milestones Count -->
          <div *ngIf="goal.milestones && goal.milestones.length > 0" class="mt-3 text-sm text-blue-600">
            🎯 {{getAchievedMilestones(goal)}} / {{goal.milestones.length}} milestones achieved
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div *ngIf="filteredGoals.length === 0" class="text-center py-12">
        <p class="text-gray-500 text-lg mb-4">No goals found</p>
        <a routerLink="/user/goals/new" class="text-blue-600 hover:underline">Create your first goal</a>
      </div>
    </div>
  `,
  styles: []
})
export class GoalListComponent implements OnInit {
  goals: Goal[] = [];
  filteredGoals: Goal[] = [];
  selectedStatus: string | null = null;

  statuses = [
    { value: null, label: 'All' },
    { value: GoalStatus.ACTIVE, label: 'Active' },
    { value: GoalStatus.COMPLETED, label: 'Completed' },
    { value: GoalStatus.PAUSED, label: 'Paused' }
  ];

  constructor(private goalService: GoalService) {}

  ngOnInit() {
    this.loadGoals();
  }

  loadGoals() {
    this.goalService.getAllGoals().subscribe({
      next: (goals) => {
        this.goals = goals;
        this.filterByStatus(this.selectedStatus);
      },
      error: (error) => console.error('Error loading goals:', error)
    });
  }

  filterByStatus(status: string | null) {
    this.selectedStatus = status;
    if (status) {
      this.filteredGoals = this.goals.filter(g => g.status === status);
    } else {
      this.filteredGoals = [...this.goals];
    }
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

  formatGoalType(type: GoalType): string {
    return type.replace(/_/g, ' ');
  }

  getAchievedMilestones(goal: Goal): number {
    return goal.milestones?.filter(m => m.achieved).length || 0;
  }
}
