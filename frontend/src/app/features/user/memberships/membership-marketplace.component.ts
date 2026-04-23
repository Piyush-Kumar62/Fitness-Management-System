import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MembershipService } from '../../../core/services/membership.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { Membership, MembershipPlan, Payment } from '../../../core/models/membership.model';
import { StripeCheckoutComponent } from '../../../shared/stripe-checkout/stripe-checkout.component';

@Component({
  selector: 'app-membership-marketplace',
  standalone: true,
  imports: [CommonModule, StripeCheckoutComponent],
  templateUrl: './membership-marketplace.component.html',
  styleUrls: ['./membership-marketplace.component.scss'],
})
export class MembershipMarketplaceComponent implements OnInit {
  private membershipService = inject(MembershipService);
  private authService = inject(AuthService);
  private toast = inject(ToastService);

  plans = signal<MembershipPlan[]>([]);
  memberships = signal<Membership[]>([]);
  payments = signal<Payment[]>([]);
  isLoadingPlans = signal(false);

  gymId = computed(() => this.authService.user()?.gymId || '');

  // Currently selected plan for Stripe checkout modal
  selectedPlan = signal<MembershipPlan | null>(null);
  showCheckout = signal(false);

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    const gymId = this.gymId();
    if (gymId) {
      this.isLoadingPlans.set(true);
      this.membershipService.getPlans(gymId).subscribe({
        next: (plans) => this.plans.set(plans),
        complete: () => this.isLoadingPlans.set(false),
      });
    }
    this.membershipService.getMembershipHistory().subscribe((rows) => this.memberships.set(rows));
    this.membershipService.getPaymentHistory().subscribe((rows) => this.payments.set(rows));
  }

  // Check if the member already has an active membership
  hasActiveMembership(): boolean {
    return this.memberships().some((m) => m.status === 'ACTIVE');
  }

  // Open the Stripe payment modal for a plan
  openCheckout(plan: MembershipPlan): void {
    if (this.hasActiveMembership()) {
      this.toast.warning('You already have an active membership. It must expire before purchasing a new one.');
      return;
    }
    this.selectedPlan.set(plan);
    this.showCheckout.set(true);
  }

  // Called by StripeCheckoutComponent on successful payment
  onPaymentSuccess(): void {
    this.showCheckout.set(false);
    this.selectedPlan.set(null);
    this.toast.success('Membership activated! Your dashboard has been updated.');
    setTimeout(() => this.loadData(), 2000); // reload after webhook processes
  }

  // Called by StripeCheckoutComponent when user cancels
  onCheckoutCancelled(): void {
    this.showCheckout.set(false);
    this.selectedPlan.set(null);
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'status-active';
      case 'EXPIRED': return 'status-expired';
      case 'CANCELLED': return 'status-cancelled';
      default: return 'status-default';
    }
  }
}
