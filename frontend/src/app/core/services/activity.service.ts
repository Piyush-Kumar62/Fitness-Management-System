import { Injectable, inject, signal } from '@angular/core';
import { Observable, finalize, map, tap } from 'rxjs';
import { ApiService } from './api.service';
import { Activity, CreateActivityRequest, ActivityType } from '../models/activity.model';
import { AuthService } from './auth.service';
import { PaginatedResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root',
})
export class ActivityService {
  private api = inject(ApiService);
  private auth = inject(AuthService);

  activities = signal<Activity[]>([]);
  isLoading = signal<boolean>(false);

  // Get all activities for current user
  getActivities(): Observable<Activity[]> {
    this.isLoading.set(true);
    return this.api.get<Activity[] | PaginatedResponse<Activity>>('activities').pipe(
      map((response) => this.extractActivities(response)),
      tap((activities) => {
        this.activities.set(activities);
      }),
      finalize(() => this.isLoading.set(false)),
    );
  }

  // Get all activities in the system (Admin only)
  getAllSystemActivities(page: number = 0, size: number = 20): Observable<any> {
    this.isLoading.set(true);
    const params = { page: page.toString(), size: size.toString() };
    return this.api.get<any>('activities/all', params).pipe(
      tap(() => {
        this.isLoading.set(false);
      }),
    );
  }

  // Get activity by ID
  getActivityById(id: string): Observable<Activity> {
    return this.api.get<Activity>(`activities/${id}`);
  }

  // Create new activity
  createActivity(activityData: CreateActivityRequest): Observable<Activity> {
    return this.api.post<Activity>('activities', activityData).pipe(
      tap((newActivity) => {
        this.activities.update((current) => [newActivity, ...current]);
      }),
    );
  }

  // Update activity
  updateActivity(id: string, activityData: Partial<Activity>): Observable<Activity> {
    return this.api.put<Activity>(`activities/${id}`, activityData).pipe(
      tap((updatedActivity) => {
        this.activities.update((current) =>
          current.map((activity) => (activity.id === id ? updatedActivity : activity)),
        );
      }),
    );
  }

  // Delete activity
  deleteActivity(id: string): Observable<void> {
    return this.api.delete<void>(`activities/${id}`).pipe(
      tap(() => {
        this.activities.update((current) => current.filter((activity) => activity.id !== id));
      }),
    );
  }

  // Get activity statistics
  getStatistics(): Observable<any> {
    return this.api.get<any>('activities/statistics');
  }

  // Filter activities by type
  filterByType(type: ActivityType): Activity[] {
    return this.activities().filter((activity) => activity.type === type);
  }

  // Search activities
  search(query: string): Activity[] {
    const lowerQuery = query.toLowerCase();
    return this.activities().filter((activity) => activity.type.toLowerCase().includes(lowerQuery));
  }

  // Get all activity types
  getActivityTypes(): { value: ActivityType; label: string }[] {
    return [
      { value: ActivityType.RUNNING, label: 'Running' },
      { value: ActivityType.WALKING, label: 'Walking' },
      { value: ActivityType.CYCLING, label: 'Cycling' },
      { value: ActivityType.SWIMMING, label: 'Swimming' },
      { value: ActivityType.YOGA, label: 'Yoga' },
      { value: ActivityType.GYM, label: 'Gym' },
      { value: ActivityType.SPORTS, label: 'Sports' },
      { value: ActivityType.OTHER, label: 'Other' },
    ];
  }

  private extractActivities(
    response: Activity[] | PaginatedResponse<Activity> | null | undefined,
  ): Activity[] {
    if (Array.isArray(response)) {
      return response;
    }

    if (response && Array.isArray(response.content)) {
      return response.content;
    }

    return [];
  }
}
