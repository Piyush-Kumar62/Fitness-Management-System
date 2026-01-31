import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { ApiService } from './api.service';
import {
  Recommendation,
  CreateRecommendationRequest,
  GenerateRecommendationRequest,
} from '../models/recommendation.model';

@Injectable({
  providedIn: 'root',
})
export class RecommendationService {
  private api = inject(ApiService);

  recommendations = signal<Recommendation[]>([]);
  isLoading = signal<boolean>(false);

  /**
   * Generate AI recommendation for activity
   */
  generateRecommendation(request: GenerateRecommendationRequest): Observable<Recommendation> {
    this.isLoading.set(true);
    return this.api.post<Recommendation>('recommendations/generate', request).pipe(
      tap((recommendation) => {
        this.recommendations.update((current) => [recommendation, ...current]);
        this.isLoading.set(false);
      }),
    );
  }

  /**
   * Get recommendations for current user
   */
  getUserRecommendations(userId: string): Observable<Recommendation[]> {
    this.isLoading.set(true);
    return this.api.get<Recommendation[]>(`recommendations/user/${userId}`).pipe(
      tap((recommendations) => {
        this.recommendations.set(recommendations);
        this.isLoading.set(false);
      }),
    );
  }

  /**
   * Get recommendations for specific activity
   */
  getActivityRecommendations(activityId: string): Observable<Recommendation[]> {
    return this.api.get<Recommendation[]>(`recommendations/activity/${activityId}`);
  }

  /**
   * Get recommendation by ID
   */
  getRecommendationById(id: string): Observable<Recommendation> {
    return this.api.get<Recommendation>(`recommendations/${id}`);
  }

  /**
   * Create recommendation manually
   */
  createRecommendation(data: CreateRecommendationRequest): Observable<Recommendation> {
    return this.api.post<Recommendation>('recommendations', data).pipe(
      tap((recommendation) => {
        this.recommendations.update((current) => [recommendation, ...current]);
      }),
    );
  }

  /**
   * Delete recommendation
   */
  deleteRecommendation(id: string): Observable<void> {
    return this.api.delete<void>(`recommendations/${id}`).pipe(
      tap(() => {
        this.recommendations.update((current) => current.filter((rec) => rec.id !== id));
      }),
    );
  }

  /**
   * Search recommendations
   */
  search(query: string): Recommendation[] {
    const lowerQuery = query.toLowerCase();
    return this.recommendations().filter(
      (rec) =>
        rec.type.toLowerCase().includes(lowerQuery) ||
        rec.recommendation.toLowerCase().includes(lowerQuery),
    );
  }
}
