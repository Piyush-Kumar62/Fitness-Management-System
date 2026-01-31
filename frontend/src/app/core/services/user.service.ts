import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { User } from '../models/user.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private api = inject(ApiService);

  /**
   * Get current user profile
   */
  getProfile(): Observable<User> {
    return this.api.get<User>('users/profile');
  }

  /**
   * Update user profile
   */
  updateProfile(userData: Partial<User>): Observable<User> {
    return this.api.put<User>('users/profile', userData);
  }

  /**
   * Change password
   */
  changePassword(currentPassword: string, newPassword: string): Observable<void> {
    return this.api.post<void>('users/change-password', {
      currentPassword,
      newPassword,
    });
  }

  /**
   * Get user by ID (admin only)
   */
  getUserById(userId: string): Observable<User> {
    return this.api.get<User>(`users/${userId}`);
  }

  /**
   * Get all users (admin only)
   */
  getAllUsers(page: number = 0, size: number = 10): Observable<Page<User>> {
    const params = { page: page.toString(), size: size.toString() };
    return this.api.get<Page<User>>('users', params);
  }

  /**
   * Search users by name or email
   */
  searchUsers(query: string, page: number = 0, size: number = 10): Observable<Page<User>> {
    const params = { query, page: page.toString(), size: size.toString() };
    return this.api.get<Page<User>>('users/search', params);
  }

  /**
   * Delete user (admin only)
   */
  deleteUser(userId: string): Observable<void> {
    return this.api.delete<void>(`users/${userId}`);
  }
}
