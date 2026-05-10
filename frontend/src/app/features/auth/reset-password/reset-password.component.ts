import { Component, inject, signal } from '@angular/core';

import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss'],
})
export class ResetPasswordComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private apiService = inject(ApiService);
  private toast = inject(ToastService);
  private router = inject(Router);

  isLoading = signal(false);

  resetForm: FormGroup = this.fb.group({
    currentPassword: ['', [Validators.required]],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]],
  }, { validators: this.passwordMatchValidator });

  passwordMatchValidator(g: FormGroup) {
    return g.get('newPassword')?.value === g.get('confirmPassword')?.value
      ? null : { mismatch: true };
  }

  onSubmit(): void {
    if (this.resetForm.invalid) {
      this.toast.validationError('Please fix the errors in the form.', []);
      this.resetForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    const { currentPassword, newPassword } = this.resetForm.value;

    this.apiService.post('auth/reset-password', { currentPassword, newPassword }).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.toast.success('Password updated successfully! Please log in with your new password.', 'Password Reset Complete');
        this.authService.logout();
      },
      error: (err) => {
        this.isLoading.set(false);
        const msg = err?.error?.message || 'Failed to update password. Please ensure your current password is correct.';
        this.toast.error(msg, 'Password Reset Failed');
      }
    });
  }
}
