import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MembershipPlan } from '../../../core/models/membership.model';
import { MembershipService } from '../../../core/services/membership.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-owner-membership-plans',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './owner-membership-plans.component.html',
})
export class OwnerMembershipPlansComponent implements OnInit {
  private fb = inject(FormBuilder);
  private membershipService = inject(MembershipService);
  private authService = inject(AuthService);

  plans = signal<MembershipPlan[]>([]);
  gymId = computed(() => this.authService.user()?.gymId || '');

  form = this.fb.group({
    name: ['', [Validators.required]],
    price: [999, [Validators.required, Validators.min(1)]],
    durationDays: [30, [Validators.required, Validators.min(1)]],
    features: [''],
  });

  ngOnInit(): void {
    this.loadPlans();
  }

  loadPlans(): void {
    const gymId = this.gymId();
    if (!gymId) {
      return;
    }
    this.membershipService.getPlans(gymId, false).subscribe((rows) => this.plans.set(rows));
  }

  createPlan(): void {
    const gymId = this.gymId();
    if (this.form.invalid || !gymId) {
      return;
    }
    this.membershipService.createPlan({ gymId, ...(this.form.getRawValue() as any) }).subscribe({
      next: () => {
        this.form.patchValue({ name: '', features: '' });
        this.loadPlans();
      },
    });
  }
}
