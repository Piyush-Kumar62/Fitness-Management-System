import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RecommendationService } from '../../../../core/services/recommendation.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-recommendation-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './recommendation-form.component.html',
})
export class RecommendationFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private recommendationService = inject(RecommendationService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private toast = inject(ToastService);

  recommendationForm: FormGroup;
  loading = signal(false);

  types = [
    { value: 'GENERAL', label: 'General' },
    { value: 'IMPROVEMENT', label: 'Improvement' },
    { value: 'ACHIEVEMENT', label: 'Achievement' },
    { value: 'MOTIVATION', label: 'Motivation' },
    { value: 'WARNING', label: 'Warning' },
  ];

  constructor() {
    this.recommendationForm = this.fb.group({
      type: ['GENERAL', Validators.required],
      recommendation: ['', [Validators.required, Validators.minLength(10)]],
      improvements: [''],
      suggestions: [''],
      safety: [''],
    });
  }

  ngOnInit() {
    const user = this.authService.user();
    if (!user) {
      this.toast.error('You must be logged in to create recommendations.');
      this.router.navigate(['/auth/login']);
    }
  }

  onSubmit() {
    if (this.recommendationForm.invalid) {
      this.recommendationForm.markAllAsTouched();
      this.toast.warning('Please fill out the form correctly.');
      return;
    }

    const userId = this.authService.user()?.id;
    if (!userId) {
      this.toast.error('User not authenticated.');
      return;
    }

    this.loading.set(true);
    const formValue = this.recommendationForm.value;

    // Split comma-separated values into arrays
    const improvements = formValue.improvements
      ? formValue.improvements.split(',').map((s: string) => s.trim()).filter((s: string) => s.length > 0)
      : [];
    const suggestions = formValue.suggestions
      ? formValue.suggestions.split(',').map((s: string) => s.trim()).filter((s: string) => s.length > 0)
      : [];
    const safety = formValue.safety
      ? formValue.safety.split(',').map((s: string) => s.trim()).filter((s: string) => s.length > 0)
      : [];

    const requestPayload: any = {
      userId,
      activityId: null,
      type: formValue.type,
      recommendation: formValue.recommendation,
      improvements,
      suggestions,
      safety
    };

    this.recommendationService.createRecommendation(requestPayload).subscribe({
      next: () => {
        this.toast.success('Recommendation created successfully! 🎯');
        this.router.navigate(['/member/recommendations']);
      },
      error: (err) => {
        console.error('Failed to create recommendation:', err);
        this.toast.error('Failed to create recommendation. Please try again.');
        this.loading.set(false);
      }
    });
  }

  goBack() {
    this.router.navigate(['/member/recommendations']);
  }
}
