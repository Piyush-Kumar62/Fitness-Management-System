import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Page } from '../models/page.model';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class OwnerService {
  private api = inject(ApiService);

  getTrainers(page: number = 0, size: number = 10): Observable<Page<User>> {
    return this.api.get<Page<User>>('owner/trainers', { page, size });
  }

  getMembers(page: number = 0, size: number = 10): Observable<Page<User>> {
    return this.api.get<Page<User>>('owner/members', { page, size });
  }

  getRevenueSummary(): Observable<any> {
    return this.api.get<any>('owner/revenue');
  }
}

