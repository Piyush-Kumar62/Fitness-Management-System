import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

interface HealthStatus {
  status: 'UP' | 'DOWN';
  components?: {
    [key: string]: {
      status: string;
      details?: any;
    };
  };
}

@Injectable({
  providedIn: 'root',
})
export class HealthService {
  private http = inject(HttpClient);

  /**
   * Check backend health status
   */
  checkHealth(): Observable<HealthStatus> {
    return this.http.get<HealthStatus>(`${environment.apiUrl}/actuator/health`);
  }

  /**
   * Check if backend is reachable
   */
  async isBackendHealthy(): Promise<boolean> {
    try {
      const health = await this.checkHealth().toPromise();
      return health?.status === 'UP';
    } catch (error) {
      console.error('Backend health check failed:', error);
      return false;
    }
  }
}
