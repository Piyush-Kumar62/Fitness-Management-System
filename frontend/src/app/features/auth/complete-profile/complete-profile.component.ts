import { Component, inject, signal } from '@angular/core';

import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { UserRole } from '../../../core/models/user.model';

@Component({
  selector: 'app-complete-profile',
  standalone: true,
  imports: [],
  templateUrl: './complete-profile.component.html',
})
export class CompleteProfileComponent {
  private authService = inject(AuthService);
  private toast = inject(ToastService);

  isSubmitting = signal(false);
  selectedRole = signal<UserRole | null>(null);

  availableRoles: { value: UserRole; label: string; description: string }[] = [
    {
      value: UserRole.MEMBER,
      label: 'Member',
      description: 'Track activities, goals, and memberships.',
    },
    {
      value: UserRole.TRAINER,
      label: 'Trainer',
      description: 'Manage members and training plans.',
    },
    {
      value: UserRole.OWNER,
      label: 'Owner',
      description: 'Manage gyms, trainers, and business modules.',
    },
  ];

  chooseRole(role: UserRole): void {
    this.selectedRole.set(role);
  }

  submit(): void {
    const role = this.selectedRole();
    if (!role) {
      this.toast.warning('Please select your role to continue');
      return;
    }

    this.isSubmitting.set(true);
    this.authService.completeProfile(role).subscribe({
      next: () => {
        this.toast.success('Profile submitted. Please wait for admin approval and email verification.');
        this.authService.finishOnboarding();
      },
      error: () => {
        this.isSubmitting.set(false);
      },
    });
  }
}
