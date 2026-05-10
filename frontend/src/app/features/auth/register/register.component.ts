import { Component, inject, signal, OnInit } from '@angular/core';

import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { GymService } from '../../../core/services/gym.service';
import { ToastService } from '../../../core/services/toast.service';
import { RegisterRequest } from '../../../core/models/auth.model';
import { GymInfo } from '../../../core/models/subscription.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
})
export class RegisterComponent implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private gymService = inject(GymService);
  private toast = inject(ToastService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  isLoading = signal(false);
  gyms = signal<GymInfo[]>([]);

  ngOnInit(): void {
    const gymId = this.route.snapshot.queryParamMap.get('gymId');
    if (gymId) {
      this.selectedRole.set('MEMBER');
      this.currentStep.set(2);
      this.registerForm.patchValue({ gymId });
      this.loadGyms();
    }
  }
  
  // Step tracking: 1 = Role Selection, 2 = Account Info, 3 = Personal Details, 4 = Gym Details (Owner) or Final Review (Member)
  currentStep = signal(1);
  selectedRole = signal<'MEMBER' | 'OWNER' | null>(null);

  registerForm: FormGroup = this.fb.group({
    firstName: ['', [Validators.required, Validators.minLength(2)]],
    lastName: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    // Member specific
    phone: [''],
    dob: [''],
    gender: [''],
    gymId: [''],
    // Owner specific
    gymName: [''],
    gymAddress: [''],
    gymContact: ['']
  });

  selectRole(role: 'MEMBER' | 'OWNER'): void {
    this.selectedRole.set(role);
    this.currentStep.set(2);
    
    // Update validators based on role
    if (role === 'OWNER') {
      this.registerForm.get('gymName')?.setValidators([Validators.required]);
      this.registerForm.get('gymContact')?.setValidators([Validators.required]);
    } else {
      this.registerForm.get('gymName')?.clearValidators();
      this.registerForm.get('gymContact')?.clearValidators();
      this.loadGyms();
    }
    this.registerForm.get('gymName')?.updateValueAndValidity();
    this.registerForm.get('gymContact')?.updateValueAndValidity();
  }

  loadGyms(): void {
    this.gymService.getPublicGyms().subscribe({
      next: (gyms) => this.gyms.set(gyms),
      error: () => this.toast.error('Failed to load gyms. You can join a gym later from your dashboard.')
    });
  }

  nextStep(): void {
    const current = this.currentStep();
    
    // Validate current step fields before proceeding
    if (current === 2) {
      if (this.isFieldsInvalid(['firstName', 'lastName', 'email', 'password'])) return;
    } else if (current === 3 && this.selectedRole() === 'OWNER') {
      // Owners have an extra step for Gym details
      this.currentStep.set(4);
      return;
    } else if (current === 3) {
      // Members go to submit after step 3
      this.onSubmit();
      return;
    } else if (current === 4) {
      this.onSubmit();
      return;
    }
    
    this.currentStep.set(current + 1);
  }

  private isFieldsInvalid(fields: string[]): boolean {
    let invalid = false;
    fields.forEach(field => {
      const control = this.registerForm.get(field);
      if (control?.invalid) {
        control.markAsTouched();
        invalid = true;
      }
    });
    return invalid;
  }
  
  goBack(): void {
    const current = this.currentStep();
    if (current <= 1) return;
    
    if (current === 2) {
      this.selectedRole.set(null);
    }
    this.currentStep.set(current - 1);
  }

  onSubmit(): void {
    if (this.registerForm.invalid || !this.selectedRole()) {
      this.toast.validationError('Registration incomplete', ['Please fill in all required fields.']);
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    const userData: RegisterRequest = {
      ...this.registerForm.value,
      role: this.selectedRole()
    };

    this.authService.register(userData).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.toast.success(
          'Registration successful! You can now log in with your credentials.',
          'Welcome to FitPro!'
        );
        this.router.navigate(['/auth/login']);
      },
      error: () => {
        this.isLoading.set(false);
      },
    });
  }

  signUpWithGoogle(): void {
    this.authService.loginWithGoogle();
  }
}
