import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private api = inject(ApiService);

  getAdminStats(): Observable<any> {
    return this.api.get<any>('dashboard/admin');
  }

  getOwnerStats(): Observable<any> {
    return this.api.get<any>('dashboard/owner');
  }

  getTrainerStats(): Observable<any> {
    return this.api.get<any>('dashboard/trainer');
  }

  getMemberStats(): Observable<any> {
    return this.api.get<any>('dashboard/member');
  }
}
