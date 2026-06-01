import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MembershipPlan } from '../../../core/models/membership.model';
import { MembershipService } from '../../../core/services/membership.service';
import { AuthService } from '../../../core/services/auth.service';
import { GymService } from '../../../core/services/gym.service';
import { GymInfo } from '../../../core/models/subscription.model';
import { CommonModule } from '@angular/common';

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
  private gymService = inject(GymService);

  gyms = signal<GymInfo[]>([]);
  selectedGymId = signal<string>('');
  plans = signal<MembershipPlan[]>([]);
  gymId = computed(() => this.selectedGymId());

  form = this.fb.group({
    name: ['', [Validators.required]],
    price: [999, [Validators.required, Validators.min(1)]],
    durationDays: [30, [Validators.required, Validators.min(1)]],
    features: [''],
  });

  ngOnInit(): void {
    this.loadGyms();
  }

  loadGyms(): void {
    this.gymService.getMyGyms().subscribe({
      next: (rows) => {
        this.gyms.set(rows);
        if (rows.length > 0) {
          this.selectedGymId.set(rows[0].id);
          this.loadPlans();
        }
      },
      error: (err) => console.error('Failed to load gyms:', err)
    });
  }

  onGymChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.selectedGymId.set(target.value);
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
