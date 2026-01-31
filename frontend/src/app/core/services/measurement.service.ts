import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface BodyMeasurement {
  id?: string;
  userId?: string;
  measurementDate: string;
  weight?: number;
  height?: number;
  bodyFat?: number;
  muscleMass?: number;
  bmi?: number;
  measurements?: { [key: string]: number };
  photoUrl?: string;
  notes?: string;
  createdAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class MeasurementService {
  private apiUrl = `${environment.apiUrl}/measurements`;

  constructor(private http: HttpClient) {}

  getAllMeasurements(): Observable<BodyMeasurement[]> {
    return this.http.get<BodyMeasurement[]>(this.apiUrl);
  }

  getMeasurementsByDateRange(startDate: string, endDate: string): Observable<BodyMeasurement[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<BodyMeasurement[]>(this.apiUrl, { params });
  }

  getMeasurementById(id: string): Observable<BodyMeasurement> {
    return this.http.get<BodyMeasurement>(`${this.apiUrl}/${id}`);
  }

  createMeasurement(measurement: BodyMeasurement): Observable<BodyMeasurement> {
    return this.http.post<BodyMeasurement>(this.apiUrl, measurement);
  }

  updateMeasurement(id: string, measurement: BodyMeasurement): Observable<BodyMeasurement> {
    return this.http.put<BodyMeasurement>(`${this.apiUrl}/${id}`, measurement);
  }

  deleteMeasurement(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
