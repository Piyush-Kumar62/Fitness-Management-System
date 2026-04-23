import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from './api.service';
import {
  BuyMembershipRequest,
  CreateMembershipPlanRequest,
  Membership,
  MembershipPlan,
  MembershipPurchaseResponse,
  Payment,
} from '../models/membership.model';
import { Page } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class MembershipService {
  private api = inject(ApiService);

  createPlan(payload: CreateMembershipPlanRequest): Observable<MembershipPlan> {
    return this.api.post<MembershipPlan>('membership-plans', payload);
  }

  getPlans(gymId: string, activeOnly = true): Observable<MembershipPlan[]> {
    return this.api.get<MembershipPlan[]>('membership-plans', { gymId, activeOnly });
  }

  buyMembership(payload: BuyMembershipRequest): Observable<MembershipPurchaseResponse> {
    return this.api.post<MembershipPurchaseResponse>('memberships/buy', payload);
  }

  getMembershipHistory(): Observable<Membership[]> {
    return this.api
      .get<Page<Membership>>('memberships/history')
      .pipe(map((response) => response.content ?? []));
  }

  getPaymentHistory(): Observable<Payment[]> {
    return this.api.get<Page<Payment>>('payments/history').pipe(map((response) => response.content ?? []));
  }
}
