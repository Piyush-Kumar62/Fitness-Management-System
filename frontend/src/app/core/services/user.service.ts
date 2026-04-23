import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ApiService } from './api.service';
import { User } from '../models/user.model';
import { Page } from '../models/page.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private api = inject(ApiService);
  private http = inject(HttpClient);
  private backendBaseUrl = environment.apiUrl.replace(/\/api(?:\/v\d+)?$/, '');

  // Get current user profile
  getProfile(): Observable<User> {
    return this.api.get<User>('users/profile');
  }

  // Update user profile
  updateProfile(userData: Partial<User>): Observable<User> {
    return this.api.put<User>('users/profile', userData);
  }

  // Change password
  changePassword(currentPassword: string, newPassword: string): Observable<void> {
    return this.api.post<void>('users/change-password', {
      currentPassword,
      newPassword,
    });
  }

  // Get user by ID (admin only)
  getUserById(userId: string): Observable<User> {
    return this.api.get<User>(`users/${userId}`);
  }

  // Get all users (admin only)
  getAllUsers(page: number = 0, size: number = 10): Observable<Page<User>> {
    const params = { page: page.toString(), size: size.toString() };
    return this.api.get<Page<User>>('users', params);
  }

  // Search users by name or email
  searchUsers(query: string, page: number = 0, size: number = 10): Observable<Page<User>> {
    const params = { query, page: page.toString(), size: size.toString() };
    return this.api.get<Page<User>>('users/search', params);
  }

  // Create user (admin only)
  createUser(userData: Partial<User>): Observable<User> {
    return this.api.post<User>('users', userData);
  }

  // Update user by ID (admin only)
  updateUserById(userId: string, userData: Partial<User> & { password?: string; active?: boolean }): Observable<User> {
    return this.api.put<User>(`users/${userId}`, userData);
  }

  // Delete user (admin only)
  deleteUser(userId: string): Observable<void> {
    return this.api.delete<void>(`users/${userId}`);
  }

  // Upload profile image
  uploadProfileImage(file: File): Observable<User> {
    const formData = new FormData();
    formData.append('file', file);
    return this.api.post<User>('users/profile/image', formData);
  }

  // Delete profile image
  deleteProfileImage(): Observable<User> {
    return this.api.delete<User>('users/profile/image');
  }

  getProfileImageBlobUrl(profileImageUrl?: string): Observable<string | null> {
    const resolvedUrl = this.resolveFileUrl(profileImageUrl);
    if (!resolvedUrl) {
      return of(null);
    }

    return this.http.get(resolvedUrl, { responseType: 'blob' }).pipe(
      map((blob) => URL.createObjectURL(blob)),
      catchError(() => of(null)),
    );
  }

  private resolveFileUrl(profileImageUrl?: string): string | null {
    if (!profileImageUrl || !profileImageUrl.trim()) {
      return null;
    }

    if (/^https?:\/\//i.test(profileImageUrl)) {
      return profileImageUrl;
    }

    return `${this.backendBaseUrl}${profileImageUrl.startsWith('/') ? '' : '/'}${profileImageUrl}`;
  }
}
