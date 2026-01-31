import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { UserService } from '../../../../core/services/user.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.scss'],
})
export class UserFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private toast = inject(ToastService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  userForm!: FormGroup;
  isSubmitting = signal(false);
  isEditMode = signal(false);
  userId = signal<string | null>(null);

  ngOnInit(): void {
    this.initForm();
    this.checkEditMode();
  }

  private initForm(): void {
    this.userForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.minLength(8)]],
      role: ['USER', Validators.required],
    });
  }

  private checkEditMode(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.userId.set(id);
      this.isEditMode.set(true);
      this.loadUser(id);
      // Password not required in edit mode
      this.userForm.get('password')?.clearValidators();
      this.userForm.get('password')?.updateValueAndValidity();
    } else {
      // Password required in create mode
      this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(8)]);
      this.userForm.get('password')?.updateValueAndValidity();
    }
  }

  private loadUser(id: string): void {
    this.userService.getProfile().subscribe({
      next: (user) => {
        this.userForm.patchValue({
          firstName: user.firstName,
          lastName: user.lastName,
          email: user.email,
          role: user.role,
        });
      },
      error: () => {
        this.toast.error('Failed to load user');
        this.router.navigate(['/admin/users']);
      },
    });
  }

  onSubmit(): void {
    if (this.userForm.invalid) {
      this.markFormGroupTouched(this.userForm);
      this.toast.error('Please fill in all required fields');
      return;
    }

    this.isSubmitting.set(true);

    if (this.isEditMode()) {
      this.updateUser();
    } else {
      this.createUser();
    }
  }

  private createUser(): void {
    // TODO: Implement create user API call
    setTimeout(() => {
      this.toast.success('User created successfully');
      this.router.navigate(['/admin/users']);
    }, 1000);
  }

  private updateUser(): void {
    const id = this.userId();
    if (!id) return;

    const updateData = {
      firstName: this.userForm.value.firstName,
      lastName: this.userForm.value.lastName,
      role: this.userForm.value.role,
    };

    // If password is provided, include it
    if (this.userForm.value.password) {
      Object.assign(updateData, { password: this.userForm.value.password });
    }

    this.userService.updateProfile(updateData).subscribe({
      next: () => {
        this.toast.success('User updated successfully');
        this.router.navigate(['/admin/users']);
      },
      error: () => {
        this.isSubmitting.set(false);
      },
    });
  }

  onCancel(): void {
    this.router.navigate(['/admin/users']);
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach((key) => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.userForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }
}
