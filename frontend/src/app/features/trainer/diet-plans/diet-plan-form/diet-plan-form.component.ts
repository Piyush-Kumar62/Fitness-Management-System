import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { TrainerService } from '../../../../core/services/trainer.service';

@Component({
  selector: 'app-diet-plan-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './diet-plan-form.component.html',
})
export class DietPlanFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private trainerService = inject(TrainerService);
  private planId: string | null = null;

  isEditMode = signal(false);
  isSubmitting = signal(false);

  planForm: FormGroup = this.fb.group({
    title: ['', [Validators.required]],
    description: [''],
    calories: [2000, [Validators.required, Validators.min(500)]],
    protein: [150],
    carbs: [200],
    fat: [70],
    meals: this.fb.array([]),
  });

  get meals(): FormArray {
    return this.planForm.get('meals') as FormArray;
  }

  ngOnInit() {
    this.planId = this.route.snapshot.paramMap.get('id');
    if (this.planId) {
      this.isEditMode.set(true);
      this.loadPlan(this.planId);
    }
  }

  addMeal(): void {
    this.meals.push(
      this.fb.group({
        mealType: ['BREAKFAST', Validators.required],
        name: ['', Validators.required],
        calories: [null],
        description: [''],
      }),
    );
  }

  removeMeal(index: number): void {
    this.meals.removeAt(index);
  }

  onSubmit(): void {
    if (this.planForm.valid) {
      this.isSubmitting.set(true);
      const payload = this.toRequestPayload();
      const request$ =
        this.isEditMode() && this.planId
          ? this.trainerService.updateDietPlan(this.planId, payload)
          : this.trainerService.createDietPlan(payload);
      request$.subscribe({
        next: () => this.router.navigate(['/trainer/diet-plans']),
        complete: () => this.isSubmitting.set(false),
        error: () => this.isSubmitting.set(false),
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/trainer/diet-plans']);
  }

  private loadPlan(planId: string): void {
    this.trainerService.getDietPlanById(planId).subscribe((plan) => {
      this.planForm.patchValue({
        title: plan.title,
        description: plan.description,
        calories: plan.targetCalories,
        protein: plan.targetProtein,
        carbs: plan.targetCarbs,
        fat: plan.targetFat,
      });
      this.meals.clear();
      plan.meals.forEach((meal) => {
        this.meals.push(
          this.fb.group({
            mealType: [meal.mealType, Validators.required],
            name: [meal.name, Validators.required],
            calories: [meal.calories ?? null],
            description: [meal.description ?? ''],
          }),
        );
      });
    });
  }

  private toRequestPayload(): any {
    const value = this.planForm.getRawValue();
    return {
      title: value.title,
      description: value.description,
      targetCalories: value.calories,
      targetProtein: value.protein,
      targetCarbs: value.carbs,
      targetFat: value.fat,
      meals: value.meals,
    };
  }
}
