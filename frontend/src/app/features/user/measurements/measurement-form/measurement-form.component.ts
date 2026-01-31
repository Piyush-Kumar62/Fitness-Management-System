import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MeasurementService, BodyMeasurement } from '../../../../core/services/measurement.service';

@Component({
  selector: 'app-measurement-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container mx-auto px-4 py-8 max-w-2xl">
      <h1 class="text-3xl font-bold mb-6">{{isEditMode ? 'Edit' : 'Add'}} Measurement</h1>

      <form [formGroup]="measurementForm" (ngSubmit)="onSubmit()" class="bg-white rounded-lg shadow-md p-6">
        
        <div class="mb-4">
          <label class="block text-gray-700 font-semibold mb-2">Date *</label>
          <input 
            type="date" 
            formControlName="measurementDate"
            class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
        </div>

        <div class="grid grid-cols-2 gap-4 mb-4">
          <div>
            <label class="block text-gray-700 font-semibold mb-2">Weight (kg)</label>
            <input 
              type="number" 
              step="0.1"
              formControlName="weight"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
          </div>
          <div>
            <label class="block text-gray-700 font-semibold mb-2">Height (cm)</label>
            <input 
              type="number" 
              step="0.1"
              formControlName="height"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4 mb-4">
          <div>
            <label class="block text-gray-700 font-semibold mb-2">Body Fat (%)</label>
            <input 
              type="number" 
              step="0.1"
              formControlName="bodyFat"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
          </div>
          <div>
            <label class="block text-gray-700 font-semibold mb-2">Muscle Mass (kg)</label>
            <input 
              type="number" 
              step="0.1"
              formControlName="muscleMass"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
          </div>
        </div>

        <div class="mb-4">
          <label class="block text-gray-700 font-semibold mb-2">Notes</label>
          <textarea 
            formControlName="notes"
            rows="3"
            class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Any observations..."></textarea>
        </div>

        <div class="flex gap-4">
 <button 
            type="submit"
            [disabled]="measurementForm.invalid || loading"
            class="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 disabled:bg-gray-400">
            {{loading ? 'Saving...' : (isEditMode ? 'Update' : 'Save')}}
          </button>
          <button 
            type="button"
            (click)="goBack()"
            class="flex-1 bg-gray-200 text-gray-700 py-2 rounded-lg hover:bg-gray-300">
            Cancel
          </button>
        </div>
      </form>
    </div>
  `,
  styles: []
})
export class MeasurementFormComponent implements OnInit {
  measurementForm: FormGroup;
  isEditMode = false;
  measurementId?: string;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private measurementService: MeasurementService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.measurementForm = this.fb.group({
      measurementDate: [new Date().toISOString().split('T')[0], Validators.required],
      weight: [null],
      height: [null],
      bodyFat: [null],
      muscleMass: [null],
      notes: ['']
    });
  }

  ngOnInit() {
    this.measurementId = this.route.snapshot.paramMap.get('id') || undefined;
    this.isEditMode = !!this.measurementId;

    if (this.isEditMode && this.measurementId) {
      this.loadMeasurement(this.measurementId);
    }
  }

  loadMeasurement(id: string) {
    this.measurementService.getMeasurementById(id).subscribe({
      next: (measurement) => {
        this.measurementForm.patchValue(measurement);
      },
      error: (error) => console.error('Error loading measurement:', error)
    });
  }

  onSubmit() {
    if (this.measurementForm.invalid) return;

    this.loading = true;
    const data: BodyMeasurement = this.measurementForm.value;

    const request = this.isEditMode && this.measurementId
      ? this.measurementService.updateMeasurement(this.measurementId, data)
      : this.measurementService.createMeasurement(data);

    request.subscribe({
      next: () => {
        this.router.navigate(['/user/measurements']);
      },
      error: (error) => {
        console.error('Error saving measurement:', error);
        this.loading = false;
      }
    });
  }

  goBack() {
    this.router.navigate(['/user/measurements']);
  }
}
