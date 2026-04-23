import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MeasurementService, BodyMeasurement } from '../../../../core/services/measurement.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-measurement-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './measurement-list.component.html',
  styles: [],
})
export class MeasurementListComponent implements OnInit {
  measurements: BodyMeasurement[] = [];

  constructor(
    private measurementService: MeasurementService,
    private toastService: ToastService,
  ) {}

  ngOnInit() {
    this.loadMeasurements();
  }

  loadMeasurements() {
    this.measurementService.getAllMeasurements().subscribe({
      next: (measurements) => {
        this.measurements = measurements;
      },
      error: (error) => {
        console.error('Error loading measurements:', error);
        this.toastService.error('Failed to load measurements');
      },
    });
  }

  async deleteMeasurement(id: string): Promise<void> {
    const confirmed = await this.toastService.confirm(
      'Delete this measurement?',
      'This action cannot be undone.',
      'Delete measurement',
    );
    if (!confirmed) return;

    this.measurementService.deleteMeasurement(id).subscribe({
      next: () => {
        this.toastService.success('Measurement deleted');
        this.loadMeasurements();
      },
      error: (error) => {
        console.error('Error deleting measurement:', error);
        this.toastService.error('Failed to delete measurement');
      },
    });
  }

  getBMIClass(bmi: number): string {
    if (bmi < 18.5) return 'bg-blue-100 text-blue-800';
    if (bmi < 25) return 'bg-green-100 text-green-800';
    if (bmi < 30) return 'bg-yellow-100 text-yellow-800';
    return 'bg-red-100 text-red-800';
  }

  getBarHeight(m: BodyMeasurement): number {
    if (!m.weight) return 0;
    const maxWeight = Math.max(...this.measurements.filter((m) => m.weight).map((m) => m.weight!));
    return (m.weight / maxWeight) * 100;
  }
}
