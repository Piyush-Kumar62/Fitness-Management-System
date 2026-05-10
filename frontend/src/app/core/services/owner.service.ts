import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Page } from '../models/page.model';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class OwnerService {
  private api = inject(ApiService);

  getTrainers(page = 0, size = 10): Observable<Page<User>> {
    return this.api.get<Page<User>>('owner/trainers', { page, size });
  }

  getMembers(page = 0, size = 10): Observable<Page<User>> {
    return this.api.get<Page<User>>('owner/members', { page, size });
  }

  getRevenueSummary(): Observable<any> {
    return this.api.get<any>('owner/revenue');
  }

  createTrainer(trainerData: any): Observable<User> {
    return this.api.post<User>('owner/trainers', trainerData);
  }

  assignTrainerToGym(trainerId: string, gymId: string): Observable<User> {
    return this.api.post<User>(`owner/trainers/${trainerId}/assign-gym/${gymId}`, {});
  }

  deleteTrainer(trainerId: string): Observable<void> {
    return this.api.delete<void>(`owner/trainers/${trainerId}`);
  }
}

