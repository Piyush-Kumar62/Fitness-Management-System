import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

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
  CUSTOM = 'CUSTOM'
}

export enum GoalStatus {
  ACTIVE = 'ACTIVE',
  COMPLETED = 'COMPLETED',
  ABANDONED = 'ABANDONED',
  PAUSED = 'PAUSED'
}

@Injectable({
  providedIn: 'root'
})
export class GoalService {
  private apiUrl = `${environment.apiUrl}/goals`;

  constructor(private http: HttpClient) {}

  getAllGoals(): Observable<Goal[]> {
    return this.http.get<Goal[]>(this.apiUrl);
  }

  getGoalById(id: string): Observable<Goal> {
    return this.http.get<Goal>(`${this.apiUrl}/${id}`);
  }

  createGoal(goal: Goal): Observable<Goal> {
    return this.http.post<Goal>(this.apiUrl, goal);
  }

  updateGoal(id: string, goal: Goal): Observable<Goal> {
    return this.http.put<Goal>(`${this.apiUrl}/${id}`, goal);
  }

  deleteGoal(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  addMilestone(goalId: string, milestone: Milestone): Observable<Milestone> {
    return this.http.post<Milestone>(`${this.apiUrl}/${goalId}/milestones`, milestone);
  }

  achieveMilestone(milestoneId: string): Observable<Milestone> {
    return this.http.put<Milestone>(`${this.apiUrl}/milestones/${milestoneId}/achieve`, {});
  }
}
