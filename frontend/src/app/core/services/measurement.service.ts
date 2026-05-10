import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface BodyMeasurement {
  id?: string;
  userId?: string;
  measurementDate: string;
  weight?: number;
  height?: number;
  bodyFat?: number;
  muscleMass?: number;
  bmi?: number;
  measurements?: Record<string, number>;
  photoUrl?: string;
  notes?: string;
  createdAt?: string;
}

@Injectable({
  providedIn: 'root',
})
export class MeasurementService {
  private api = inject(ApiService);


  getAllMeasurements(): Observable<BodyMeasurement[]> {
    return this.api.get<BodyMeasurement[]>('measurements');
  }

  getMeasurementsByDateRange(startDate: string, endDate: string): Observable<BodyMeasurement[]> {
    return this.api.get<BodyMeasurement[]>('measurements', { startDate, endDate });
  }

  getMeasurementById(id: string): Observable<BodyMeasurement> {
    return this.api.get<BodyMeasurement>(`measurements/${id}`);
  }

  createMeasurement(measurement: BodyMeasurement): Observable<BodyMeasurement> {
    return this.api.post<BodyMeasurement>('measurements', measurement);
  }

  updateMeasurement(id: string, measurement: BodyMeasurement): Observable<BodyMeasurement> {
    return this.api.put<BodyMeasurement>(`measurements/${id}`, measurement);
  }

  deleteMeasurement(id: string): Observable<void> {
    return this.api.delete<void>(`measurements/${id}`);
  }
}
