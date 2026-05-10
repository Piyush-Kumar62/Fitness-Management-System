import { Component, OnInit, inject, signal } from '@angular/core';

import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { TrainerService } from '../../../../core/services/trainer.service';

@Component({
  selector: 'app-workout-plan-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './workout-plan-form.component.html',
})
export class WorkoutPlanFormComponent implements OnInit {
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
    difficulty: ['BEGINNER', [Validators.required]],
    durationWeeks: [4, [Validators.required, Validators.min(1)]],
    exercises: this.fb.array([]),
  });

  get exercises(): FormArray {
    return this.planForm.get('exercises') as FormArray;
  }

  ngOnInit() {
    this.planId = this.route.snapshot.paramMap.get('id');
    if (this.planId) {
      this.isEditMode.set(true);
      this.loadPlan(this.planId);
    }
  }

  addExercise(): void {
    this.exercises.push(
      this.fb.group({
        name: ['', Validators.required],
        sets: [3],
        reps: [10],
        duration: [null],
        day: ['MONDAY'],
        restSeconds: [60],
      }),
    );
  }

  removeExercise(index: number): void {
    this.exercises.removeAt(index);
  }

  onSubmit(): void {
    if (this.planForm.valid) {
      this.isSubmitting.set(true);
      const payload = this.toRequestPayload();
      const request$ =
        this.isEditMode() && this.planId
          ? this.trainerService.updateWorkoutPlan(this.planId, payload)
          : this.trainerService.createWorkoutPlan(payload);
      request$.subscribe({
        next: () => this.router.navigate(['/trainer/workout-plans']),
        complete: () => this.isSubmitting.set(false),
        error: () => this.isSubmitting.set(false),
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/trainer/workout-plans']);
  }

  private loadPlan(planId: string): void {
    this.trainerService.getWorkoutPlanById(planId).subscribe((plan) => {
      this.planForm.patchValue({
        title: plan.title,
        description: plan.description,
        difficulty: plan.difficulty,
        durationWeeks: plan.durationWeeks,
      });
      this.exercises.clear();
      plan.exercises.forEach((exercise) => {
        this.exercises.push(
          this.fb.group({
            name: [exercise.name, Validators.required],
            sets: [exercise.sets ?? 3],
            reps: [exercise.reps ?? 10],
            duration: [exercise.durationMinutes ?? null],
            day: [exercise.day ?? 'MONDAY'],
            restSeconds: [exercise.restSeconds ?? 60],
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
      difficulty: value.difficulty,
      durationWeeks: value.durationWeeks,
      exercises: value.exercises.map((exercise: any) => ({
        name: exercise.name,
        sets: exercise.sets,
        reps: exercise.reps,
        durationMinutes: exercise.duration,
        day: exercise.day,
        restSeconds: exercise.restSeconds,
      })),
    };
  }
}
