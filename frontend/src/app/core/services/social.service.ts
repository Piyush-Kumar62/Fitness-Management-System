import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root',
})
export class SocialService {
  private api = inject(ApiService);

  followUser(userId: string): Observable<void> {
    return this.api.post<void>(`social/follow/${userId}`, {});
  }

  unfollowUser(userId: string): Observable<void> {
    return this.api.delete<void>(`social/unfollow/${userId}`);
  }

  getFollowers(userId: string): Observable<string[]> {
    return this.api.get<string[]>(`social/followers/${userId}`);
  }

  getFollowing(userId: string): Observable<string[]> {
    return this.api.get<string[]>(`social/following/${userId}`);
  }

  getFeed(page = 0, size = 20): Observable<any[]> {
    return this.api.get<any[]>('social/feed', { page, size });
  }
}
