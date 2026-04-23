import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { RegisterRequest } from '../../../core/models/auth.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private toast = inject(ToastService);

  isLoading = signal(false);

  registerForm: FormGroup = this.fb.group({
    firstName: ['', [Validators.required, Validators.minLength(2)]],
    lastName: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  onSubmit(): void {
    if (this.registerForm.invalid) {
      const errors: string[] = [];
      const ctrl = (name: string) => this.registerForm.get(name);

      if (ctrl('firstName')?.hasError('required')) errors.push('First name is required.');
      if (ctrl('firstName')?.hasError('minlength')) errors.push('First name must be at least 2 characters.');
      if (ctrl('lastName')?.hasError('required')) errors.push('Last name is required.');
      if (ctrl('lastName')?.hasError('minlength')) errors.push('Last name must be at least 2 characters.');
      if (ctrl('email')?.hasError('required')) errors.push('Email address is required.');
      if (ctrl('email')?.hasError('email')) errors.push('Please enter a valid email address.');
      if (ctrl('password')?.hasError('required')) errors.push('Password is required.');
      if (ctrl('password')?.hasError('minlength')) errors.push('Password must be at least 6 characters.');

      this.toast.validationError('Registration incomplete', errors);
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    const userData: RegisterRequest = this.registerForm.value;

    this.authService.register(userData).subscribe({
      next: () => {
        this.isLoading.set(false);
        // Navigation handled in authService
      },
      error: () => {
        this.isLoading.set(false);
      },
    });
  }

  signUpWithGoogle(): void {
    this.authService.loginWithGoogle();
  }

  signUpWithGithub(): void {
    this.authService.loginWithGithub();
  }
}
