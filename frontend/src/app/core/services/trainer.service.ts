import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from './api.service';
import {
  AssignPlanRequest,
  DietPlan,
  MemberCurrentPlansResponse,
  MemberPlanResponse,
  MemberProgress,
  WorkoutPlan,
} from '../models/trainer.model';
import { User } from '../models/user.model';
import { Page } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class TrainerService {
  private api = inject(ApiService);

  getAssignedMembers(): Observable<User[]> {
    return this.api.get<Page<User>>('trainer/members').pipe(map((res) => res.content ?? []));
  }

  getMemberProgress(memberId: string): Observable<MemberProgress> {
    return this.api.get<MemberProgress>(`trainer/members/${memberId}/progress`);
  }

  getWorkoutPlans(): Observable<WorkoutPlan[]> {
    return this.api
      .get<Page<WorkoutPlan>>('trainer/workout-plans')
      .pipe(map((res) => res.content ?? []));
  }

  getWorkoutPlanById(planId: string): Observable<WorkoutPlan> {
    return this.api.get<WorkoutPlan>(`trainer/workout-plans/${planId}`);
  }

  createWorkoutPlan(payload: any): Observable<WorkoutPlan> {
    return this.api.post<WorkoutPlan>('trainer/workout-plans', payload);
  }

  updateWorkoutPlan(planId: string, payload: any): Observable<WorkoutPlan> {
    return this.api.put<WorkoutPlan>(`trainer/workout-plans/${planId}`, payload);
  }

  getDietPlans(): Observable<DietPlan[]> {
    return this.api.get<Page<DietPlan>>('trainer/diet-plans').pipe(map((res) => res.content ?? []));
  }

  getDietPlanById(planId: string): Observable<DietPlan> {
    return this.api.get<DietPlan>(`trainer/diet-plans/${planId}`);
  }

  createDietPlan(payload: any): Observable<DietPlan> {
    return this.api.post<DietPlan>('trainer/diet-plans', payload);
  }

  updateDietPlan(planId: string, payload: any): Observable<DietPlan> {
    return this.api.put<DietPlan>(`trainer/diet-plans/${planId}`, payload);
  }

  assignPlan(payload: AssignPlanRequest): Observable<MemberPlanResponse> {
    return this.api.post<MemberPlanResponse>('trainer/member-plans/assign', payload);
  }

  getCurrentMemberPlans(): Observable<MemberCurrentPlansResponse> {
    return this.api.get<MemberCurrentPlansResponse>('member/plans/current');
  }
}
