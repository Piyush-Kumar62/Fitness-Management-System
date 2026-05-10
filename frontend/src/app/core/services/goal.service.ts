import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Goal {
  id?: string;
  userId?: string;
  title: string;
  description?: string;
  type: GoalType;
  targetValue?: number;
  currentValue?: number;
  unit: string;
  startDate?: string;
  deadline?: string;
  status?: GoalStatus;
  progressPercentage?: number;
  milestones?: Milestone[];
  createdAt?: string;
  updatedAt?: string;
}

export interface Milestone {
  id?: string;
  goalId?: string;
  title: string;
  description?: string;
  targetValue: number;
  achieved?: boolean;
  achievedAt?: string;
  createdAt?: string;
}

export enum GoalType {
  WEIGHT_LOSS = 'WEIGHT_LOSS',
  WEIGHT_GAIN = 'WEIGHT_GAIN',
  MUSCLE_GAIN = 'MUSCLE_GAIN',
  ENDURANCE = 'ENDURANCE',
  STRENGTH = 'STRENGTH',
  FLEXIBILITY = 'FLEXIBILITY',
  HABIT_BUILDING = 'HABIT_BUILDING',
  CUSTOM = 'CUSTOM',
}

export enum GoalStatus {
  ACTIVE = 'ACTIVE',
  COMPLETED = 'COMPLETED',
  ABANDONED = 'ABANDONED',
  PAUSED = 'PAUSED',
}

@Injectable({
  providedIn: 'root',
})
export class GoalService {
  private api = inject(ApiService);


  getAllGoals(): Observable<Goal[]> {
    return this.api.get<Goal[]>('goals');
  }

  getGoalById(id: string): Observable<Goal> {
    return this.api.get<Goal>(`goals/${id}`);
  }

  createGoal(goal: Goal): Observable<Goal> {
    return this.api.post<Goal>('goals', goal);
  }

  updateGoal(id: string, goal: Goal): Observable<Goal> {
    return this.api.put<Goal>(`goals/${id}`, goal);
  }

  deleteGoal(id: string): Observable<void> {
    return this.api.delete<void>(`goals/${id}`);
  }

  addMilestone(goalId: string, milestone: Milestone): Observable<Milestone> {
    return this.api.post<Milestone>(`goals/${goalId}/milestones`, milestone);
  }

  achieveMilestone(milestoneId: string): Observable<Milestone> {
    return this.api.put<Milestone>(`goals/milestones/${milestoneId}/achieve`, {});
  }
}
