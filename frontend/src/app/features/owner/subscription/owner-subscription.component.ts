import { Component, OnInit, inject, signal } from '@angular/core';

import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  GymInfo,
  GymSubscription,
  GymSubscriptionPlan,
} from '../../../core/models/subscription.model';
import { GymService } from '../../../core/services/gym.service';
import { SubscriptionService } from '../../../core/services/subscription.service';

@Component({
  selector: 'app-owner-subscription',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './owner-subscription.component.html',
})
export class OwnerSubscriptionComponent implements OnInit {
  private fb = inject(FormBuilder);
  private gymService = inject(GymService);
  private subscriptionService = inject(SubscriptionService);

  gyms = signal<GymInfo[]>([]);
  plans = signal<GymSubscriptionPlan[]>([]);
  currentSubscription = signal<GymSubscription | null>(null);

  form = this.fb.group({
    gymId: ['', [Validators.required]],
  });

  ngOnInit(): void {
    this.gymService.getMyGyms().subscribe((rows) => this.gyms.set(rows));
    this.subscriptionService.getPlans().subscribe((rows) => this.plans.set(rows));
  }

  selectedGymId(): string {
    return this.form.getRawValue().gymId || '';
  }

  loadCurrent(): void {
    const gymId = this.selectedGymId();
    if (!gymId) {
      this.currentSubscription.set(null);
      return;
    }
    this.subscriptionService.getGymSubscription(gymId).subscribe({
      next: (value) => this.currentSubscription.set(value),
      error: () => this.currentSubscription.set(null),
    });
  }

  activate(planId: string): void {
    const gymId = this.selectedGymId();
    if (!gymId) {
      return;
    }
    this.subscriptionService
      .activate(gymId, planId)
      .subscribe((value) => this.currentSubscription.set(value));
  }
}
