import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MeasurementService, BodyMeasurement } from '../../../../core/services/measurement.service';

@Component({
  selector: 'app-measurement-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container mx-auto px-4 py-8">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold">Body Measurements</h1>
        <a routerLink="/user/measurements/new" class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700">
          + Add Measurement
        </a>
      </div>

      <!-- Measurements Table -->
      <div class="bg-white rounded-lg shadow-md overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Weight</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Height</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">BMI</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Body Fat %</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr *ngFor="let m of measurements" class="hover:bg-gray-50">
              <td class="px-6 py-4">{{m.measurementDate | date}}</td>
              <td class="px-6 py-4">{{m.weight ? m.weight + ' kg' : '-'}}</td>
              <td class="px-6 py-4">{{m.height ? m.height + ' cm' : '-'}}</td>
              <td class="px-6 py-4">
                <span *ngIf="m.bmi" [class]="getBMIClass(m.bmi)" class="px-2 py-1 rounded text-sm font-semibold">
                  {{m.bmi.toFixed(1)}}
                </span>
                <span *ngIf="!m.bmi">-</span>
              </td>
              <td class="px-6 py-4">{{m.bodyFat ? m.bodyFat + '%' : '-'}}</td>
              <td class="px-6 py-4">
                <div class="flex gap-2">
                  <a [routerLink]="['/user/measurements/edit', m.id]" class="text-blue-600 hover:underline">Edit</a>
                  <button (click)="deleteMeasurement(m.id!)" class="text-red-600 hover:underline">Delete</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div *ngIf="measurements.length === 0" class="text-center py-12">
          <p class="text-gray-500 mb-4">No measurements yet</p>
          <a routerLink="/user/measurements/new" class="text-blue-600 hover:underline">Add your first measurement</a>
        </div>
      </div>

      <!-- Chart Section -->
      <div *ngIf="measurements.length > 0" class="mt-8 bg-white rounded-lg shadow-md p-6">
        <h2 class="text-2xl font-bold mb-4">Weight Progress</h2>
        <div class="h-64 flex items-end justify-around gap-2">
          <div *ngFor="let m of measurements.slice(-10)" class="flex-1 bg-blue-600 rounded-t" 
            [style.height.%]="getBarHeight(m)" title="{{m.measurementDate | date}}: {{m.weight}}kg">
            <div class="text-xs text-center text-white pt-2">{{m.weight}}</div>
          </div>
        </div>
        <div class="flex justify-around mt-2 text-xs text-gray-600">
          <span *ngFor="let m of measurements.slice(-10)">{{m.measurementDate | date: 'MM/dd'}}</span>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class MeasurementListComponent implements OnInit {
  measurements: BodyMeasurement[] = [];

  constructor(private measurementService: MeasurementService) {}

  ngOnInit() {
    this.loadMeasurements();
  }

  loadMeasurements() {
    this.measurementService.getAllMeasurements().subscribe({
      next: (measurements) => {
        this.measurements = measurements;
      },
      error: (error) => console.error('Error loading measurements:', error)
    });
  }

  deleteMeasurement(id: string) {
    if (!confirm('Delete this measurement?')) return;
    
    this.measurementService.deleteMeasurement(id).subscribe({
      next: () => this.loadMeasurements(),
      error: (error) => console.error('Error deleting measurement:', error)
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
    const maxWeight = Math.max(...this.measurements.filter(m => m.weight).map(m => m.weight!));
    return (m.weight / maxWeight) * 100;
  }
}
