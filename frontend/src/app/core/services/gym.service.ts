import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { GymInfo } from '../models/subscription.model';

@Injectable({ providedIn: 'root' })
export class GymService {
  private api = inject(ApiService);

  getMyGyms(): Observable<GymInfo[]> {
    return this.api.get<GymInfo[]>('gyms/my');
  }

  getAllGyms(): Observable<GymInfo[]> {
    return this.api.get<GymInfo[]>('gyms');
  }

  getPublicGyms(): Observable<GymInfo[]> {
    return this.api.get<GymInfo[]>('gyms/public');
  }

  joinGym(gymId: string): Observable<void> {
    return this.api.post<void>(`gyms/${gymId}/join`, {});
  }

  createGym(payload: { name: string; address: string; contact: string }): Observable<GymInfo> {
    return this.api.post<GymInfo>('gyms', payload);
  }

  updateGym(gymId: string, payload: { name: string; address: string; contact: string }): Observable<GymInfo> {
    return this.api.put<GymInfo>(`gyms/${gymId}`, payload);
  }

  deleteGym(gymId: string): Observable<void> {
    return this.api.delete<void>(`gyms/${gymId}`);
  }
}
