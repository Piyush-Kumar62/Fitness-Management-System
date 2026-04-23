import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { GymSubscription, GymSubscriptionPlan } from '../models/subscription.model';

@Injectable({ providedIn: 'root' })
export class SubscriptionService {
  private api = inject(ApiService);

  getPlans(): Observable<GymSubscriptionPlan[]> {
    return this.api.get<GymSubscriptionPlan[]>('subscriptions/plans', { activeOnly: true });
  }

  activate(gymId: string, planId: string): Observable<GymSubscription> {
    return this.api.post<GymSubscription>('subscriptions/activate', { gymId, planId });
  }

  getGymSubscription(gymId: string): Observable<GymSubscription> {
    return this.api.get<GymSubscription>(`subscriptions/gym/${gymId}`);
  }
}
