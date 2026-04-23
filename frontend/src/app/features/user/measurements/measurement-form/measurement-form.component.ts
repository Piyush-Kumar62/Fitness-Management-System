import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MeasurementService, BodyMeasurement } from '../../../../core/services/measurement.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-measurement-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './measurement-form.component.html',
  styles: [],
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
    private route: ActivatedRoute,
    private toast: ToastService,
  ) {
    this.measurementForm = this.fb.group({
      measurementDate: [new Date().toISOString().split('T')[0], Validators.required],
      weight: [null],
      height: [null],
      bodyFat: [null],
      muscleMass: [null],
      notes: [''],
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
      error: () => {
        this.toast.error('Could not load measurement record. It may have been deleted.', 'Not Found');
        this.router.navigate(['/member/measurements']);
      },
    });
  }

  onSubmit() {
    if (this.measurementForm.invalid) {
      this.measurementForm.markAllAsTouched();
      this.toast.warning('Please provide a measurement date before saving.', 'Required Field');
      return;
    }

    this.loading = true;
    const data: BodyMeasurement = this.measurementForm.value;

    const request =
      this.isEditMode && this.measurementId
        ? this.measurementService.updateMeasurement(this.measurementId, data)
        : this.measurementService.createMeasurement(data);

    request.subscribe({
      next: () => {
        this.toast.success(
          this.isEditMode ? 'Measurement record updated.' : 'Body measurement recorded! 📊',
          this.isEditMode ? 'Record Updated' : 'Measurement Saved',
        );
        this.router.navigate(['/member/measurements']);
      },
      error: () => {
        this.toast.error('Failed to save the measurement. Please try again.');
        this.loading = false;
      },
    });
  }

  goBack() {
    this.router.navigate(['/member/measurements']);
  }
}
